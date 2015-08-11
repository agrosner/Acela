package com.andrewgrosner.acela.event;

import com.andrewgrosner.acela.JsonParserWrapper;

/**
 * Description: Enables a class to listen to parse events and provide own custom logic during so.
 */
public interface ParseListener {

    /*
     * Allows classes to listen to parse events. Check if the key is the one the class cares about,
     * and call the {@link JsonParser} methods to retrieve the proper data.
     *
     * @param wrapper the wrapper providing focused retrieval.
     * @param key     the key of the current field.
     */
    void onParse(JsonParserWrapper wrapper, String key);
}
