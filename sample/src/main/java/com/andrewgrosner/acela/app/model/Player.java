package com.andrewgrosner.acela.app.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.andrewgrosner.acela.annotation.Key;
import com.andrewgrosner.acela.annotation.Translatable;

import java.util.Date;

/**
 * Description: Only a subset of data that represents a player
 */
@Translatable(checkForNullDuringSerialize = false)
@JsonObject
public class Player {

    @Key
    @JsonField
    String player;

    @Key
    @JsonField
    int number;

    @Key
    @JsonField
    String[] position;

    @Key
    @JsonField
    Date to;

    @Key
    @JsonField
    Date from;

}
