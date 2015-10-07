/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package ai.serotonin.haystack.validator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HReference;
import org.brickhouse.datatype.HValue;
import org.brickhouse.filter.Filter;
import org.brickhouse.filter.Filter.Pather;

import com.serotonin.json.type.JsonArray;
import com.serotonin.json.type.JsonObject;
import com.serotonin.json.type.JsonTypeReader;
import com.serotonin.json.type.JsonValue;

/**
 * Base class for all rules. Also defines how rules definitions are loaded from file.
 * 
 * @author Matthew
 */
abstract public class Rule {
    public static List<Rule> loadRules() {
        List<Rule> rules = new ArrayList<>();

        InputStream is = null;
        try {
            is = Rule.class.getResourceAsStream("/rules.json");
            JsonTypeReader in = new JsonTypeReader(new InputStreamReader(is, "UTF-8"));
            JsonArray arr = in.read().toJsonArray();
            for (JsonValue v : arr) {
                JsonObject o = v.toJsonObject();

                String type = o.getString("ruleType");
                Rule rule;

                try {
                    if ("dataType".equals(type)) {
                        String tag = o.getString("tag");
                        JsonArray dataTypes = o.getJsonArray("dataTypes");
                        List<Class<? extends HValue>> types = new ArrayList<>(dataTypes.size());
                        for (JsonValue dataType : dataTypes)
                            types.add(Tags.TagType.forDescription(dataType.toString()).clazz);
                        rule = new DataTypeRule(tag, types);
                    }
                    else if ("onlyOneOf".equals(type)) {
                        List<String> tags = arrayToList(o.getJsonArray("tags"));
                        rule = new OnlyOneOfRule(tags);
                    }
                    else if ("atLeastOneOf".equals(type)) {
                        List<String> tags = arrayToList(o.getJsonArray("tags"));
                        rule = new AtLeastOneOfRule(tags);
                    }
                    else if ("noOrphans".equals(type)) {
                        rule = new NoOrphansRule();
                    }
                    else if ("tagCondition".equals(type)) {
                        String tag = o.getString("tag");
                        String filter = o.getString("filter");
                        rule = new TagConditionRule(tag, Filter.parse(filter));
                    }
                    else if ("numberUnit".equals(type)) {
                        String tag = o.getString("tag");
                        List<String> units = arrayToList(o.getJsonArray("units"));
                        rule = new NumberUnitRule(tag, units);
                    }
                    else if ("timezoneDenormalization".equals(type)) {
                        rule = new TimezoneDenormalizationRule();
                    }
                    else if (type == null)
                        rule = null;
                    else
                        throw new RuntimeException("Unknown rule type: " + type);

                    if (rule != null) {
                        String condStr = o.getString("condition");
                        if (condStr != null)
                            rule.setCondition(Filter.parse(condStr));

                        String levelStr = o.getString("level");
                        if (levelStr != null)
                            rule.setLevel(Level.valueOf(levelStr));

                        rules.add(rule);
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException("Failed to create rule from " + o, e);
                }
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (is != null)
                    is.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return rules;
    }

    private static List<String> arrayToList(JsonArray arr) {
        List<String> list = new ArrayList<>(arr.size());
        for (JsonValue tag : arr)
            list.add(tag.toString());
        return list;
    }

    private Filter condition;
    private Level level = Level.error;

    public Filter getCondition() {
        return condition;
    }

    public void setCondition(Filter condition) {
        this.condition = condition;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public boolean isOk(HMap map, List<HMap> rows) {
        if (condition != null) {
            if (!condition.include(map, createPather(rows)))
                // The condition is not satisfied, so don't evaluate the row.
                return true;
        }

        return test(map, rows);
    }

    /**
     * Returns true if the row passed the test, i.e. is valid.
     */

    abstract protected boolean test(HMap map, List<HMap> rows);

    abstract public String getMessage(HMap map, List<HMap> rows);

    protected HMap findRow(List<HMap> rows, HReference id) {
        return findRow(rows, id.getId());
    }

    protected HMap findRow(List<HMap> rows, String id) {
        for (HMap row : rows) {
            HReference rowId = row.id();
            if (id != null && rowId.getId().equals(id))
                return row;
        }
        return null;
    }

    protected Pather createPather(final List<HMap> rows) {
        return new Pather() {
            @Override
            public HMap find(String ref) {
                return findRow(rows, ref);
            }
        };
    }
}
