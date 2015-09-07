package com.andrewgrosner.acela.app.link;

import com.andrewgrosner.acela.JsonParserWrapper;
import com.andrewgrosner.acela.annotation.ParseKeyListener;
import com.andrewgrosner.acela.annotation.Translatable;

/**
 * Description:
 */
@Translatable
public class ResponseHandler {

    @ParseKeyListener(key = "error")
    public void onParseError(JsonParserWrapper wrapper) {

    }

    @ParseKeyListener(key = "success")
    public void onParseSuccess(JsonParserWrapper wrapper) {

    }
}
