package com.andrewgrosner.acela.app.parser;

import com.andrewgrosner.acela.Acela;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Description:
 */
public class ParserHandler<ParseClass> implements BaseHandler<ParseClass> {

    @Override
    public List<ParseClass> parseData(Class<ParseClass> parseClass, InputStream jsonData) {
        return Acela.getTranslator(parseClass).parseList(jsonData);
    }

    @Override
    public String serializeData(Class<ParseClass> parseClass, List<ParseClass> parseList) {
        try {
            return Acela.getTranslator(parseClass).serializeList(parseList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getName() {
        return "Acela";
    }
}
