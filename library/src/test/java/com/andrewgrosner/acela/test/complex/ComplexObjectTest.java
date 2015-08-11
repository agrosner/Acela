package com.andrewgrosner.acela.test.complex;

import com.fasterxml.jackson.core.JsonGenerator;
import com.andrewgrosner.acela.Acela;
import com.andrewgrosner.acela.test.TestingUtils;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Description:
 */
public class ComplexObjectTest {

    @Test
    public void testComplexJsonModel() throws IOException, ParseException {

        ComplexObject complexObject = new ComplexObject();
        complexObject.name = "Test";
        complexObject.simpleObjectList = TestingUtils.getSimpleObjectList(2);
        complexObject.simpleObjectArray = TestingUtils.getSimpleObjectArray(2);
        complexObject.simpleObjectMap = TestingUtils.getSimpleTestMap();

        String output = Acela.getTranslator(ComplexObject.class).serialize(complexObject);
        for (int i = 0; i < complexObject.fieldsCalled.length; i++) {
            assertTrue(complexObject.fieldsCalled[i]);
        }

        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = Acela.JSON_FACTORY.createGenerator(stringWriter);

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("somethingElse", "Test");
        jsonGenerator.writeStringField("handle", "This is handled");
        jsonGenerator.writeStringField("manualKey", "This will be handled individually");

        TestingUtils.writeTestArray(jsonGenerator, "simpleObjectList");
        TestingUtils.writeTestArray(jsonGenerator, "simpleObjectArray");
        TestingUtils.writeTestMap(jsonGenerator, "simpleObjectMap");

        jsonGenerator.writeEndObject();

        jsonGenerator.close();

        String jsonString = stringWriter.toString();

        complexObject = Acela.getTranslator(ComplexObject.class).parse(output);
        assertNotNull(complexObject.name);

        assertNotNull(complexObject.simpleObjectList);
        assertEquals(complexObject.simpleObjectList.size(), 2);

        assertNotNull(complexObject.simpleObjectArray);
        assertEquals(complexObject.simpleObjectArray.length, 2);

        assertNotNull(complexObject.simpleObjectMap);
        assertEquals(complexObject.simpleObjectMap.size(), 2);
        assertNotNull(complexObject.simpleObjectMap.get("inner1"));
        assertNotNull(complexObject.simpleObjectMap.get("inner2"));

        complexObject = Acela.getTranslator(ComplexObject.class).parse(jsonString);

        assertNotNull(complexObject.handledManually);
        assertEquals(complexObject.handledManually, "This is handled");

        assertTrue(complexObject.hasManualKey);

    }

}
