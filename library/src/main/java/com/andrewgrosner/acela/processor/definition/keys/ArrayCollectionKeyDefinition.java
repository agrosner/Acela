package com.andrewgrosner.acela.processor.definition.keys;

import com.andrewgrosner.acela.Acela;
import com.fasterxml.jackson.core.JsonToken;
import com.andrewgrosner.acela.annotation.Translatable;
import com.andrewgrosner.acela.processor.JSONMethodMap;
import com.andrewgrosner.acela.processor.AcelaProcessorManager;
import com.andrewgrosner.acela.processor.definition.TranslatableDefinition;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * Description: Represents an array and collection key type.
 */
public class ArrayCollectionKeyDefinition extends BaseKeyDefinition {

    public String componentType;

    private final KeyDefinition.Type type;

    private final String variableName;
    private String erasedType;

    public ArrayCollectionKeyDefinition(AcelaProcessorManager manager, KeyDefinition keyDefinition) {
        super(manager, keyDefinition);

        TypeMirror typeMirror = keyDefinition.element.asType();
        TypeElement componentTypeElement = null;

        type = keyDefinition.type;
        variableName = keyDefinition.variableName;
        if (typeMirror instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) typeMirror;
            componentTypeElement = manager.getElements().getTypeElement(arrayType.getComponentType().toString());
            primaryTypeElementIsTranslatable = (componentTypeElement.getAnnotation(Translatable.class) != null);
            componentType = componentTypeElement.asType().toString();
            erasedType = "";
        } else if (type.equals(KeyDefinition.Type.COLLECTION)) {
            erasedType = manager.getTypes().erasure(typeMirror).toString();
            if (((DeclaredType) typeMirror).getTypeArguments().size() > 0) {
                componentTypeElement = manager.getElements().getTypeElement(
                        ((DeclaredType) typeMirror).getTypeArguments().get(0).toString());
            } else {
                componentTypeElement = manager.getElements().getTypeElement(Object.class.getCanonicalName());
            }
            primaryTypeElementIsTranslatable = (componentTypeElement.getAnnotation(Translatable.class) != null);
            componentType = componentTypeElement.toString();
        }

        primaryTypeName = ClassName.get(componentTypeElement);
    }

    @Override
    public void addParseCode(CodeBlock.Builder codeBlockBuilder) {

        String getValue = JSONMethodMap.getMethodName(componentType, keyDefinition.defaultValue);
        boolean hasConverter = (getValue == null || getValue.isEmpty());

        String instantiationType = keyDefinition.element.asType().toString();
        if (type.equals(KeyDefinition.Type.COLLECTION)) {
            if (erasedType.equals(Collection.class.getCanonicalName())) {
                instantiationType = ArrayList.class.getCanonicalName();
            } else if (erasedType.equals(List.class.getCanonicalName())) {
                instantiationType = ArrayList.class.getCanonicalName();
            } else {
                instantiationType = erasedType;
            }
            instantiationType += "<>";
        } else if (type.equals(KeyDefinition.Type.ARRAY)) {
            instantiationType = ArrayList.class.getCanonicalName() + "<>";
        }

        KeyDefinition.emitCurrentTokenIf("START_ARRAY", codeBlockBuilder);

        codeBlockBuilder.addStatement("$T list = new $L()", ParameterizedTypeName.get(ClassName.get(List.class),
                primaryTypeName), instantiationType);
        codeBlockBuilder.beginControlFlow("while ($L.nextToken() != $T.END_ARRAY) ", "parser", JsonToken.class);
        if (primaryTypeElementIsTranslatable) {
            codeBlockBuilder.addStatement("list.add($L_translator.parse(parser))", primaryTypeName.simpleName());
        } else if (hasConverter) {
            codeBlockBuilder.addStatement("list.add($T.getTypeConverterForClass($T.class).parse(wrapper, $L))",
                    Acela.class, primaryTypeName, keyDefinition.defaultValue);
        } else {
            codeBlockBuilder.addStatement("list.add($L.$L)", "parser", getValue);
        }

        codeBlockBuilder.endControlFlow();

        if (type.equals(KeyDefinition.Type.ARRAY)) {
            codeBlockBuilder.addStatement("$L.$L = $L.toArray(new $T[$L.size()])", TranslatableDefinition.TRANSLATABLE_PARAM_NAME,
                    variableName, "list", primaryTypeName, "list");
        } else {
            KeyDefinition.emitAssignment(variableName, "list", codeBlockBuilder);
        }

        if (!keyDefinition.isMergeable) {
            codeBlockBuilder.nextControlFlow("else");
            KeyDefinition.emitAssignment(variableName, "null", codeBlockBuilder);
        }
        codeBlockBuilder.endControlFlow();
    }

    @Override
    public void addSerializeCode(CodeBlock.Builder codeBlockBuilder) {
        String fieldAccess = "";
        String methodName = JSONMethodMap.getListCollectionMethodName(componentType);
        boolean hasConverter = (methodName == null || methodName.isEmpty());
        if (type.equals(KeyDefinition.Type.COLLECTION)) {
            fieldAccess = "coll" + variableName;
            codeBlockBuilder.addStatement("$T<$T> $L = $L.$L", Collection.class, primaryTypeName,
                    fieldAccess, TranslatableDefinition.TRANSLATABLE_PARAM_NAME, variableName);
        } else if (type.equals(KeyDefinition.Type.ARRAY)) {
            fieldAccess = "array" + variableName;
            codeBlockBuilder.addStatement("$T[] $L = $L.$L", primaryTypeName, fieldAccess,
                    TranslatableDefinition.TRANSLATABLE_PARAM_NAME, variableName);
        }

        if (keyDefinition.checkForNullDuringSerialize) {
            codeBlockBuilder.beginControlFlow("if ($L != null)", fieldAccess);
        }
        codeBlockBuilder.addStatement("generator.writeFieldName($S)", keyDefinition.keyName);
        codeBlockBuilder.addStatement("generator.writeStartArray()");
        codeBlockBuilder.beginControlFlow("for ($T $L_$L: $L)", primaryTypeName,
                fieldAccess, TranslatableDefinition.TRANSLATABLE_PARAM_NAME, fieldAccess);
        codeBlockBuilder.beginControlFlow("if ($L_$L != null)", fieldAccess, TranslatableDefinition.TRANSLATABLE_PARAM_NAME);
        if (!hasConverter) {
            codeBlockBuilder.addStatement("generator.$L($L_$L)", methodName, fieldAccess, TranslatableDefinition.TRANSLATABLE_PARAM_NAME);
        } else {
            if (!primaryTypeElementIsTranslatable) {
                codeBlockBuilder.addStatement(
                        "$T.getTypeConverterForClass($T.class).serialize(wrapper, $S, $L_$L, $L)",
                        Acela.class, componentType, keyDefinition.keyName, variableName,
                        TranslatableDefinition.TRANSLATABLE_PARAM_NAME, keyDefinition.defaultValue);
            } else {
                codeBlockBuilder.addStatement("$L_translator.serialize($L_$L, generator)", primaryTypeName.simpleName(),
                        fieldAccess, TranslatableDefinition.TRANSLATABLE_PARAM_NAME);
            }
        }
        if (!keyDefinition.serializeNullCollections) {
            codeBlockBuilder.endControlFlow();
        } else {
            codeBlockBuilder.nextControlFlow("else");
            codeBlockBuilder.addStatement("generator.writeNull()");
            codeBlockBuilder.endControlFlow();
        }

        if (keyDefinition.checkForNullDuringSerialize) {
            codeBlockBuilder.endControlFlow();
        }
        codeBlockBuilder.addStatement("generator.writeEndArray()");
        codeBlockBuilder.endControlFlow();
    }

    @Override
    public boolean hasReferencedTranslator() {
        return primaryTypeElementIsTranslatable;
    }

}
