package com.andrewgrosner.acela.processor.validation;

import com.google.common.collect.Lists;
import com.andrewgrosner.acela.annotation.Mergeable;
import com.andrewgrosner.acela.annotation.NotMergeable;
import com.andrewgrosner.acela.processor.AcelaProcessorManager;
import com.andrewgrosner.acela.processor.definition.keys.ArrayCollectionKeyDefinition;
import com.andrewgrosner.acela.processor.definition.keys.KeyDefinition;
import com.andrewgrosner.acela.processor.definition.keys.MapKeyDefinition;
import com.andrewgrosner.acela.processor.definition.keys.SimpleKeyDefinition;

import java.util.List;

/**
 * Author: andrewgrosner
 * Contributors: { }
 * Description: Runs a validation on a {@link KeyDefinition} to ensure it
 * is constructed properly.
 */
public class KeyValidator implements Validator<KeyDefinition> {

    private AcelaProcessorManager manager;

    private List<String> mKeyList = Lists.newArrayList();

    public KeyValidator(AcelaProcessorManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean validate(AcelaProcessorManager acelaProcessorManager, KeyDefinition keyDefinition) {
        if (isNull(keyDefinition.keyName)) {
            manager.logError("Key was found null for %1s", keyDefinition.keyName,
                             keyDefinition.element.asType().toString());
            return false;
        }
        boolean success = false;
        if (mKeyList.contains(keyDefinition.keyName)) {
            success = false;
            manager.logError("Duplicate fields from %1s request the same key %1s", keyDefinition.variableName,
                             keyDefinition.keyName);
        } else {
            mKeyList.add(keyDefinition.keyName);
            if (keyDefinition.type.equals(KeyDefinition.Type.NORMAL)) {
                success = validateNormalType(keyDefinition);
            } else if (keyDefinition.type.equals(KeyDefinition.Type.ARRAY) ||
                       keyDefinition.type.equals(KeyDefinition.Type.COLLECTION)) {
                success = validateListType(keyDefinition);
            } else if (keyDefinition.type.equals(KeyDefinition.Type.MAP)) {
                success = validateMapType(keyDefinition);
            }
            if(!success) {
                manager.logError("Invalid type found for: " + keyDefinition.variableName);
            }
        }

        if(keyDefinition.hasDefaultValue() && !keyDefinition.type.equals(KeyDefinition.Type.NORMAL)){
            manager.logError("Only normal, non-translatable keys can have default values for:" + keyDefinition.variableName + " " + keyDefinition.defaultValue);
            success = false;
        }

        if (keyDefinition.element.getAnnotation(Mergeable.class) != null &&
            keyDefinition.element.getAnnotation(NotMergeable.class) != null) {
            manager.logError("Found a field with both Mergeable and NotMergeable. NotMergeable will override" +
                             "Mergeable so remove Mergeable.");
            success = false;
        }

        return success;
    }

    /**
     * @param objects
     * @return true if any of the objects are null
     */
    private static boolean isNull(Object... objects) {
        for (Object ob : objects) {
            if (ob == null) {
                return true;
            }
        }
        return false;
    }

    private boolean validateNormalType(KeyDefinition keyDefinition) {
        SimpleKeyDefinition simpleKeyDefinition = ((SimpleKeyDefinition) keyDefinition.typedKeyDefinition);
        return simpleKeyDefinition.variableTypeElement != null;
    }

    private boolean validateMapType(KeyDefinition keyDefinition) {
        MapKeyDefinition mapKeyDefinition = ((MapKeyDefinition) keyDefinition.typedKeyDefinition);
        if (isNull(mapKeyDefinition.firstTypeName, mapKeyDefinition.primaryTypeName)) {
            manager.logError("Map type for %1s should define type parameters.", keyDefinition.variableName);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateListType(KeyDefinition keyDefinition) {
        ArrayCollectionKeyDefinition arrayCollectionKeyDefinition = ((ArrayCollectionKeyDefinition) keyDefinition.typedKeyDefinition);
        if (isNull(arrayCollectionKeyDefinition.primaryTypeName)) {
            manager.logError("List/array type for %1s has a non-supported type element", keyDefinition.variableName);
            return false;
        } else {
            return true;
        }
    }

}
