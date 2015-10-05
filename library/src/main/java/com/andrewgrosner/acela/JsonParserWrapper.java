package com.andrewgrosner.acela;

import com.fasterxml.jackson.core.JsonParser;
import com.andrewgrosner.acela.processor.JSONMethodMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * Description: Wraps around the {@link JsonParser} to provide a more focused set of methods for retrieving data
 * from the parser.
 */
public class JsonParserWrapper {

    private final JsonParser jsonParser;

    private boolean shouldThrowExceptions = false;

    public JsonParserWrapper(JsonParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    /**
     * If true, any exception during parse gets thrown in the get methods.
     *
     * @param shouldThrowExceptions if true, we throw exceptions for failures during parsing.
     */
    public void setShouldThrowExceptions(boolean shouldThrowExceptions) {
        this.shouldThrowExceptions = shouldThrowExceptions;
    }

    public JsonParser getJsonParser() {
        return jsonParser;
    }

    /**
     * @param returnType   The type to return.
     * @param defValue     The default value that we use if missing.
     * @param <ReturnType> The return type that we return.
     * @return safely retrieves proper method to call for specified return type. Also attempts to use {@link TypeConverter}
     * if possible.
     */
    @SuppressWarnings("unchecked")
    public <ReturnType> ReturnType getCurrentValue(Class<ReturnType> returnType, ReturnType defValue) {
        TypeConverter typeConverter = Acela.getTypeConverterForClassNoCheck(returnType);
        ReturnType value = null;
        if (typeConverter != null) {
            value = (ReturnType) typeConverter.parse(this, defValue);
        } else {
            if (JSONMethodMap.hasMethod(returnType.getCanonicalName())) {
                if (returnType.equals(String.class)) {
                    value = (ReturnType) getValueAsString((String) defValue);
                } else if (returnType.equals(Boolean.class) || returnType.equals(boolean.class)) {
                    value = (ReturnType) getValueAsBoolean((Boolean) defValue);
                } else if (returnType.equals(Double.class) || returnType.equals(double.class)) {
                    value = (ReturnType) getValueAsDouble((Double) defValue);
                } else if (returnType.equals(Float.class) || returnType.equals(float.class)) {
                    value = (ReturnType) getFloatValue((Float) defValue);
                } else if (returnType.equals(Number.class)) {
                    value = (ReturnType) getNumberValue((Number) defValue);
                } else if (returnType.equals(Integer.class) || returnType.equals(int.class)) {
                    value = (ReturnType) getValueAsInt((Integer) defValue);
                } else if (returnType.equals(Long.class) || returnType.equals(long.class)) {
                    value = (ReturnType) getValueAsLong((Long) defValue);
                }
            } else if (returnType.isAssignableFrom(Collection.class)) {
                try {
                    value = (ReturnType) Acela.getTranslator(returnType).parseList(jsonParser);
                } catch (IOException e) {
                    e.printStackTrace();
                    value = defValue;
                }
            } else if (returnType.isAssignableFrom(Map.class)) {
                try {
                    value = (ReturnType) Acela.getTranslator(returnType).parseMap(jsonParser);
                } catch (IOException e) {
                    e.printStackTrace();
                    value = defValue;
                }
            } else if (returnType.isArray()) {
                try {
                    value = (ReturnType) Acela.getTranslator(returnType).parseArray(jsonParser);
                } catch (IOException e) {
                    e.printStackTrace();
                    value = defValue;
                }
            } else {
                try {
                    value = Acela.getTranslator(returnType).parse(jsonParser);
                } catch (IOException e) {
                    e.printStackTrace();
                    value = defValue;
                }
            }
        }
        return value;
    }

    public int getValueAsInt(int defValue) {
        int value;
        try {
            value = jsonParser.getValueAsInt(defValue);
        } catch (IOException e) {
            e.printStackTrace();
            tryThrowException(e);
            value = defValue;
        }
        return value;
    }

    public Integer getValueAsInt(Integer defValue) {
        Integer value = 0;
        try {
            value = jsonParser.getValueAsInt(defValue == null ? value : defValue);
        } catch (IOException e) {
            e.printStackTrace();
            tryThrowException(e);
            value = defValue;
        }
        return value;
    }

    public double getValueAsDouble(double defValue) {
        double value;
        try {
            value = jsonParser.getValueAsDouble(defValue);
        } catch (IOException e) {
            e.printStackTrace();
            tryThrowException(e);
            value = defValue;
        }
        return value;
    }

    public Double getValueAsDouble(Double defValue) {
        Double value = 0d;
        try {
            value = jsonParser.getValueAsDouble(defValue == null ? value : defValue);
        } catch (IOException e) {
            e.printStackTrace();
            tryThrowException(e);
            value = defValue;
        }
        return value;
    }

    public long getValueAsLong(long defValue) {
        long value;
        try {
            value = jsonParser.getValueAsLong(defValue);
        } catch (IOException e) {
            e.printStackTrace();
            tryThrowException(e);
            value = defValue;
        }
        return value;
    }

    public Long getValueAsLong(Long defValue) {
        Long value = 0l;
        try {
            value = jsonParser.getValueAsLong(defValue == null ? value : defValue);
        } catch (IOException e) {
            e.printStackTrace();
            tryThrowException(e);
            value = defValue;
        }
        return value;
    }

    public boolean getValueAsBoolean(boolean defValue) {
        boolean value;
        try {
            value = jsonParser.getValueAsBoolean(defValue);
        } catch (IOException e) {
            e.printStackTrace();
            tryThrowException(e);
            value = defValue;
        }
        return value;
    }

    public Boolean getValueAsBoolean(Boolean defValue) {
        Boolean value = false;
        try {
            value = jsonParser.getValueAsBoolean(defValue == null ? value : defValue);
        } catch (IOException e) {
            e.printStackTrace();
            tryThrowException(e);
            value = defValue;
        }
        return value;
    }

    public String getValueAsString(String defValue) {
        String value;
        try {
            value = jsonParser.getValueAsString(defValue);
        } catch (IOException e) {
            e.printStackTrace();
            tryThrowException(e);
            value = defValue;
        }
        return value;
    }

    public String getTextValue() {
        String text = null;
        try {
            text = jsonParser.getText();
        } catch (IOException e) {
            e.printStackTrace();
            tryThrowException(e);
        } finally {
            return text;
        }
    }

    public Number getNumberValue(Number defValue) {
        Number value;
        try {
            value = jsonParser.getNumberValue();
        } catch (IOException e) {
            e.printStackTrace();
            tryThrowException(e);
            value = defValue;
        }
        return value;
    }

    public Float getFloatValue(Float defValue) {
        Float value;
        try {
            value = jsonParser.getFloatValue();
        } catch (IOException e) {
            e.printStackTrace();
            tryThrowException(e);
            value = defValue;
        }
        return value;
    }

    public BigDecimal getBigDecimalValue(BigDecimal defValue) {
        BigDecimal value;
        try {
            value = jsonParser.getDecimalValue();
        } catch (IOException e) {
            e.printStackTrace();
            tryThrowException(e);
            value = defValue;
        }
        return value;
    }

    private void tryThrowException(Exception e) {
        if (shouldThrowExceptions) {
            throw new RuntimeException(e);
        }
    }
}
