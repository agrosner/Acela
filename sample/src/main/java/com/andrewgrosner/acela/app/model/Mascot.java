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
public class Mascot {

    @Key
    @JsonField
    String id;

    @Key
    @JsonField
    String name;

    @Key
    @JsonField
    String[] type;
}
