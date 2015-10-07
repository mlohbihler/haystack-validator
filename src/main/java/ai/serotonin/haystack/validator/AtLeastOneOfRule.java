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
 * Given a set of tags, at least one them must appear on the row. Multiple of the tags on the row is allowed.
 * 
 * @author Matthew
 */
public class AtLeastOneOfRule extends Rule {
    private final List<String> tags;

    public AtLeastOneOfRule(String... tags) {
        if (tags.length == 0)
            throw new RuntimeException("The AtLeastOneOf rule requires at least one tag");

        this.tags = new ArrayList<>();
        for (String tag : tags)
            this.tags.add(tag);
    }

    public AtLeastOneOfRule(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean test(HMap map, List<HMap> rows) {
        for (String tag : tags) {
            if (map.has(tag))
                return true;
        }
        return false;
    }

    @Override
    public String getMessage(HMap map, List<HMap> rows) {
        StringBuilder sb = new StringBuilder();

        if (tags.size() == 1)
            sb.append("The tag '").append(tags.get(0)).append("' is missing");
        else {
            sb.append("Expected one of ");

            boolean first = true;
            for (String tag : tags) {
                if (first)
                    first = false;
                else
                    sb.append(",");
                sb.append(tag);
            }

            sb.append(" but none were found");
        }

        return sb.toString();
    }
}
