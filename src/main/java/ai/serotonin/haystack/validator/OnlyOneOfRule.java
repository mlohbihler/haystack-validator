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

/**
 * The given tags are exclusive, but not required, on the row. I.e., it is valid for none of the tags to appear,
 * but if one does, the others cannot.
 * 
 * @author Matthew
 */
public class OnlyOneOfRule extends Rule {
    private final List<String> tags;

    public OnlyOneOfRule(String... tags) {
        this.tags = new ArrayList<>();
        for (String tag : tags)
            this.tags.add(tag);
    }

    public OnlyOneOfRule(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean test(HMap map, List<HMap> rows) {
        boolean found = false;
        for (String tag : tags) {
            if (map.has(tag)) {
                if (!found)
                    found = true;
                else
                    return false;
            }
        }
        return true;
    }

    @Override
    public String getMessage(HMap map, List<HMap> rows) {
        StringBuilder sb = new StringBuilder();

        sb.append("Only one of ");

        boolean first = true;
        for (String tag : tags) {
            if (map.has(tag)) {
                if (first)
                    first = false;
                else
                    sb.append(",");
                sb.append(tag);
            }
        }

        sb.append(" is allowed");

        return sb.toString();
    }
}
