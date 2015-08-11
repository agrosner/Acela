package com.andrewgrosner.acela.app.parser;

import java.io.InputStream;
import java.util.List;

/**
 * Description:
 */
public interface BaseHandler<ParseClass> {

    List<ParseClass> parseData(Class<ParseClass> parseClass, InputStream jsonData);

    String serializeData(Class<ParseClass> parseClass, List<ParseClass> parseList);

    String getName();
}
