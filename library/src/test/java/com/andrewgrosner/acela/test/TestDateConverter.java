package com.andrewgrosner.acela.test;

import com.andrewgrosner.acela.JsonGeneratorWrapper;
import com.andrewgrosner.acela.JsonParserWrapper;
import com.andrewgrosner.acela.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Description:
 */
@com.andrewgrosner.acela.annotation.TypeConverter
public class TestDateConverter implements TypeConverter<Date, String> {

    static DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

    @Override
    public Date parse(JsonParserWrapper jsonParser, String defValue) {
        try {
            String value = jsonParser.getValueAsString(defValue);
            return DATE_FORMAT.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public void serialize(JsonGeneratorWrapper wrapper, String fieldName, Date value, String defValue) {
        if(value != null) {
            wrapper.writeStringField(fieldName, DATE_FORMAT.format(value));
        } else {
            wrapper.writeStringField(fieldName, defValue);
        }
    }
}
