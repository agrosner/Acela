package com.andrewgrosner.acela.processor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 */
public class JSONMethodMap {

    static final Map<String, String> VALUE_AS_METHOD_MAP = new HashMap<String, String>() {{
        put(String.class.getCanonicalName(), "String");
        put(Boolean.class.getCanonicalName(), "Boolean");
        put(boolean.class.getCanonicalName(), "Boolean");
        put(Double.class.getCanonicalName(), "Double");
        put(double.class.getCanonicalName(), "Double");
        put(Integer.class.getCanonicalName(), "Int");
        put(int.class.getCanonicalName(), "Int");
        put(Long.class.getCanonicalName(), "Long");
        put(long.class.getCanonicalName(), "Long");
    }};

    static final Map<String, String> VALUE_OTHER_MAP = new HashMap<String, String>() {{
        put(Number.class.getCanonicalName(), "Number");
        put(Float.class.getCanonicalName(), "Float");
        put(float.class.getCanonicalName(), "Float");
        put(BigDecimal.class.getCanonicalName(), "BigDecimal");
    }};

    public static boolean hasMethod(String className) {
        return VALUE_AS_METHOD_MAP.containsKey(className) || VALUE_OTHER_MAP.containsKey(className);
    }

    public static String getMethodName(String className, String defValue) {
        String method = VALUE_AS_METHOD_MAP.get(className);
        if (method == null) {
            method = VALUE_OTHER_MAP.get(className);
            if (method == null) {
                return null;
            } else {
                return "get" + method + "Value(" + defValue + ")";
            }
        } else {
            return "getValueAs" + method + "(" + defValue + ")";
        }
    }

    public static String getSerializeMethodName(String className) {
        String method = VALUE_AS_METHOD_MAP.get(className);
        if (method == null) {
            method = VALUE_OTHER_MAP.get(className);
            if (method == null) {
                return null;
            } else {
                return "write" + method + "Field";
            }
        } else {
            return "write" + method + "Field";
        }
    }

    public static String getListCollectionMethodName(String className) {
        String method = VALUE_AS_METHOD_MAP.get(className);
        if (method == null) {
            method = VALUE_OTHER_MAP.get(className);
            if (method == null) {
                return null;
            } else {
                return "write" + method;
            }
        } else {
            return "write" + method;
        }
    }
}
