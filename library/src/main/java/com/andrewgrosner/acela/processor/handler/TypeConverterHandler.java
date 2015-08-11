package com.andrewgrosner.acela.processor.handler;

import com.andrewgrosner.acela.annotation.TypeConverter;
import com.andrewgrosner.acela.processor.AcelaProcessorManager;
import com.andrewgrosner.acela.processor.definition.BaseDefinition;
import com.andrewgrosner.acela.processor.definition.TypeConverterDefinition;
import com.andrewgrosner.acela.processor.validation.TypeConverterValidator;
import com.andrewgrosner.acela.processor.validation.Validator;

import javax.lang.model.element.TypeElement;

/**
 * Description:
 */
public class TypeConverterHandler extends BaseHandler {
    public TypeConverterHandler() {
        super(TypeConverter.class);
    }

    @Override
    protected BaseDefinition createDefinition(TypeElement typeElement, AcelaProcessorManager manager) {
        return new TypeConverterDefinition(typeElement, manager);
    }

    @Override
    protected Validator getValidator() {
        return new TypeConverterValidator();
    }

    @Override
    protected boolean shouldWriteFile() {
        return false;
    }

    @Override
    protected void onValidated(AcelaProcessorManager manager, BaseDefinition baseDefinition) {
        manager.addTypeConverterDefinition(baseDefinition.element, (TypeConverterDefinition) baseDefinition);
    }

}
