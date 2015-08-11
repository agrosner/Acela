package com.andrewgrosner.acela.converter;

import com.andrewgrosner.acela.JsonGeneratorWrapper;
import com.andrewgrosner.acela.JsonParserWrapper;
import com.andrewgrosner.acela.TypeConverter;

import java.util.Date;

/**
 * Description: Handles, by default, {@link Date} fields that represent {@link Long} data underneath.
 * Be warned that combining this with {@link BaseStringDateConverter} could result in one or the other
 * getting ignored. To use both, you will have to specify on a per {@link com.andrewgrosner.acela.annotation.Key} basis.
 */
public abstract class LongDateConverter implements TypeConverter<Date, Long> {

    @Override
    public Date parse(JsonParserWrapper jsonParser, Long defValue) {
        Long value = jsonParser.getValueAsLong(defValue);
        if (value != null) {
            return new Date(value);
        } else {
            return null;
        }
    }

    @Override
    public void serialize(JsonGeneratorWrapper wrapper, String fieldName, Date value, Long defValue) {
        if (value != null) {
            wrapper.writeLongField(fieldName, value.getTime());
        } else {
            wrapper.writeLongField(fieldName, defValue);
        }
    }
}
