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
import org.brickhouse.datatype.HString;
import org.brickhouse.datatype.HValue;

/**
 * Finds rows that have a tz field, and verifies that references that also have a tz have the same value.
 * 
 * @author Matthew
 */
public class TimezoneDenormalizationRule extends Rule {
    @Override
    public boolean test(HMap map, List<HMap> rows) {
        HString tz = map.get("tz");
        if (tz != null) {
            // Look at the rows that are referenced by this row, and verify that if they have a tz it is the same.
            for (Entry<String, HValue> e : map.entrySet()) {
                if (e.getValue() instanceof HReference) {
                    HMap row = findRow(rows, (HReference) e.getValue());
                    if (row != null) {
                        HString thatTz = row.get("tz");
                        if (thatTz != null && !thatTz.equals(tz))
                            return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String getMessage(HMap map, List<HMap> rows) {
        return "Row has a tz value that doesn't match that of at least one reference.";
    }
}
