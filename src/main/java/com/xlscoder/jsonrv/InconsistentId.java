package com.xlscoder.jsonrv;

public class InconsistentId {
    public String key;
    public String file;
    public int position;
    public int size;

    public InconsistentId(String key, String file, int position, int size) {
        this.key = key;
        this.file = file;
        this.position = position;
        this.size = size;
    }
}
