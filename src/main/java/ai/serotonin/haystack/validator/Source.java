/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package ai.serotonin.haystack.validator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.brickhouse.datatype.HGrid;
import org.brickhouse.datatype.HMap;
import org.brickhouse.datatype.HReference;
import org.brickhouse.zinc.ZincReader;
import org.brickhouse.zinc.ZincWriter;

import com.serotonin.web.http.HttpUtils4;

public class Source {
    /**
     * Read a local diffs file.
     * 
     * @param filename
     * @return the list of rows found in the file.
     * @throws Exception
     */
    public static List<HMap> diffs(String filename) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(filename));

        String line;
        List<HMap> rows = new ArrayList<>();
        while ((line = in.readLine()) != null) {
            if (line.startsWith("#"))
                // Comment. Skip.
                continue;

            if (line.startsWith("+"))
                rows.add(parseLine(line));
            else if (line.startsWith("^")) {
                HMap map = parseLine(line);

                // Find the existing row
                HReference id = map.id();
                for (HMap row : rows) {
                    if (row.id().equals(id)) {
                        row.merge(map);
                        break;
                    }
                }
            }
            else {
                System.out.println("Unknown line operation: " + line.charAt(0));
                continue;
            }
        }

        in.close();

        return rows;
    }

    private static HMap parseLine(String line) {
        line = line.substring(2, line.length() - 1);
        return new ZincReader(line).readDiff();
    }

    /**
     * Read a remote database via the Project-Haystack protocol.
     * 
     * This method currently does not support authentication.
     * 
     * @param endpoint
     * @return the list of rows returned
     * @throws Exception
     */
    public static List<HMap> remote(String endpoint) throws Exception {
        String filter = "id";
        //int limit = 10000;

        HMap map = new HMap().put("filter", filter); //.put("limit", limit);
        String entityStr = ZincWriter.gridToString(new HGrid(map));

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(endpoint + "read");
        // Set the auth as required.
        StringEntity entity = new StringEntity(entityStr, ContentType.TEXT_PLAIN);
        post.setEntity(entity);

        String responseStr = HttpUtils4.getTextContent(client, post, 1);
        HGrid response = new ZincReader(responseStr).readGrid();
        return response.getRows();
    }

    public static void clean(List<HMap> rows) throws Exception {
        Set<String> tags = Tags.getTags();

        // Dump the fields we're not interested in.
        List<HMap> rowDump = new ArrayList<>();
        for (HMap row : rows) {
            Set<String> keyDump = new HashSet<>();
            for (String key : row.keySet()) {
                if (!tags.contains(key))
                    keyDump.add(key);
            }
            for (String key : keyDump)
                row.delete(key);
            row.delete("id");
            row.delete("dis");

            if (row.isEmpty())
                rowDump.add(row);
        }

        rows.removeAll(rowDump);
    }

    /**
     * Remove all tags that are not in the Project-Haystack spec.
     * 
     * @param row
     * @throws Exception
     */
    public static void clean(HMap row) throws Exception {
        Set<String> tags = Tags.getTags();

        Set<String> keyDump = new HashSet<>();
        for (String key : row.keySet()) {
            if (!tags.contains(key))
                keyDump.add(key);
        }
        for (String key : keyDump)
            row.delete(key);
    }
}
