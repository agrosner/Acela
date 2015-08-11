package com.andrewgrosner.acela.test.statuslistener;

import com.andrewgrosner.acela.annotation.Key;
import com.andrewgrosner.acela.annotation.Translatable;
import com.andrewgrosner.acela.event.ParseStatusListener;

/**
 * Description:
 */
@Translatable
public class ParseStatusObject implements ParseStatusListener {


    @Key
    String name;

    @Override
    public void onPreParse() {

    }

    @Override
    public void onParseComplete() {

    }
}
