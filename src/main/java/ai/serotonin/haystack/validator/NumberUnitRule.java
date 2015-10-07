/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package ai.serotonin.haystack.validator;

import java.util.List;

import org.brickhouse.datatype.HMap;

/**
 * The given tag must have a number value with one of the the given units.
 * 
 * @author Matthew
 */
public class NumberUnitRule extends Rule {
    private final String tag;
    private final List<String> units;

    public NumberUnitRule(String tag, List<String> units) {
        this.tag = tag;
        this.units = units;
    }

    @Override
    public boolean test(HMap map, List<HMap> rows) {
        if (map.has(tag)) {
            String tagUnit = map.getNumber(tag).getUnit();
            return units.contains(tagUnit);
        }
        return true;
    }

    @Override
    public String getMessage(HMap map, List<HMap> rows) {
        StringBuilder sb = new StringBuilder();

        sb.append("The unit of tag '").append(tag).append("' must be ");

        for (String unit : units)
            sb.append(unit).append(",");

        sb.append(" but is ").append(map.getNumber(tag).getUnit());

        return sb.toString();
    }
}
