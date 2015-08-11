package com.andrewgrosner.acela.app.model;

import android.text.TextUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.andrewgrosner.acela.JsonGeneratorWrapper;
import com.andrewgrosner.acela.JsonParserWrapper;
import com.andrewgrosner.acela.TypeConverter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Description:
 */
@com.andrewgrosner.acela.annotation.TypeConverter
public class SimpleDateParser implements TypeConverter<Date, String>, com.bluelinelabs.logansquare.typeconverters.TypeConverter<Date> {

    static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    public Date parse(JsonParserWrapper jsonParser, String defValue) {
        try {
            String value = jsonParser.getValueAsString(defValue);
            if(!TextUtils.isEmpty(value)) {
                return FORMAT.parse(value);
            } else {
                return null;
            }
        } catch (ParseException e) {
        }
        return null;
    }

    @Override
    public void serialize(JsonGeneratorWrapper wrapper, String fieldName, Date value, String defValue) {
        if (value != null) {
            wrapper.writeStringField(fieldName, FORMAT.format(value));
        } else {
            wrapper.writeStringField(fieldName, defValue);
        }
    }

    @Override
    public Date parse(JsonParser jsonParser) throws IOException {
        try {
            String value = jsonParser.getValueAsString(null);
            if(!TextUtils.isEmpty(value)) {
                return FORMAT.parse(value);
            } else {
                return null;
            }
        } catch (ParseException e) {
        }
        return null;
    }

    @Override
    public void serialize(Date object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) throws IOException {
        if (object != null) {
            jsonGenerator.writeStringField(fieldName, FORMAT.format(object));
        } else {
            jsonGenerator.writeStringField(fieldName, null);
        }
    }
}
