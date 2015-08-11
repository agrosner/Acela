package com.andrewgrosner.acela.event;

/**
 * Description: Implement this in your {@link com.andrewgrosner.acela.annotation.Translatable}
 * class and these methods get called during parsing.
 */
public interface ParseStatusListener {

    /**
     * Called before parse begins. Do some set up or pre-parse logic here.
     */
    void onPreParse();

    /**
     * Called when parsing completes.
     */
    void onParseComplete();
}
