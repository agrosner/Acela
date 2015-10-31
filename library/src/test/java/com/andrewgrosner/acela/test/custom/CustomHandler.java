package com.andrewgrosner.acela.test.custom;

import com.andrewgrosner.acela.JsonGeneratorWrapper;
import com.andrewgrosner.acela.JsonParserWrapper;
import com.andrewgrosner.acela.handler.BaseTranslator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

/**
 * Description: Example of a custom parse handler that overrides the generated _Translator class.
 */
public class CustomHandler extends BaseTranslator<CustomHandlers> {

    @Override
    public CustomHandlers getInstance() {
        return new CustomHandlers();
    }

    @Override
    public void parse(CustomHandlers parseable, JsonParser jsonParser) throws IOException {

    }

    @Override
    public void serialize(CustomHandlers customHandlers, JsonGenerator jsonGenerator) throws IOException {

    }

    @Override
    protected Class<CustomHandlers> getType() {
        return CustomHandlers.class;
    }

    @Override
    protected boolean shouldThrowFieldExceptions() {
        return false;
    }

    @Override
    protected void handleParse(CustomHandlers translator, JsonParserWrapper wrapper, JsonParser jsonParser, String fieldName) throws IOException {
        // handle the parsing here.
    }

    @Override
    protected void handleSerialize(CustomHandlers translator, JsonGeneratorWrapper wrapper, JsonGenerator jsonGenerator) throws IOException {
        // handle serializing here.
    }
}
