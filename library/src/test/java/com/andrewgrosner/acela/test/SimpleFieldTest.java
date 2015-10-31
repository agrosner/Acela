package com.andrewgrosner.acela.test;

import com.andrewgrosner.acela.Acela;
import com.fasterxml.jackson.core.JsonGenerator;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Description:
 */
public class SimpleFieldTest {

    @Test
    public void testSimpleClass() throws IOException, ParseException {

        SimpleObject simpleObject;

        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = Acela.JSON_FACTORY.createGenerator(stringWriter);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("name");
        jsonGenerator.writeString("Test");

        jsonGenerator.writeFieldName("isSet");
        jsonGenerator.writeBoolean(true);

        jsonGenerator.writeFieldName("date");
        jsonGenerator.writeString("05/24/1990");

        jsonGenerator.writeEndObject();
        jsonGenerator.close();

        String jsonString = stringWriter.toString();
        simpleObject = Acela.getTranslator(SimpleObject.class).parse(jsonString);
        assertCompleteSimpleObject(simpleObject);

        String generatedString = Acela.getTranslator(SimpleObject.class).serialize(simpleObject);
        simpleObject = Acela.getTranslator(SimpleObject.class).parse(generatedString);
        assertCompleteSimpleObject(simpleObject);

    }

    private void assertCompleteSimpleObject(SimpleObject simpleObject) throws ParseException {
        assertNotNull(simpleObject.name);
        assertEquals("Test", simpleObject.name);

        assertTrue(simpleObject.isSet);

        assertNotNull(simpleObject.date);

        Date date = TestDateConverter.DATE_FORMAT.parse("05/24/1990");
        assertEquals(date, simpleObject.date);
    }
}
