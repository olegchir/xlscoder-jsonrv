package com.xlscoder.jsonrv;

import java.util.List;

public class RId {
    public String id;
    public List<String> data;

    public RId() {
    }

    public RId(String id, List<String> data) {
        this.id = id;
        this.data = data;
    }
}
