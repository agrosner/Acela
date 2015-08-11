package com.andrewgrosner.acela.processor.definition.keys;

import com.andrewgrosner.acela.processor.AcelaProcessorManager;
import com.squareup.javapoet.ClassName;

/**
 * Description:
 */
public abstract class BaseKeyDefinition implements TypedKeyDefinition {

    public boolean primaryTypeElementIsTranslatable;
    public ClassName primaryTypeName;

    public final KeyDefinition keyDefinition;

    protected final AcelaProcessorManager manager;

    public BaseKeyDefinition(AcelaProcessorManager manager, KeyDefinition keyDefinition) {
        this.keyDefinition = keyDefinition;
        this.manager = manager;
    }

    @Override
    public ClassName getReferencedTranslatorClassName() {
        return primaryTypeName;
    }
}
