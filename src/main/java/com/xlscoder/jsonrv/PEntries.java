package com.xlscoder.jsonrv;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.HashMap;

@JsonDeserialize(contentUsing = CustomDeserializer.class)
public class PEntries extends HashMap<String, PEntry> {
}
