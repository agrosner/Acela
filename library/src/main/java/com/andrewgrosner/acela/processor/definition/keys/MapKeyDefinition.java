package com.andrewgrosner.acela.processor.definition.keys;

import com.andrewgrosner.acela.Acela;
import com.andrewgrosner.acela.annotation.Translatable;
import com.andrewgrosner.acela.processor.AcelaProcessorManager;
import com.andrewgrosner.acela.processor.JSONMethodMap;
import com.andrewgrosner.acela.processor.definition.TranslatableDefinition;
import com.fasterxml.jackson.core.JsonToken;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * Description:
 */
public class MapKeyDefinition extends BaseKeyDefinition {

    public ClassName firstTypeName;

    public String erasedType;

    public String secondaryComponentType;

    public boolean primaryTypeElementIsTranslatable;

    public MapKeyDefinition(AcelaProcessorManager manager, KeyDefinition keyDefinition) {
        super(manager, keyDefinition);

        TypeMirror typeMirror = keyDefinition.element.asType();
        TypeElement secondComponentTypeElement = null;
        TypeElement firstComponentTypeElement = null;

        if (((DeclaredType) typeMirror).getTypeArguments().size() > 0) {
            List<? extends TypeMirror> typeMirrors = ((DeclaredType) typeMirror).getTypeArguments();
            firstComponentTypeElement = manager.getElements().getTypeElement(typeMirrors.get(0).toString());
            secondComponentTypeElement = manager.getElements().getTypeElement(typeMirrors.get(1).toString());

            if (firstComponentTypeElement != null && secondComponentTypeElement != null) {
                secondaryComponentType = secondComponentTypeElement.asType().toString();

                primaryTypeElementIsTranslatable = (secondComponentTypeElement.getAnnotation(Translatable.class) !=
                        null);
            }
        }
        erasedType = manager.getTypes().erasure(typeMirror).toString();
        firstTypeName = ClassName.get(firstComponentTypeElement);
        primaryTypeName = ClassName.get(secondComponentTypeElement);
    }

    @Override
    public void addParseCode(CodeBlock.Builder codeBlockBuilder) {
        String getValue = JSONMethodMap.getMethodName(primaryTypeName.toString(), keyDefinition.defaultValue);
        boolean hasConverter = (getValue == null || getValue.isEmpty());

        TypeName actualType = ClassName.get(keyDefinition.element.asType());
        TypeName instantiationType = actualType;
        if (erasedType.equals(Map.class.getCanonicalName())) {
            instantiationType = ClassName.get(HashMap.class);
        }

        KeyDefinition.emitCurrentTokenIf("START_OBJECT", codeBlockBuilder);
        codeBlockBuilder.addStatement("$T map = new $T()", actualType, instantiationType);

        codeBlockBuilder.beginControlFlow("while ($L.nextToken() != $T.END_OBJECT) ", "parser", JsonToken.class);
        codeBlockBuilder.addStatement("String key = parser.getText()");
        codeBlockBuilder.addStatement("parser.nextToken()");
        KeyDefinition.emitCurrentTokenIf("VALUE_NULL", codeBlockBuilder);
        codeBlockBuilder.addStatement("map.put($L, null)", "key");
        codeBlockBuilder.nextControlFlow("else");
        if (primaryTypeElementIsTranslatable) {
            codeBlockBuilder.addStatement("map.put($L, $T.getTranslator($T.class).parse(parser))", "key", Acela.class,
                    primaryTypeName);
        } else if (hasConverter) {
            codeBlockBuilder.addStatement("map.put($L, $T.getTypeConverterForClass($T.class).parse(wrapper, $L))", "key",
                    Acela.class, primaryTypeName, keyDefinition.defaultValue);
        } else {
            codeBlockBuilder.addStatement("map.put($L.$L)", "key", "parser", getValue);

        }
        codeBlockBuilder.endControlFlow();
        codeBlockBuilder.endControlFlow();
        KeyDefinition.emitAssignment(keyDefinition.variableName, "map", codeBlockBuilder);

        if (!keyDefinition.isMergeable) {
            codeBlockBuilder.nextControlFlow("else");
            KeyDefinition.emitAssignment(keyDefinition.variableName, "null", codeBlockBuilder);
        }
        codeBlockBuilder.endControlFlow();
    }

    @Override
    public void addSerializeCode(CodeBlock.Builder codeBlockBuilder) {
        String methodName = JSONMethodMap.getSerializeMethodName(primaryTypeName.toString());
        boolean hasConverter = (methodName == null || methodName.isEmpty());

        String fieldAccess = "map" + keyDefinition.variableName;

        codeBlockBuilder.addStatement("$T<$T, $T> $L = $L.$L", Map.class, firstTypeName,
                primaryTypeName, fieldAccess,
                TranslatableDefinition.TRANSLATABLE_PARAM_NAME, keyDefinition.variableName);

        if (keyDefinition.checkForNullDuringSerialize) {
            codeBlockBuilder.beginControlFlow("if ($L != null)", fieldAccess);
        }

        codeBlockBuilder.addStatement("generator.writeFieldName($S)", keyDefinition.keyName);
        codeBlockBuilder.addStatement("generator.writeStartObject()");
        codeBlockBuilder.beginControlFlow("for ($T.Entry<$T, $T> entry: $L.entrySet())", Map.class,
                firstTypeName, primaryTypeName, fieldAccess);
        codeBlockBuilder.addStatement("generator.writeFieldName(entry.getKey().toString())");
        if (!hasConverter) {
            codeBlockBuilder.addStatement("wrapper.$L($S, $L)", methodName, keyDefinition.keyName,
                    "entry.getValue()");
        } else {
            if (!primaryTypeElementIsTranslatable) {
                codeBlockBuilder.addStatement(
                        "$T.getTypeConverterForClass($T.class).serialize(wrapper, $S, $L, $L)",
                        Acela.class, primaryTypeName, keyDefinition.keyName, "entry.getValue()",
                        keyDefinition.defaultValue);
            } else {
                codeBlockBuilder.addStatement("$T.getTranslator($T.class).serialize($L, generator)", Acela.class,
                        primaryTypeName, "entry.getValue()");
            }
        }

        if (keyDefinition.checkForNullDuringSerialize) {
            codeBlockBuilder.endControlFlow();
        }
        codeBlockBuilder.addStatement("generator.writeEndObject()");
        codeBlockBuilder.endControlFlow();
    }

    @Override
    public boolean hasReferencedTranslator() {
        return primaryTypeElementIsTranslatable;
    }

}