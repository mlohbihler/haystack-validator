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

public class Test {
    public static void main(String[] args) throws Exception {
        // Normally you would only run one of the following methods.
        remote();
        //        local();
    }

    static void remote() throws Exception {
        validate(Source.remote("http://localhost:85/api/demo/"));
    }

    static void local() throws Exception {
        validate(Source.diffs("data/proj.diffs"));
    }

    static void validate(List<HMap> rows) throws Exception {
        List<Rule> rules = Rule.loadRules();

        for (HMap row : rows) {
            for (Rule rule : rules) {
                if (!rule.isOk(row, rows)) {
                    Source.clean(row);
                    System.out.println(rule.getMessage(row, rows) + ": " + row);
                }
            }
        }
    }
}
