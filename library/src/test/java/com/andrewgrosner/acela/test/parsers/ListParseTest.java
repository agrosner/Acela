package com.andrewgrosner.acela.test.parsers;

import com.andrewgrosner.acela.Acela;
import com.andrewgrosner.acela.test.SimpleObject;
import com.andrewgrosner.acela.test.TestingUtils;
import com.fasterxml.jackson.core.JsonGenerator;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Description:
 */
public class ListParseTest {

    @Test
    public void testListParse() throws IOException {

        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = Acela.JSON_FACTORY.createGenerator(stringWriter);

        TestingUtils.writeTestArray(jsonGenerator, null);

        jsonGenerator.close();

        List<SimpleObject> simpleObjects = Acela.getTranslator(SimpleObject.class).parseList(stringWriter.toString());
        assertNotNull(simpleObjects);
        assertEquals(2, simpleObjects.size());

        String generatedString = Acela.getTranslator(SimpleObject.class).serializeList(simpleObjects);
        String jsonString = stringWriter.toString();
        assertEquals(jsonString, generatedString);
    }
}
