package com.andrewgrosner.acela.test.complex;

import com.andrewgrosner.acela.JsonGeneratorWrapper;
import com.andrewgrosner.acela.JsonParserWrapper;
import com.andrewgrosner.acela.annotation.InheritedField;
import com.andrewgrosner.acela.annotation.Key;
import com.andrewgrosner.acela.annotation.ParseKeyListener;
import com.andrewgrosner.acela.annotation.SerializeKeyListener;
import com.andrewgrosner.acela.annotation.Translatable;
import com.andrewgrosner.acela.event.ParseListener;
import com.andrewgrosner.acela.event.SerializeListener;
import com.andrewgrosner.acela.test.SimpleObject;

import java.util.List;
import java.util.Map;

/**
 * Description:
 */
@Translatable(inheritedKeys = {@InheritedField(keyDefinition = @Key, fieldName = "isAField")})
public class ComplexObject extends BaseObject implements ParseListener, SerializeListener {

    @Key(name = "somethingElse")
    String name;

    @Key
    List<SimpleObject> simpleObjectList;

    @Key
    SimpleObject[] simpleObjectArray;

    @Key
    Map<String, SimpleObject> simpleObjectMap;

    String handledManually;

    boolean hasManualKey;

    boolean hasManualSerializeKey;

    boolean[] fieldsCalled = new boolean[5];

    @Override
    public void onParse(JsonParserWrapper wrapper, String key) {
        if ("handle".equals(key)) {
            handledManually = wrapper.getValueAsString("manual");
        }
    }

    @ParseKeyListener(key = "manualKey")
    public void onParseManualKey() {
        hasManualKey = true;
    }

    @SerializeKeyListener(key = "manualKey")
    public void onSerializeManualKey() {
        hasManualSerializeKey = true;
    }

    @Override
    public void onSerialize(JsonGeneratorWrapper wrapper, String key) {
        if ("somethingElse".equals(key)) {
            fieldsCalled[0] = true;
        } else if ("simpleObjectList".equals(key)) {
            fieldsCalled[1] = true;
        } else if ("simpleObjectArray".equals(key)) {
            fieldsCalled[2] = true;
        } else if ("simpleObjectMap".equals(key)) {
            fieldsCalled[3] = true;
        } else if ("isAField".equals(key)) {
            fieldsCalled[4] = true;
        }
    }
}
