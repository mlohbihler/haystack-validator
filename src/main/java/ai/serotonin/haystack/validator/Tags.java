/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package ai.serotonin.haystack.validator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.brickhouse.datatype.HBinary;
import org.brickhouse.datatype.HBoolean;
import org.brickhouse.datatype.HCoordinates;
import org.brickhouse.datatype.HDate;
import org.brickhouse.datatype.HDateTime;
import org.brickhouse.datatype.HMarker;
import org.brickhouse.datatype.HNumber;
import org.brickhouse.datatype.HReference;
import org.brickhouse.datatype.HString;
import org.brickhouse.datatype.HTime;
import org.brickhouse.datatype.HUri;
import org.brickhouse.datatype.HValue;

public class Tags {
    public static Set<String> getTags() throws Exception {
        Set<String> tags = new HashSet<>();

        InputStream is = Tags.class.getResourceAsStream("/tags.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = in.readLine()) != null)
                tags.add(line);
        }
        finally {
            in.close();
        }

        return tags;
    }

    public static enum TagType {
        binary("binary", HBinary.class), //
        bool("boolean", HBoolean.class), //
        coordinates("coordinates", HCoordinates.class), //
        date("date", HDate.class), //
        datetime("datetime", HDateTime.class), //
        marker("marker", HMarker.class), //
        number("number", HNumber.class), //
        reference("reference", HReference.class), //
        string("string", HString.class), //
        time("time", HTime.class), //
        uri("uri", HUri.class);

        public final String description;
        public final Class<? extends HValue> clazz;

        private TagType(String description, Class<? extends HValue> clazz) {
            this.description = description;
            this.clazz = clazz;
        }

        public static TagType forDescription(String description) {
            for (TagType type : values()) {
                if (type.description.equals(description))
                    return type;
            }
            throw new NullPointerException("No tag description: " + description);
        }
    }
}
