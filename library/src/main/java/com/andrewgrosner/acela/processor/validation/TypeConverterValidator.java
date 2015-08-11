package com.andrewgrosner.acela.processor.validation;

import com.andrewgrosner.acela.TypeConverter;
import com.andrewgrosner.acela.processor.AcelaProcessorManager;
import com.andrewgrosner.acela.processor.definition.TypeConverterDefinition;

/**
 * Description: Validates {@link TypeConverter} classes.
 */
public class TypeConverterValidator implements Validator<TypeConverterDefinition> {

    @Override
    public boolean validate(AcelaProcessorManager acelaProcessorManager, TypeConverterDefinition typeConverterDefinition) {
        return true;
    }
}
