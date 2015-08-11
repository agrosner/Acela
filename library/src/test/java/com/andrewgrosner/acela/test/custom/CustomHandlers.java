package com.andrewgrosner.acela.test.custom;

import com.andrewgrosner.acela.annotation.Key;
import com.andrewgrosner.acela.annotation.Translatable;

/**
 * Description:
 */
@Translatable(customTranslator = CustomHandler.class)
public class CustomHandlers {

    @Key
    String name;
}
