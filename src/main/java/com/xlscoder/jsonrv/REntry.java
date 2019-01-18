package com.xlscoder.jsonrv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class REntry {
        public String key;
        public int numberOfConferences;
        public List<String> files;
        public List<RId> ids;

        public static REntry createUnitialized(PEntry pEntry) {
            REntry result = new REntry();
            result.key = pEntry.key;
            result.numberOfConferences = pEntry.numberOfConferences;
            result.files = new ArrayList<>(pEntry.files);

            result.ids = pEntry.ids.stream()
                    .map(id -> new RId(id, new ArrayList<>()))
                    .collect(Collectors.toList());

            return result;
        }
}
