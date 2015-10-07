/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package ai.serotonin.haystack.validator;

import java.util.List;
import java.util.Map.Entry;

import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HReference;
import org.brickhouse.datatype.HValue;

/**
 * All references on the row must point to a row in the database (possibly the same row).
 * 
 * @author Matthew
 */
public class NoOrphansRule extends Rule {
    @Override
    public boolean test(HMap map, List<HMap> rows) {
        for (Entry<String, HValue> e : map.entrySet()) {
            if (e.getValue() instanceof HReference) {
                HMap row = findRow(rows, (HReference) e.getValue());
                if (row == null)
                    return false;
            }
        }
        return true;
    }

    @Override
    public String getMessage(HMap map, List<HMap> rows) {
        StringBuilder sb = new StringBuilder();

        sb.append("Row contains orphaned references at ");

        boolean first = true;
        for (Entry<String, HValue> e : map.entrySet()) {
            if (e.getValue() instanceof HReference) {
                HMap row = findRow(rows, (HReference) e.getValue());
                if (row == null) {
                    if (first)
                        first = false;
                    else
                        sb.append(",");
                    sb.append(e.getKey());
                }
            }
        }

        return sb.toString();
    }
}
