package com.andrewgrosner.acela.test.mergeable;

import com.andrewgrosner.acela.Acela;
import com.fasterxml.jackson.core.JsonGenerator;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Description:
 */
public class MergeableObjectTest {

    @Test
    public void testMergeableObject() throws IOException {

        MergeableObject mergeableObject = new MergeableObject();
        mergeableObject.name = "Name";
        mergeableObject.date = new Date();
        mergeableObject.notMergeable = "NotMergeable";

        StringWriter writer = new StringWriter();
        JsonGenerator generator = Acela.JSON_FACTORY.createGenerator(writer);

        Date newDate = new Date();
        generator.writeStartObject();
        generator.writeStringField("name", null);
        generator.writeNumberField("date", newDate.getTime());
        generator.writeStringField("notMergeable", "Merged??");
        generator.writeEndObject();

        String json = writer.toString();
        generator.close();

        Acela.getTranslator(MergeableObject.class).parse(mergeableObject, json);
        assertEquals(mergeableObject.name, "Name");
        assertEquals(mergeableObject.date.toString(), newDate.toString());
        assertNotEquals(mergeableObject.notMergeable, "Merged??");
    }
}
