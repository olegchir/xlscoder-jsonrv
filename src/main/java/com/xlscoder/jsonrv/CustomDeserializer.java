package com.xlscoder.jsonrv;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CustomDeserializer extends JsonDeserializer<PEntry> {

    @Override
    public PEntry deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        JsonNode node = jsonParser.readValueAsTree();
        ObjectMapper mapper = new ObjectMapper();
        PEntry result = new PEntry();
        result.setNumberOfConferences(mapper.readValue(node.get(0).toString(), Integer.class));
        result.setFiles(mapper.readValue(node.get(1).toString(), List.class));
        List ids = mapper.readValue(node.get(2).toString(), List.class);
        Object idsAsString = ids.stream().map(Object::toString).collect(Collectors.toList());
        result.setIds((List<String>)idsAsString);
//        System.out.println(result.getNumberOfConferences()+",");
        return result;
    }
}