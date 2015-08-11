package com.andrewgrosner.acela.processor.definition;

import com.google.common.collect.Sets;
import com.andrewgrosner.acela.processor.AcelaProcessorManager;
import com.andrewgrosner.acela.processor.writer.TypeDefinition;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Arrays;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Description: The base definition class that contains default methods for writing {@link TypeSpec}
 */
public abstract class BaseDefinition implements TypeDefinition {

    public static final Set<Modifier> METHOD_MODIFIERS = Sets.newHashSet(Modifier.PUBLIC, Modifier.FINAL);

    private final AcelaProcessorManager manager;

    public final ClassName elementClassName;

    public final TypeElement element;

    public ClassName definitionClassName;

    public final String packageName;

    public BaseDefinition(TypeElement typeElement, AcelaProcessorManager manager) {
        this.manager = manager;
        this.element = typeElement;
        elementClassName = ClassName.get(element);
        packageName = manager.getElements().getPackageOf(typeElement).toString();
    }

    protected void setDefinitionClassName(String definitionClassName) {
        this.definitionClassName = ClassName.get(elementClassName.packageName(), elementClassName.simpleName() + definitionClassName);
    }

    public ClassName getElementClassName() {
        return elementClassName;
    }

    public AcelaProcessorManager getManager() {
        return manager;
    }

    @Override
    public TypeSpec getTypeSpec() {
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(definitionClassName.simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterfaces(Arrays.asList(getImplementsClasses()))
                .addJavadoc("This class is generated from Translator. Please do not modify")
                .superclass(getExtendsClass());
        onWriteDefinition(typeSpecBuilder);
        return typeSpecBuilder.build();
    }

    protected TypeName getExtendsClass() {
        return null;
    }

    protected TypeName[] getImplementsClasses() {
        return new TypeName[0];
    }

    public abstract void onWriteDefinition(TypeSpec.Builder builder);
}
