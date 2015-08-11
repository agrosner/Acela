package com.andrewgrosner.acela;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Description: Provides a focused set of methods for a {@link JsonGenerator}
 */
public class JsonGeneratorWrapper {

    private final JsonGenerator jsonGenerator;

    private boolean shouldThrowExceptions = false;

    public JsonGeneratorWrapper(JsonGenerator jsonGenerator) {
        this.jsonGenerator = jsonGenerator;
    }

    /**
     * If true, any exception during serialization gets thrown in the write methods.
     *
     * @param shouldThrowExceptions if true, we throw exceptions for failures during parsing.
     */
    public void setShouldThrowExceptions(boolean shouldThrowExceptions) {
        this.shouldThrowExceptions = shouldThrowExceptions;
    }

    /**
     * Writes an arbitrary {@link Number} to a field. If it doesn't know its type, it
     * coerces the value into a {@link Double}.
     *
     * @param fieldName The name of the field to write.
     * @param value     The value to write.
     */
    public void writeNumberField(String fieldName, Number value) {
        if (value instanceof Float) {
            writeFloatField(fieldName, value.floatValue());
        } else if (value instanceof Integer) {
            writeIntField(fieldName, value.intValue());
        } else if (value instanceof Double) {
            writeDoubleField(fieldName, value.doubleValue());
        } else if (value instanceof BigDecimal) {
            writeBigDecimalField(fieldName, (BigDecimal) value);
        } else if (value != null) {
            writeDoubleField(fieldName, value.doubleValue());
        }
    }

    public void writeStringField(String fieldName, String value) {
        try {
            jsonGenerator.writeStringField(fieldName, value);
        } catch (IOException e) {
            tryThrowException(e);
        }
    }

    public void writeBooleanField(String fieldName, Boolean value) {
        try {
            jsonGenerator.writeBooleanField(fieldName, value != null && value);
        } catch (IOException e) {
            tryThrowException(e);
        }
    }

    public void writeBooleanField(String fieldName, boolean value) {
        try {
            jsonGenerator.writeBooleanField(fieldName, value);
        } catch (IOException e) {
            tryThrowException(e);
        }
    }

    public void writeLongField(String fieldName, long value) {
        try {
            jsonGenerator.writeNumberField(fieldName, value);
        } catch (IOException e) {
            tryThrowException(e);
        }
    }

    public void writeLongField(String fieldName, Long value) {
        try {
            jsonGenerator.writeNumberField(fieldName, value == null ? 0 : value);
        } catch (IOException e) {
            tryThrowException(e);
        }
    }

    public void writeIntField(String fieldName, int value) {
        try {
            jsonGenerator.writeNumberField(fieldName, value);
        } catch (IOException e) {
            tryThrowException(e);
        }
    }

    public void writeIntField(String fieldName, Integer value) {
        try {
            jsonGenerator.writeNumberField(fieldName, value == null ? 0 : value);
        } catch (IOException e) {
            tryThrowException(e);
        }
    }

    public void writeDoubleField(String fieldName, double value) {
        try {
            jsonGenerator.writeNumberField(fieldName, value);
        } catch (IOException e) {
            tryThrowException(e);
        }
    }

    public void writeDoubleField(String fieldName, Double value) {
        try {
            jsonGenerator.writeNumberField(fieldName, value == null ? 0d : value);
        } catch (IOException e) {
            tryThrowException(e);
        }
    }

    public void writeBigDecimalField(String fieldName, BigDecimal value) {
        try {
            jsonGenerator.writeNumberField(fieldName, value);
        } catch (IOException e) {
            tryThrowException(e);
        }
    }

    public void writeFloatField(String fieldName, Float value) {
        try {
            jsonGenerator.writeNumberField(fieldName, value == null ? 0f : value);
        } catch (IOException e) {
            tryThrowException(e);
        }
    }

    public void writeFloatField(String fieldName, float value) {
        try {
            jsonGenerator.writeNumberField(fieldName, value);
        } catch (IOException e) {
            tryThrowException(e);
        }
    }

    public void writeBigDecimalValue(String fieldName, BigDecimal value) {
        try {
            jsonGenerator.writeNumberField(fieldName, value);
        } catch (IOException e) {
            tryThrowException(e);
        }
    }

    public void writeNullField(String fieldName) {
        try {
            jsonGenerator.writeNullField(fieldName);
        } catch (IOException e) {
            tryThrowException(e);
        }
    }

    private void tryThrowException(Exception e) {
        if (shouldThrowExceptions) {
            throw new RuntimeException(e);
        }
    }

}
