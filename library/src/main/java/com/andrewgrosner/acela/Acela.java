package com.andrewgrosner.acela;

import com.andrewgrosner.acela.annotation.Translatable;
import com.andrewgrosner.acela.converter.CalendarConverter;
import com.andrewgrosner.acela.handler.BaseTranslator;
import com.andrewgrosner.acela.processor.definition.ClassNames;
import com.fasterxml.jackson.core.JsonFactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: andrewgrosner
 * Description: The main class to parse data from.
 */
public class Acela {

    public static final JsonFactory JSON_FACTORY = new JsonFactory();

    private static Map<Class<?>, BaseTranslator> parseableMap = new HashMap<>();
    private static Map<Class<?>, TypeConverter> typeConverterMap = new HashMap<Class<?>, TypeConverter>() {{
        put(Calendar.class, new CalendarConverter());
    }};

    private static Object manager;

    /**
     * Creates the manager, which in turn will fill this class with all of the {@link com.andrewgrosner.acela.handler.ParseHandler}
     * and {@link com.andrewgrosner.acela.handler.ParseHandler} needed.
     *
     * @return The manager created once by reflection since its generated during annotation processing.
     */
    private static Object createManagerIfNeeded() {
        if (manager == null) {
            try {
                manager = Class.forName(ClassNames.BASE_PACKAGE + ".AcelaManager").newInstance();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return manager;
    }

    @SuppressWarnings("unchecked")
    public static <ReturnType> BaseTranslator<ReturnType> getTranslator(Class<ReturnType> returnClass) {
        createManagerIfNeeded();
        BaseTranslator<ReturnType> baseParseHandler = parseableMap.get(returnClass);
        if (baseParseHandler == null) {
            throw new IllegalArgumentException(String.format("The following type %1s was not registered as " +
                    "translatable. Please add the @Translatable annotation to the class", returnClass));
        } else {
            baseParseHandler.initIfEmpty();
        }
        return baseParseHandler;
    }

    /**
     * internal method that will add each {@link com.andrewgrosner.acela.handler.ParseHandler} automatically
     *
     * @param parseableClass    The class that is annotated with {@link Translatable}
     * @param translatorHandler The corresponding {@link com.andrewgrosner.acela.handler.ParseHandler} for that class.
     */
    static void addTranslator(Class<?> parseableClass, BaseTranslator translatorHandler) {
        parseableMap.put(parseableClass, translatorHandler);
    }

    /**
     * Manually registers a {@link TypeConverter} with this class. Otherwise we can use the
     *
     * @param typeConverterClass the class when defined as a field that uses this converter. Note that multiple calls of
     *                           this method with same class will override previous calls.
     * @param typeConverter      The converter to use.
     * @param <ModelType>        The field type to register this {@link TypeConverter} with.
     */
    public static <ModelType> void registerTypeConverterForClass(Class<ModelType> typeConverterClass,
                                                                 TypeConverter<ModelType, ?> typeConverter) {
        typeConverterMap.put(typeConverterClass, typeConverter);
    }

    /**
     * @param converterClass   The class that has a type converter registered with it.
     * @param <ModelType>      The kind of class the converter represents when in a {@link Translatable}
     * @param <JsonParserType> The kind of class we retrieve from the {@link JsonParserWrapper}
     * @return The type converter for the specified class. These are registered globally.
     */
    @SuppressWarnings("unchecked")
    public static <ModelType, JsonParserType> TypeConverter<ModelType, JsonParserType> getTypeConverterForClass(
            Class<ModelType> converterClass) {
        TypeConverter<ModelType, JsonParserType> converter = getTypeConverterForClassNoCheck(converterClass);
        if (converter == null) {
            throw new IllegalArgumentException(
                    "Missing type converter for class:" + converterClass + ", one needs to be" +
                            "defined for this field using the @TypeConverter annotation and implementing TypeConverter");
        }
        return converter;
    }

    /**
     * Just queries the map, does not throw an {@link IllegalArgumentException} when called for internal use only.
     *
     * @param converterClass   The class that has a type converter registered with it.
     * @param <ModelType>      The kind of class the converter represents when in a {@link Translatable}
     * @param <JsonParserType> The kind of class we retrieve from the {@link JsonParserWrapper}
     * @return The type converter for the specified class. These are registered globally.
     */
    @SuppressWarnings("unchecked")
    static <ModelType, JsonParserType> TypeConverter<ModelType, JsonParserType> getTypeConverterForClassNoCheck(
            Class<ModelType> converterClass) {
        createManagerIfNeeded();
        return typeConverterMap.get(converterClass);
    }
}
