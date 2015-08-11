package com.andrewgrosner.acela.processor.definition.keys;

import com.fasterxml.jackson.core.JsonToken;
import com.andrewgrosner.acela.Acela;
import com.andrewgrosner.acela.annotation.Translatable;
import com.andrewgrosner.acela.processor.JSONMethodMap;
import com.andrewgrosner.acela.processor.AcelaProcessorManager;
import com.andrewgrosner.acela.processor.definition.TranslatableDefinition;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;

/**
 * Description:
 */
public class SimpleKeyDefinition extends BaseKeyDefinition {

    public boolean isPrimitive;
    public TypeElement variableTypeElement;

    public SimpleKeyDefinition(AcelaProcessorManager manager, KeyDefinition keyDefinition) {
        super(manager, keyDefinition);

        TypeMirror typeMirror = keyDefinition.element.asType();

        if (typeMirror.getKind().isPrimitive()) {
            variableTypeElement = manager.getTypes().boxedClass((PrimitiveType) typeMirror);
            isPrimitive = true;
        } else {
            this.variableTypeElement = manager.getElements().getTypeElement(
                    manager.getTypes().erasure(typeMirror).toString());
            primaryTypeElementIsTranslatable = (variableTypeElement.getAnnotation(Translatable.class) != null);
        }

        primaryTypeName = ClassName.get(variableTypeElement);
    }

    @Override
    public void addParseCode(CodeBlock.Builder codeBlockBuilder) {
        String getValue = JSONMethodMap.getMethodName(primaryTypeName.toString(), keyDefinition.defaultValue);
        boolean hasConverter = (getValue == null || getValue.isEmpty());

        if (keyDefinition.isMergeable) {
            codeBlockBuilder.beginControlFlow("if (parser.getCurrentToken() != $T.VALUE_NULL)", JsonToken.class);
        }
        if (!hasConverter) {
            codeBlockBuilder.addStatement("$L.$L = wrapper.$L", TranslatableDefinition.TRANSLATABLE_PARAM_NAME,
                    keyDefinition.variableName, getValue);
        } else {
            if (primaryTypeElementIsTranslatable) {
                codeBlockBuilder.addStatement("$L.$L = $L_translator.parse(parser)", TranslatableDefinition.TRANSLATABLE_PARAM_NAME,
                        keyDefinition.variableName, primaryTypeName.simpleName());
            } else if (keyDefinition.hasCustomTypeConverter) {
                codeBlockBuilder.addStatement("$L.$L = $L_converter.parse(wrapper, $L)",
                        TranslatableDefinition.TRANSLATABLE_PARAM_NAME, keyDefinition.variableName,
                        keyDefinition.keyName, keyDefinition.defaultValue);
            } else {
                codeBlockBuilder.addStatement("$L.$L = $T.getTypeConverterForClass($T.class).parse(wrapper, $S)",
                        TranslatableDefinition.TRANSLATABLE_PARAM_NAME,
                        keyDefinition.variableName, Acela.class, primaryTypeName,
                        keyDefinition.defaultValue);
            }
        }
        if (keyDefinition.isMergeable) {
            codeBlockBuilder.endControlFlow();
        }
    }

    @Override
    public void addSerializeCode(CodeBlock.Builder codeBlockBuilder) {
        String methodName = JSONMethodMap.getSerializeMethodName(primaryTypeName.toString());
        boolean hasConverter = (methodName == null || methodName.isEmpty());
        String accessStatement = TranslatableDefinition.TRANSLATABLE_PARAM_NAME + "." + keyDefinition.variableName;
        if (!isPrimitive && keyDefinition.checkForNullDuringSerialize) {
            codeBlockBuilder.beginControlFlow("if ($L != null)", accessStatement);
        }
        if (!hasConverter) {
            codeBlockBuilder.addStatement("wrapper.$L($S, $L)", methodName, keyDefinition.keyName, accessStatement);
        } else {
            if (primaryTypeElementIsTranslatable) {
                codeBlockBuilder.addStatement("generator.writeFieldName($S)", keyDefinition.keyName);
                codeBlockBuilder.addStatement("$L_translator.serialize($L, generator)", primaryTypeName.simpleName(), accessStatement);
            } else {
                if (keyDefinition.hasCustomTypeConverter) {
                    codeBlockBuilder.addStatement("$L_converter.serialize(wrapper, $S, $L.$L, $L)",
                            keyDefinition.keyName, keyDefinition.keyName,
                            TranslatableDefinition.TRANSLATABLE_PARAM_NAME, keyDefinition.variableName,
                            keyDefinition.defaultValue);
                } else {
                    codeBlockBuilder.addStatement(
                            "$T.getTypeConverterForClass($T.class).serialize(wrapper, $S, $L.$L, $L)",
                            Acela.class, ClassName.get(variableTypeElement), keyDefinition.keyName,
                            TranslatableDefinition.TRANSLATABLE_PARAM_NAME, keyDefinition.variableName,
                            keyDefinition.defaultValue);
                }
            }
        }
        if (!isPrimitive && keyDefinition.checkForNullDuringSerialize) {
            if (!keyDefinition.serializeNullObjects) {
                codeBlockBuilder.endControlFlow();
            } else {
                codeBlockBuilder.nextControlFlow("else");
                codeBlockBuilder.addStatement("generator.writeNullField($S)", keyDefinition.keyName);
                codeBlockBuilder.endControlFlow();
            }
        }
    }

    @Override
    public boolean hasReferencedTranslator() {
        return primaryTypeElementIsTranslatable;
    }

}
