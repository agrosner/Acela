package com.andrewgrosner.acela.processor.handler;

import com.andrewgrosner.acela.processor.AcelaProcessorManager;
import com.andrewgrosner.acela.processor.definition.BaseDefinition;
import com.andrewgrosner.acela.processor.definition.TranslatableDefinition;
import com.andrewgrosner.acela.processor.validation.Validator;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Description: Provides a base implementation to handle top-level class annotations.
 */
public abstract class BaseHandler implements Handler {

    private Class<? extends Annotation> mAnnotationClass;

    public BaseHandler(Class<? extends Annotation> annotationClass) {
        mAnnotationClass = annotationClass;
    }

    @Override
    public void handle(AcelaProcessorManager manager, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(mAnnotationClass);
        if (elements.size() > 0) {
            for (Element element : elements) {
                onProcessElement(manager, element);
            }
        }
    }

    protected void onProcessElement(AcelaProcessorManager acelaProcessorManager, Element element) {
        BaseDefinition definition = createDefinition((TypeElement) element, acelaProcessorManager);
        Validator validator = getValidator();
        if (validator.validate(acelaProcessorManager, definition)) {
            try {
                if (shouldWriteFile() && (!(definition instanceof TranslatableDefinition) ||
                    ((TranslatableDefinition) definition).customHandlerMirror == null)) {
                    JavaFile javaFile = JavaFile.builder(acelaProcessorManager
                            .getElements().getPackageOf(element).toString(), definition.getTypeSpec()).build();
                    javaFile.writeTo(acelaProcessorManager.getFiler());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            onValidated(acelaProcessorManager, definition);
        }
    }

    protected boolean shouldWriteFile() {
        return true;
    }

    protected void onValidated(AcelaProcessorManager manager, BaseDefinition baseDefinition){

    }

    protected abstract BaseDefinition createDefinition(TypeElement typeElement, AcelaProcessorManager manager);

    protected abstract Validator getValidator();
}
