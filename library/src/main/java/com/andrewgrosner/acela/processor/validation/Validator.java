package com.andrewgrosner.acela.processor.validation;

import com.andrewgrosner.acela.processor.AcelaProcessorManager;

/**
 * Author: andrewgrosner
 * Contributors: { }
 * Description:
 */
public interface Validator<ValidationDefinition> {

    public boolean validate(AcelaProcessorManager acelaProcessorManager, ValidationDefinition validationDefinition);
}
