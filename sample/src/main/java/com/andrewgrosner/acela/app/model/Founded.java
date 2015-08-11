package com.andrewgrosner.acela.app.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.andrewgrosner.acela.annotation.Key;
import com.andrewgrosner.acela.annotation.Translatable;

/**
 * Description:
 */
@Translatable
@JsonObject
public class Founded {

    @Key
    @JsonField
    String value;
}
