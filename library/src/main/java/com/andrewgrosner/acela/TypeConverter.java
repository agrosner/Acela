package com.andrewgrosner.acela;

import com.andrewgrosner.acela.annotation.Translatable;

/**
 * Description: Allows you to define your own way of parsing data from one format into your model format.
 */
public interface TypeConverter<ModelType, JsonParserType> {

    /**
     * @param jsonParser The wrapper parser to retrieve data from.
     * @param defValue   The default value filled by the compiler. This must match the type of data we're retrieving from the {@link JsonParserWrapper}.
     * @return A representation of the data that fills a field within a {@link Translatable} class.
     */
    ModelType parse(JsonParserWrapper jsonParser, JsonParserType defValue);

    /**
     * Serialize this field to the wrapper class.
     *
     * @param wrapper
     * @param defValue
     */
    void serialize(JsonGeneratorWrapper wrapper, String fieldName, ModelType value, JsonParserType defValue);
}
