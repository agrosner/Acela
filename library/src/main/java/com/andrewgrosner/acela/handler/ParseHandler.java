package com.andrewgrosner.acela.handler;

import com.fasterxml.jackson.core.JsonParser;
import com.andrewgrosner.acela.annotation.Key;
import com.andrewgrosner.acela.annotation.Translatable;

import java.io.IOException;

/**
 * Description: The interface for parsing an object. We can insert a custom one using the {@link Translatable#parseHandler()}
 * that's called at the end of parsing.
 */
public interface ParseHandler<ParseableClass> {

    /**
     * @return a new instance of the {@link ParseableClass} without using reflection.
     */
    ParseableClass getInstance();

    /**
     * Will set all of the fields for the {@link ParseableClass} that define a {@link Key} attribute.
     *
     * @param parseable  The instance that we are parsing
     * @param jsonParser The parser to use.
     * @throws IOException inherited from {@link JsonParser}
     */
    void parse(ParseableClass parseable, JsonParser jsonParser) throws IOException;

}
