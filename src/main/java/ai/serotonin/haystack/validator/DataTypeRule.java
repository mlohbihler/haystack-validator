/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package ai.serotonin.haystack.validator;

import java.util.ArrayList;
import java.util.List;

import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HValue;

/**
 * Ensures the data type of the given tag, if present on the row, is one of the given types.
 * 
 * @author Matthew
 */
public class DataTypeRule extends Rule {
    private final String tag;
    private final List<Class<? extends HValue>> types;

    @SafeVarargs
    public DataTypeRule(String tag, Class<? extends HValue>... types) {
        this.tag = tag;
        this.types = new ArrayList<>(types.length);
        for (Class<? extends HValue> type : types)
            this.types.add(type);
    }

    public DataTypeRule(String tag, List<Class<? extends HValue>> types) {
        this.tag = tag;
        this.types = types;
    }

    @Override
    public boolean test(HMap map, List<HMap> rows) {
        HValue value = map.get(tag);
        if (value == null)
            return true;

        for (Class<?> type : types) {
            if (type.isAssignableFrom(value.getClass()))
                return true;
        }

        return false;
    }

    @Override
    public String getMessage(HMap map, List<HMap> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("The tag '").append(tag).append("' has an invalid data type. Expected ");

        if (types.size() == 1)
            sb.append(getName(types.get(0)));
        else {
            sb.append("one of ");
            boolean first = true;
            for (Class<? extends HValue> type : types) {
                if (first)
                    first = false;
                else
                    sb.append(",");
                sb.append(getName(type));
            }
        }

        sb.append(" but was ").append(getName(map.get(tag).getClass()));

        return sb.toString();
    }

    private String getName(Class<? extends HValue> type) {
        String name = type.getSimpleName();
        return name.substring(1).toLowerCase();
    }
}
