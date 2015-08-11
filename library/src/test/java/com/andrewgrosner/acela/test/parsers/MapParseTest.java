package com.andrewgrosner.acela.test.parsers;

import com.andrewgrosner.acela.Acela;
import com.fasterxml.jackson.core.JsonGenerator;
import com.andrewgrosner.acela.test.SimpleObject;
import com.andrewgrosner.acela.test.TestingUtils;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Description:
 */
public class MapParseTest {

    @Test
    public void testMapParse() throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = Acela.JSON_FACTORY.createGenerator(stringWriter);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("firstArray");
        TestingUtils.writeSimpleObject(jsonGenerator);
        jsonGenerator.writeFieldName("secondArray");
        TestingUtils.writeSimpleObject(jsonGenerator);
        jsonGenerator.writeEndObject();

        jsonGenerator.close();

        Map<String, SimpleObject> simpleObjectMap = Acela.getTranslator(SimpleObject.class).parseMap(stringWriter.toString());
        assertNotNull(simpleObjectMap);
        assertEquals(2, simpleObjectMap.size());
        assertNotNull(simpleObjectMap.get("firstArray"));
        assertNotNull(simpleObjectMap.get("secondArray"));

        String jsonString = stringWriter.toString();
        String generatedString = Acela.getTranslator(SimpleObject.class).serializeMap(simpleObjectMap);
        assertEquals(jsonString, generatedString);
    }
}
