package com.andrewgrosner.acela.processor.validation;

import com.andrewgrosner.acela.annotation.Translatable;
import com.andrewgrosner.acela.processor.AcelaProcessorManager;
import com.andrewgrosner.acela.processor.definition.TranslatableDefinition;

import java.util.Arrays;

/**
 * Description: Validates {@link Translatable} class.
 */
public class TranslatableValidator implements Validator<TranslatableDefinition> {

    @Override
    public boolean validate(AcelaProcessorManager manager, TranslatableDefinition translatableDefinition) {
        boolean success = true;

        if (!translatableDefinition.inheritedFields.isEmpty()) {
            manager.logError("There are a few inherited fields remaining that do not correspond to fields or are not accessible" +
                    " including: " +
                    Arrays.toString(translatableDefinition.inheritedFields.keySet().toArray()));
            success = false;
        }

        return success;
    }
}
