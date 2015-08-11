package com.andrewgrosner.acela.event;

/**
 * Description: Implement this in your {@link com.andrewgrosner.acela.annotation.Translatable}
 * class and these methods get called during serialization.
 */
public interface SerializeStatusListener {

    void onPreSerialize();

    void onPostSerialize();
}
