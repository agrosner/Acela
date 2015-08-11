package com.andrewgrosner.acela.processor.definition.keys;

import com.fasterxml.jackson.core.JsonToken;
import com.andrewgrosner.acela.TypeConverter;
import com.andrewgrosner.acela.annotation.Key;
import com.andrewgrosner.acela.annotation.Mergeable;
import com.andrewgrosner.acela.annotation.NotMergeable;
import com.andrewgrosner.acela.processor.AcelaProcessorManager;
import com.andrewgrosner.acela.processor.ProcessorUtils;
import com.andrewgrosner.acela.processor.definition.TranslatableDefinition;
import com.andrewgrosner.acela.processor.writer.CodeBlockAdder;
import com.squareup.javapoet.CodeBlock;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class KeyDefinition implements CodeBlockAdder {

    public enum Type {
        ARRAY,
        NORMAL,
        COLLECTION,
        MAP
    }

    public VariableElement element;
    public TypedKeyDefinition typedKeyDefinition;
    public String keyName;
    public String variableName;
    public String variableType;
    public Type type;
    boolean isPrimitive = false;
    public String defaultValue;
    boolean isMergeable = false;
    boolean notMergeable = false;
    public boolean hasKeyListenerAssociate;
    public boolean isParentMergeable;
    public boolean hasCustomTypeConverter;
    public TypeMirror customTypeConverterClassName;
    public boolean isFirst;
    public boolean isLast;
    public boolean checkForNullDuringSerialize;
    public boolean serializeNullCollections;
    public boolean serializeNullObjects;

    public KeyDefinition(AcelaProcessorManager manager, VariableElement element, Key key, TranslatableDefinition translatableDefinition) {
        this.element = element;

        serializeNullCollections = translatableDefinition.serializeNullCollections;
        serializeNullObjects = translatableDefinition.serializeNullObjects;
        this.isParentMergeable = translatableDefinition.isMergeable;
        this.checkForNullDuringSerialize = translatableDefinition.shouldCheckForNullDuringSerialize
                || key.checkForNullDuringSerialize();

        keyName = key.name();
        variableName = element.getSimpleName().toString();
        if (keyName == null || keyName.isEmpty()) {
            keyName = ProcessorUtils.getKeyName(variableName, translatableDefinition.keyNameRule);
        }
        customTypeConverterClassName = ProcessorUtils.getCustomTypeConverter(key);
        hasCustomTypeConverter = customTypeConverterClassName != null &&
                !customTypeConverterClassName.toString().equals(TypeConverter.class.getCanonicalName());

        isMergeable = element.getAnnotation(Mergeable.class) != null;
        if (!isMergeable) {
            notMergeable = element.getAnnotation(NotMergeable.class) != null;
            if (notMergeable) {
                isParentMergeable = false;
            }
        }

        defaultValue = key.defValue();

        TypeMirror typeMirror = element.asType();

        if (typeMirror.getKind().isPrimitive()) {
            typedKeyDefinition = new SimpleKeyDefinition(manager, this);
            type = Type.NORMAL;
            isPrimitive = true;

            boolean emptyDefaultValue = defaultValue == null || defaultValue.isEmpty();

            if (emptyDefaultValue) {
                if (isMergeable || isParentMergeable) {
                    defaultValue = String.format("%s.%1s", TranslatableDefinition.TRANSLATABLE_PARAM_NAME, variableName);
                } else {
                    TypeKind typeKind = typeMirror.getKind();
                    if (typeKind.equals(TypeKind.LONG)) {
                        defaultValue = "0l";
                    } else if (typeKind.equals(TypeKind.DOUBLE)) {
                        defaultValue = "0d";
                    } else if (typeKind.equals(TypeKind.FLOAT)) {
                        defaultValue = "0f";
                    } else if (typeKind.equals(TypeKind.BOOLEAN)) {
                        defaultValue = "false";
                    } else if (typeKind.equals(TypeKind.SHORT) || typeKind.equals(TypeKind.BYTE) ||
                            typeKind.equals(TypeKind.INT)) {
                        defaultValue = "0";
                    } else if (typeKind.equals(TypeKind.CHAR)) {
                        defaultValue = "\'\'";
                    } else {
                        defaultValue = "null";
                    }
                }
            }
        } else {
            boolean emptyDefaultValue = defaultValue == null || defaultValue.length() == 0;
            if (emptyDefaultValue) {
                if (isMergeable || isParentMergeable) {
                    defaultValue = String.format("%1s.%1s", TranslatableDefinition.TRANSLATABLE_PARAM_NAME, variableName);
                } else {
                    defaultValue = "null";
                }
            }

            if (typeMirror instanceof ArrayType) {
                type = Type.ARRAY;
                typedKeyDefinition = new ArrayCollectionKeyDefinition(manager, this);
            } else {
                TypeElement variableTypeElement = manager.getElements().getTypeElement(
                        manager.getTypes().erasure(element.asType()).toString());

                if ((ProcessorUtils.implementsClass(manager, Collection.class.getCanonicalName(),
                        variableTypeElement) ||
                        ProcessorUtils.implementsClass(manager, List.class.getCanonicalName(), variableTypeElement)) &&
                        typeMirror instanceof DeclaredType) {
                    type = Type.COLLECTION;
                    typedKeyDefinition = new ArrayCollectionKeyDefinition(manager, this);
                } else if (ProcessorUtils.implementsClass(manager, Map.class.getCanonicalName(), variableTypeElement) &&
                        typeMirror instanceof DeclaredType) {

                    type = Type.MAP;
                    typedKeyDefinition = new MapKeyDefinition(manager, this);
                } else {
                    type = Type.NORMAL;
                    typedKeyDefinition = new SimpleKeyDefinition(manager, this);
                }
            }
        }

        variableType = typeMirror.toString();

    }

    @Override
    public void addCode(CodeBlock.Builder codeBlockBuilder) {
        String condition = String.format("if (\"%1s\".equals(%1s))", keyName, "fieldName");
        if (isFirst) {
            codeBlockBuilder.beginControlFlow(condition);
        } else {
            codeBlockBuilder.nextControlFlow("else " + condition);
        }

        typedKeyDefinition.addParseCode(codeBlockBuilder);
    }

    public void addSerializeCode(CodeBlock.Builder codeBlockBuilder) {
        typedKeyDefinition.addSerializeCode(codeBlockBuilder);
    }

    public boolean hasDefaultValue() {
        return defaultValue != null && !defaultValue.isEmpty() && !defaultValue.equals("null");
    }

    static void emitCurrentTokenIf(String tokenCompare, CodeBlock.Builder codeBlockBuilder) {
        codeBlockBuilder.beginControlFlow("if (parser.getCurrentToken() == $T.$L)", JsonToken.class, tokenCompare);
    }

    static void emitAssignment(String variableName, String value, CodeBlock.Builder codeBlockBuilder) {
        codeBlockBuilder.addStatement("$L.$L = $L", TranslatableDefinition.TRANSLATABLE_PARAM_NAME, variableName, value);
    }
}
