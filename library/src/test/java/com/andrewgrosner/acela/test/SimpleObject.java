package com.andrewgrosner.acela.test;

import com.andrewgrosner.acela.JsonGeneratorWrapper;
import com.andrewgrosner.acela.JsonParserWrapper;
import com.andrewgrosner.acela.event.ParseListener;
import com.andrewgrosner.acela.event.SerializeListener;
import com.andrewgrosner.acela.annotation.Key;
import com.andrewgrosner.acela.annotation.Translatable;

import java.util.Date;

/**
 * Description:
 */
@Translatable
public class SimpleObject implements ParseListener, SerializeListener{

    @Key
    String name;

    @Key
    boolean isSet;

    @Key(typeConverter = TestDateConverter.class)
    Date date;

    @Override
    public void onParse(JsonParserWrapper wrapper, String key) {

    }

    @Override
    public void onSerialize(JsonGeneratorWrapper wrapper, String key) {

    }
}
