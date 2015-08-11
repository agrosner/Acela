package com.andrewgrosner.acela.converter;

import com.andrewgrosner.acela.JsonGeneratorWrapper;
import com.andrewgrosner.acela.JsonParserWrapper;
import com.andrewgrosner.acela.TypeConverter;

import java.util.Calendar;

/**
 * Description: Handles {@link Calendar} fields represented by {@link Long} data underneath. This is
 * the default handling.
 */
public class CalendarConverter implements TypeConverter<Calendar, Long> {

    @Override
    public Calendar parse(JsonParserWrapper jsonParser, Long defValue) {
        Long value = jsonParser.getValueAsLong(defValue);
        if (value != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(value);
            return calendar;
        } else {
            return null;
        }
    }

    @Override
    public void serialize(JsonGeneratorWrapper wrapper, String fieldName, Calendar value, Long defValue) {
        if (value != null) {
            wrapper.writeLongField(fieldName, value.getTimeInMillis());
        } else {
            wrapper.writeLongField(fieldName, defValue);
        }
    }
}
