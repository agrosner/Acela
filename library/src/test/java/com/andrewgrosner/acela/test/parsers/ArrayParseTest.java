package com.andrewgrosner.acela.test.parsers;

import com.andrewgrosner.acela.Acela;
import com.andrewgrosner.acela.test.SimpleObject;
import com.andrewgrosner.acela.test.TestingUtils;
import com.fasterxml.jackson.core.JsonGenerator;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Description:
 */
public class ArrayParseTest {

    @Test
    public void testArrayParse() throws IOException {

        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = Acela.JSON_FACTORY.createGenerator(stringWriter);

        TestingUtils.writeTestArray(jsonGenerator, null);

        jsonGenerator.close();

        SimpleObject[] simpleObjects = Acela.getTranslator(SimpleObject.class).parseArray(stringWriter.toString());
        assertNotNull(simpleObjects);
        assertEquals(2, simpleObjects.length);

        String jsonString = stringWriter.toString();
        String generatedString = Acela.getTranslator(SimpleObject.class).serializeArray(simpleObjects);
        assertEquals(jsonString, generatedString);
    }
}
