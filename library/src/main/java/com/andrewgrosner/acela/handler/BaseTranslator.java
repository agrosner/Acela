package com.andrewgrosner.acela.handler;

import com.andrewgrosner.acela.Acela;
import com.andrewgrosner.acela.JsonGeneratorWrapper;
import com.andrewgrosner.acela.JsonParserWrapper;
import com.andrewgrosner.acela.event.ParseStatusListener;
import com.andrewgrosner.acela.event.SerializeStatusListener;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.andrewgrosner.acela.Acela.JSON_FACTORY;

/**
 * Description:
 */
public abstract class BaseTranslator<T> implements TranslatorHandler<T> {

    private boolean isEmpty = true;

    public void initIfEmpty() {
        if (isEmpty) {
            isEmpty = false;
            init();
        }
    }

    // region Simple Parse Methods

    @SuppressWarnings("unchecked")
    public T parse(JsonParser jsonParser) throws IOException {
        ParseHandler<T> parseHandler = Acela.getTranslator(getType());
        T translator = parseHandler.getInstance();
        parse(translator, jsonParser);
        return translator;
    }

    @Override
    public void parse(T translator, JsonParser jsonParser) throws IOException {
        JsonParserWrapper wrapper = new JsonParserWrapper(jsonParser);
        wrapper.setShouldThrowExceptions(shouldThrowFieldExceptions());
        if (jsonParser.getCurrentToken() == null) {
            jsonParser.nextToken();
        }

        if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
            jsonParser.skipChildren();
        } else {
            if (translator instanceof ParseStatusListener) {
                ((ParseStatusListener) translator).onPreParse();
            }

            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String key = jsonParser.getCurrentName();
                jsonParser.nextToken();
                handleParse(translator, wrapper, jsonParser, key);
                jsonParser.skipChildren();
            }

            if (translator instanceof ParseStatusListener) {
                ((ParseStatusListener) translator).onParseComplete();
            }
        }
    }

    public T parse(String jsonString) {
        try {
            return parse(JSON_FACTORY.createParser(jsonString));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T parse(InputStream inputStream) {
        try {
            return parse(JSON_FACTORY.createParser(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T parse(char[] chars) {
        try {
            return parse(JSON_FACTORY.createParser(chars));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T parse(byte[] bytes) {
        try {
            return parse(JSON_FACTORY.createParser(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T parse(File file) {
        try {
            return parse(JSON_FACTORY.createParser(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T parse(Reader reader) {
        try {
            return parse(JSON_FACTORY.createParser(reader));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parse(T translator, String jsonString) {
        try {
            parse(translator, JSON_FACTORY.createParser(jsonString));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parse(T translator, InputStream inputStream) {
        try {
            parse(translator, JSON_FACTORY.createParser(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parse(T translator, char[] chars) {
        try {
            parse(translator, JSON_FACTORY.createParser(chars));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parse(T translator, byte[] bytes) {
        try {
            parse(translator, JSON_FACTORY.createParser(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parse(T translator, File file) {
        try {
            parse(translator, JSON_FACTORY.createParser(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parse(T translator, Reader reader) {
        try {
            parse(translator, JSON_FACTORY.createParser(reader));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // endregion Simple Parse Methods

    // region Simple Serialize Methods

    public String serialize(T translator) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonGenerator generator = Acela.JSON_FACTORY.createGenerator(stringWriter);
        serialize(translator, generator);
        generator.close();
        return stringWriter.toString();
    }

    @Override
    public void serialize(T translator, JsonGenerator jsonGenerator) throws IOException {
        JsonGeneratorWrapper wrapper = new JsonGeneratorWrapper(jsonGenerator);
        wrapper.setShouldThrowExceptions(shouldThrowFieldExceptions());
        if (translator instanceof SerializeStatusListener) {
            ((SerializeStatusListener) translator).onPreSerialize();
        }
        jsonGenerator.writeStartObject();
        handleSerialize(translator, wrapper, jsonGenerator);
        jsonGenerator.writeEndObject();
        if (translator instanceof SerializeStatusListener) {
            ((SerializeStatusListener) translator).onPostSerialize();
        }
    }

    // endregion Simple Serialize Methods

    // region List Parse Methods

    public List<T> parseList(JsonParser parser) throws IOException {
        List<T> translatorTypeList = new ArrayList<>();
        parseList(translatorTypeList, parser);
        return translatorTypeList;
    }

    public void parseList(List<T> translatorTypes, JsonParser jsonParser) throws IOException {
        JsonParserWrapper wrapper = new JsonParserWrapper(jsonParser);

        if (jsonParser.getCurrentToken() == null) {
            jsonParser.nextToken();
        }

        if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                translatorTypes.add(wrapper.getCurrentValue(getType(), null));
            }
        }
    }

    public List<T> parseList(String jsonString) {
        try {
            return parseList(JSON_FACTORY.createParser(jsonString));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> parseList(InputStream inputStream) {
        try {
            return parseList(JSON_FACTORY.createParser(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> parseList(char[] chars) {
        try {
            return parseList(JSON_FACTORY.createParser(chars));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> parseList(byte[] bytes) {
        try {
            return parseList(JSON_FACTORY.createParser(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> parseList(File file) {
        try {
            return parseList(JSON_FACTORY.createParser(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> parseList(Reader reader) {
        try {
            return parseList(JSON_FACTORY.createParser(reader));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parse(List<T> translator, String jsonString) {
        try {
            parseList(translator, JSON_FACTORY.createParser(jsonString));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseList(List<T> translator, InputStream inputStream) {
        try {
            parseList(translator, JSON_FACTORY.createParser(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseList(List<T> translator, char[] chars) {
        try {
            parseList(translator, JSON_FACTORY.createParser(chars));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseList(List<T> translator, byte[] bytes) {
        try {
            parseList(translator, JSON_FACTORY.createParser(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseList(List<T> translator, File file) {
        try {
            parseList(translator, JSON_FACTORY.createParser(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseList(List<T> translator, Reader reader) {
        try {
            parseList(translator, JSON_FACTORY.createParser(reader));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // endregion List Parse Methods

    // region List Serialize Methods

    public String serializeList(List<T> translator) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonGenerator generator = Acela.JSON_FACTORY.createGenerator(stringWriter);
        serializeList(generator, translator);
        generator.close();
        return stringWriter.toString();
    }

    public void serializeList(JsonGenerator jsonGenerator, List<T> translatorTypes) throws IOException {
        jsonGenerator.writeStartArray();
        for (T translatorType : translatorTypes) {
            if (translatorType != null) {
                Acela.getTranslator(getType()).serialize(translatorType, jsonGenerator);
            } else {
                jsonGenerator.writeNull();
            }
        }
        jsonGenerator.writeEndArray();
    }

    // endregion List Serialize Methods

    // region Array Parse Methods

    @SuppressWarnings("unchecked")
    public T[] parseArray(JsonParser parser) throws IOException {
        List<T> translatorTypeList = new ArrayList<>();
        JsonParserWrapper wrapper = new JsonParserWrapper(parser);

        if (parser.getCurrentToken() == null) {
            parser.nextToken();
        }

        if (parser.getCurrentToken() == JsonToken.START_ARRAY) {
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                translatorTypeList.add(wrapper.getCurrentValue(getType(), null));
            }
        }

        return translatorTypeList.toArray((T[]) Array.newInstance(getType(), translatorTypeList.size()));
    }

    public void parseArray(T[] translatorTypes, JsonParser jsonParser) throws IOException {
        JsonParserWrapper wrapper = new JsonParserWrapper(jsonParser);

        if (jsonParser.getCurrentToken() == null) {
            jsonParser.nextToken();
        }

        if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
            int count = 0;
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                translatorTypes[count] = wrapper.getCurrentValue(getType(), null);
                count++;
            }
        }
    }

    public T[] parseArray(String jsonString) {
        try {
            return parseArray(JSON_FACTORY.createParser(jsonString));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T[] parseArray(InputStream inputStream) {
        try {
            return parseArray(JSON_FACTORY.createParser(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T[] parseArray(char[] chars) {
        try {
            return parseArray(JSON_FACTORY.createParser(chars));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T[] parseArray(byte[] bytes) {
        try {
            return parseArray(JSON_FACTORY.createParser(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T[] parseArray(File file) {
        try {
            return parseArray(JSON_FACTORY.createParser(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T[] parseArray(Reader reader) {
        try {
            return parseArray(JSON_FACTORY.createParser(reader));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseArray(T[] translator, String jsonString) {
        try {
            parseArray(translator, JSON_FACTORY.createParser(jsonString));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseArray(T[] translator, InputStream inputStream) {
        try {
            parseArray(translator, JSON_FACTORY.createParser(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseArray(T[] translator, char[] chars) {
        try {
            parseArray(translator, JSON_FACTORY.createParser(chars));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseArray(T[] translator, byte[] bytes) {
        try {
            parseArray(translator, JSON_FACTORY.createParser(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseArray(T[] translator, File file) {
        try {
            parseArray(translator, JSON_FACTORY.createParser(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseArray(T[] translator, Reader reader) {
        try {
            parseArray(translator, JSON_FACTORY.createParser(reader));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // endregion Array Parse Methods

    // region Array Serialize Methods

    public String serializeArray(T[] translator) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonGenerator generator = Acela.JSON_FACTORY.createGenerator(stringWriter);
        serializeArray(generator, translator);
        generator.close();
        return stringWriter.toString();
    }

    public void serializeArray(JsonGenerator jsonGenerator, T[] translatorTypes) throws IOException {
        jsonGenerator.writeStartArray();
        for (T type : translatorTypes) {
            if (type != null) {
                Acela.getTranslator(getType()).serialize(type, jsonGenerator);
            } else {
                jsonGenerator.writeNull();
            }
        }

        jsonGenerator.writeEndArray();
    }

    // endregion Array Serialize Methods

    // region Map Parse Methods

    public Map<String, T> parseMap(JsonParser parser) throws IOException {
        Map<String, T> map = new HashMap<>();
        parseMap(map, parser);
        return map;
    }

    public void parseMap(Map<String, T> map, JsonParser jsonParser) throws IOException {
        JsonParserWrapper wrapper = new JsonParserWrapper(jsonParser);

        if (jsonParser.getCurrentToken() == null) {
            jsonParser.nextToken();
        }

        if (jsonParser.getCurrentToken() == com.fasterxml.jackson.core.JsonToken.START_OBJECT) {
            while (jsonParser.nextToken() != com.fasterxml.jackson.core.JsonToken.END_OBJECT) {
                String key = jsonParser.getText();
                jsonParser.nextToken();
                if (jsonParser.getCurrentToken() == com.fasterxml.jackson.core.JsonToken.VALUE_NULL) {
                    map.put(key, null);
                } else {
                    map.put(key, wrapper.getCurrentValue(getType(), null));
                }
            }
        }
    }

    public Map<String, T> parseMap(String jsonString) {
        try {
            return parseMap(JSON_FACTORY.createParser(jsonString));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, T> parseMap(InputStream inputStream) {
        try {
            return parseMap(JSON_FACTORY.createParser(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, T> parseMap(char[] chars) {
        try {
            return parseMap(JSON_FACTORY.createParser(chars));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, T> parseMap(byte[] bytes) {
        try {
            return parseMap(JSON_FACTORY.createParser(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, T> parseMap(File file) {
        try {
            return parseMap(JSON_FACTORY.createParser(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, T> parseMap(Reader reader) {
        try {
            return parseMap(JSON_FACTORY.createParser(reader));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parse(Map<String, T> translator, String jsonString) {
        try {
            parseMap(translator, JSON_FACTORY.createParser(jsonString));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseMap(Map<String, T> translator, InputStream inputStream) {
        try {
            parseMap(translator, JSON_FACTORY.createParser(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseMap(Map<String, T> translator, char[] chars) {
        try {
            parseMap(translator, JSON_FACTORY.createParser(chars));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseMap(Map<String, T> translator, byte[] bytes) {
        try {
            parseMap(translator, JSON_FACTORY.createParser(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseMap(Map<String, T> translator, File file) {
        try {
            parseMap(translator, JSON_FACTORY.createParser(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseMap(Map<String, T> translator, Reader reader) {
        try {
            parseMap(translator, JSON_FACTORY.createParser(reader));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // endregion Map Parse Methods

    // region Map Serialize Methods

    public String serializeMap(Map<String, T> translator) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonGenerator generator = Acela.JSON_FACTORY.createGenerator(stringWriter);
        serializeMap(generator, translator);
        generator.close();
        return stringWriter.toString();
    }

    public void serializeMap(JsonGenerator jsonGenerator, Map<String, T> map) throws IOException {
        jsonGenerator.writeStartObject();
        for (Map.Entry<String, T> entry : map.entrySet()) {
            jsonGenerator.writeFieldName(entry.getKey());
            if (entry.getValue() == null) {
                jsonGenerator.writeNull();
            } else {
                Acela.getTranslator(getType()).serialize(entry.getValue(), jsonGenerator);
            }
        }

        jsonGenerator.writeEndObject();
    }

    // endregion Map Serialize Methods

    protected void init() {

    }

    // region Abstract Members

    protected abstract Class<T> getType();

    protected abstract boolean shouldThrowFieldExceptions();

    protected abstract void handleParse(T translator, JsonParserWrapper wrapper, JsonParser jsonParser,
                                        String fieldName) throws IOException;

    protected abstract void handleSerialize(T translator, JsonGeneratorWrapper wrapper,
                                            JsonGenerator jsonGenerator) throws IOException;

    // endregion Abstract Members

}
