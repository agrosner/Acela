package com.andrewgrosner.acela.processor.handler;

import com.andrewgrosner.acela.annotation.Translatable;
import com.andrewgrosner.acela.processor.AcelaProcessorManager;
import com.andrewgrosner.acela.processor.definition.BaseDefinition;
import com.andrewgrosner.acela.processor.definition.TranslatableDefinition;
import com.andrewgrosner.acela.processor.validation.TranslatableValidator;
import com.andrewgrosner.acela.processor.validation.Validator;

import javax.lang.model.element.TypeElement;

/**
 * Description:
 */
public class TranslatableHandler extends BaseHandler {

    public TranslatableHandler() {
        super(Translatable.class);
    }

    @Override
    protected BaseDefinition createDefinition(TypeElement typeElement, AcelaProcessorManager manager) {
        return new TranslatableDefinition(typeElement, manager);
    }

    @Override
    protected Validator getValidator() {
        return new TranslatableValidator();
    }
}
