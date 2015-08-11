package com.andrewgrosner.acela.app.parser;

import com.bluelinelabs.logansquare.LoganSquare;
import com.andrewgrosner.acela.app.model.SimpleDateParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Description:
 */
public class LoganSquareHandler<ParseClass> implements BaseHandler<ParseClass> {

    public LoganSquareHandler() {
        LoganSquare.registerTypeConverter(Date.class, new SimpleDateParser());
    }

    @Override
    public List<ParseClass> parseData(Class<ParseClass> parseClass, InputStream jsonData) {
        try {
            return LoganSquare.parseList(jsonData, parseClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String serializeData(Class<ParseClass> parseClass, List<ParseClass> parseList) {
        try {
            return LoganSquare.serialize(parseList, parseClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getName() {
        return "Logan Square";
    }
}
