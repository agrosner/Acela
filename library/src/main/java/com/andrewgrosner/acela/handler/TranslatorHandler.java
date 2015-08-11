package com.andrewgrosner.acela.handler;

/**
 * Description: Defines the merging of {@link ParseHandler} and {@link com.andrewgrosner.acela.handler.SerializeHandler} that's responsible for both
 * parsing and serializing.
 */
public interface TranslatorHandler<TranslationType> extends ParseHandler<TranslationType>, com.andrewgrosner.acela.handler.SerializeHandler<TranslationType> {
}
