package com.andrewgrosner.acela.handler;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

/**
 * Description: Main interface for serializing existing data.
 */
public interface SerializeHandler<SerializeType> {

    void serialize(SerializeType serializeType, JsonGenerator jsonGenerator) throws IOException;
}
