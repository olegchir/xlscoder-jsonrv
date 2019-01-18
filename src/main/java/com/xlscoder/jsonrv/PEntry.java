package com.xlscoder.jsonrv;

import java.util.ArrayList;
import java.util.List;

public class PEntry {
    public String key;
    public int numberOfConferences;
    public List<String> files;
    public List<String> ids;

    public int getNumberOfConferences() {
        return numberOfConferences;
    }

    public void setNumberOfConferences(int numberOfConferences) {
        this.numberOfConferences = numberOfConferences;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
