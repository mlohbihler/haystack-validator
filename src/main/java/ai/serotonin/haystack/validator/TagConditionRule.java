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
import org.brickhouse.filter.Filter;

/**
 * A generic rule where if a given tag is present, the given row condition must also be true.
 * 
 * @author Matthew
 */
public class TagConditionRule extends Rule {
    private final String tag;
    private final Filter filter;

    public TagConditionRule(String tag, Filter filter) {
        this.tag = tag;
        this.filter = filter;
    }

    @Override
    public boolean test(HMap map, List<HMap> rows) {
        if (map.has(tag))
            return filter.include(map, createPather(rows));
        return true;
    }

    @Override
    public String getMessage(HMap map, List<HMap> rows) {
        return "Row contains '" + tag + "', but does not satisfy '" + filter + "'";
    }
}
