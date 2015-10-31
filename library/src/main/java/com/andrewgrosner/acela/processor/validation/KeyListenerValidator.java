package com.andrewgrosner.acela.processor.validation;

import com.andrewgrosner.acela.processor.AcelaProcessorManager;
import com.andrewgrosner.acela.processor.definition.KeyListenerDefinition;

/**
 * Description:
 */
public class KeyListenerValidator implements Validator<KeyListenerDefinition> {
    @Override
    public boolean validate(AcelaProcessorManager acelaProcessorManager, KeyListenerDefinition keyListenerDefinition) {
        boolean success = true;

        if (keyListenerDefinition.isParse && !keyListenerDefinition.methodName.startsWith("onParse")) {
            acelaProcessorManager.logError("The keylistener %1s must start with onParse", keyListenerDefinition.methodName);
            success = false;
        }

        if (!keyListenerDefinition.isParse && !keyListenerDefinition.methodName.startsWith("onSerialize")) {
            acelaProcessorManager.logError("The keylistener %1s must start with onSerialize", keyListenerDefinition.methodName);
            success = false;
        }

        if (keyListenerDefinition.parameterList.size() > 1) {
            acelaProcessorManager.logError("The keylistener %1s can only have 0 or 1 parameters",
                    keyListenerDefinition.methodName);
            success = false;
        }

        return success;
    }
}
