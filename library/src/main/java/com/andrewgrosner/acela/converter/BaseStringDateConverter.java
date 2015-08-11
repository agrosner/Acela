package com.andrewgrosner.acela.converter;

import com.andrewgrosner.acela.JsonGeneratorWrapper;
import com.andrewgrosner.acela.JsonParserWrapper;
import com.andrewgrosner.acela.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Description: Provides base functionality for a converter that takes a {@link Date} field and
 * handles how it is represented in JSON as a string. Be warned that combining this with {@link LongDateConverter}
 * could result in one or the other getting ignored. To use both, you will have to specify on a per
 * {@link com.andrewgrosner.acela.annotation.Key} basis.
 */
public abstract class BaseStringDateConverter implements TypeConverter<Date, String> {

    private final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return createDateFormat();
        }
    };

    @Override
    public Date parse(JsonParserWrapper jsonParser, String defValue) {
        try {
            String value = jsonParser.getValueAsString(defValue);
            if (value != null && value.length() > 0) {
                return dateFormat.get().parse(value);
            } else {
                return null;
            }
        } catch (ParseException e) {
            if (shouldThrowParseException()) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public void serialize(JsonGeneratorWrapper wrapper, String fieldName, Date value, String defValue) {
        if (value != null) {
            wrapper.writeStringField(fieldName, dateFormat.get().format(value));
        } else {
            wrapper.writeStringField(fieldName, defValue);
        }
    }

    /**
     * @return true if you want the parse to crash when an invalid date occurs ({@link ParseException}).
     * Default is false, to ignore these exceptions.
     */
    protected boolean shouldThrowParseException() {
        return false;
    }

    /**
     * @return The intended {@link DateFormat} to use for this converter.
     */
    protected abstract DateFormat createDateFormat();
}
