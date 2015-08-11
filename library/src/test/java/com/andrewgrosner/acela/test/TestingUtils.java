package com.andrewgrosner.acela.test;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 */
public class TestingUtils {

    public static void writeTestArray(JsonGenerator jsonGenerator, String fieldName) throws IOException {
        if(fieldName != null) {
            jsonGenerator.writeFieldName(fieldName);
        }
        jsonGenerator.writeStartArray();
        writeSimpleObject(jsonGenerator);
        writeSimpleObject(jsonGenerator);
        jsonGenerator.writeEndArray();
    }

    public static List<SimpleObject> getSimpleObjectList(int size) throws ParseException {
        List<SimpleObject> simpleObjects = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            simpleObjects.add(getSimpleObject());
        }
        return simpleObjects;
    }

    public static SimpleObject[] getSimpleObjectArray(int size) throws ParseException {
        return getSimpleObjectList(size).toArray(new SimpleObject[size]);
    }

    public static void writeTestMap(JsonGenerator jsonGenerator, String fieldName) throws IOException {
        jsonGenerator.writeFieldName(fieldName);
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("inner1");
        writeSimpleObject(jsonGenerator);
        jsonGenerator.writeFieldName("inner2");
        writeSimpleObject(jsonGenerator);
        jsonGenerator.writeEndObject();
    }

    public static Map<String, SimpleObject> getSimpleTestMap() throws ParseException {
        Map<String, SimpleObject> stringSimpleObjectMap = new HashMap<>();
        stringSimpleObjectMap.put("inner1", getSimpleObject());
        stringSimpleObjectMap.put("inner2", getSimpleObject());
        return stringSimpleObjectMap;
    }

    public static void writeSimpleObject(JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("date", "05/24/1990");
        jsonGenerator.writeBooleanField("isSet", true);
        jsonGenerator.writeStringField("name", "Inner1");
        jsonGenerator.writeEndObject();
    }

    public static SimpleObject getSimpleObject() throws ParseException {
        SimpleObject simpleObject = new SimpleObject();
        simpleObject.name = "Inner1";
        simpleObject.date = TestDateConverter.DATE_FORMAT.parse("05/24/1990");
        simpleObject.isSet = true;
        return simpleObject;
    }
}
