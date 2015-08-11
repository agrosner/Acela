package com.andrewgrosner.acela.app.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.andrewgrosner.acela.annotation.Key;
import com.andrewgrosner.acela.annotation.Translatable;

import java.util.List;

/**
 * Description: A team that contains roster
 */
@Translatable
@JsonObject
public class Team {

    @Key
    @JsonField
    List<Player> roster;

    @Key
    @JsonField
    CurrentManager[] current_manager;

    @Key
    @JsonField
    Stadium[] stadiums;

    @Key
    @JsonField
    Founded[] founded;

    @Key
    @JsonField
    League league;

    @Key
    @JsonField
    Division division;

    @Key
    @JsonField
    String type;

    @Key
    @JsonField
    Mascot[] mascot;

    @Key
    @JsonField
    String name;

    public String getName() {
        return name;
    }

    public List<Player> getRoster() {
        return roster;
    }

    public CurrentManager[] getCoaches() {
        return current_manager;
    }

    public String getType() {
        return type;
    }

}
