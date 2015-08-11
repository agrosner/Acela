package com.andrewgrosner.acela.event;

import com.fasterxml.jackson.core.JsonGenerator;
import com.andrewgrosner.acela.JsonGeneratorWrapper;
import com.andrewgrosner.acela.annotation.Key;

/**
 * Description: Enables a class to listen to serialization events and provide some custom logic during serialization. Please
 * note that this method is called multiple times during serialization (once for each {@link Key}) and after events related to
 * any associated {@link Key}.
 */
public interface SerializeListener {

    /**
     * Called during serialization.
     *
     * @param wrapper Wrapper around the {@link JsonGenerator} allowing for more functionality and easier methods provided.
     * @param key     The key that's currently serialized.
     */
    void onSerialize(JsonGeneratorWrapper wrapper, String key);
}
