package com.andrewgrosner.acela.processor.definition;

import com.andrewgrosner.acela.Acela;
import com.andrewgrosner.acela.JsonGeneratorWrapper;
import com.andrewgrosner.acela.JsonParserWrapper;
import com.andrewgrosner.acela.annotation.InheritedField;
import com.andrewgrosner.acela.annotation.Key;
import com.andrewgrosner.acela.annotation.Mergeable;
import com.andrewgrosner.acela.annotation.ParseKeyListener;
import com.andrewgrosner.acela.annotation.SerializeKeyListener;
import com.andrewgrosner.acela.annotation.Translatable;
import com.andrewgrosner.acela.event.ParseListener;
import com.andrewgrosner.acela.event.SerializeListener;
import com.andrewgrosner.acela.handler.BaseTranslator;
import com.andrewgrosner.acela.handler.ParseHandler;
import com.andrewgrosner.acela.handler.SerializeHandler;
import com.andrewgrosner.acela.processor.AcelaProcessorManager;
import com.andrewgrosner.acela.processor.ProcessorUtils;
import com.andrewgrosner.acela.processor.definition.keys.KeyDefinition;
import com.andrewgrosner.acela.processor.validation.KeyListenerValidator;
import com.andrewgrosner.acela.processor.validation.KeyValidator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Description: Holds information on a {@link Translatable}
 */
public class TranslatableDefinition extends BaseDefinition {

    public static final String TRANSLATABLE_CLASS_SUFFIX = "_Translator";
    public static final String TRANSLATABLE_PARAM_NAME = "item";

    public static String getReferencedTranslatorFieldName(String shortclassName) {
        return shortclassName + "_translator";
    }

    public static TypeName getReferencedTranslatorTypeName(ClassName referencedTranslatorType) {
        return ClassName.get(referencedTranslatorType.packageName(), referencedTranslatorType.simpleName() + TRANSLATABLE_CLASS_SUFFIX);
    }

    public boolean isParseListener = false;
    public boolean isSerializeListener = false;

    public TypeMirror parseHandlerMirror;
    public TypeMirror serializeHandlerMirror;
    public TypeMirror customHandlerMirror;

    public boolean shouldCheckForNullDuringSerialize = true;
    public boolean serializeNullObjects = false;
    public boolean serializeNullCollections = false;
    public boolean isMergeable = false;
    public boolean shouldThrowExceptions = false;
    public Translatable.KeyNameRule keyNameRule;

    public Map<String, Key> inheritedFields = Maps.newLinkedHashMap();

    public Map<String, KeyDefinition> keyDefinitions = Maps.newLinkedHashMap();
    public Map<String, KeyListenerDefinition> parseKeyListenerDefinitions = Maps.newLinkedHashMap();
    public Map<String, KeyListenerDefinition> serializeKeyListenerDefinitions = Maps.newLinkedHashMap();
    public Map<KeyDefinition, TypeName> customTypeConverters = Maps.newLinkedHashMap();
    public Set<ClassName> referencedTranslators = Sets.newHashSet();

    public TranslatableDefinition(TypeElement typeElement, AcelaProcessorManager manager) {
        super(typeElement, manager);
        setDefinitionClassName(TRANSLATABLE_CLASS_SUFFIX);

        isMergeable = typeElement.getAnnotation(Mergeable.class) != null;

        Translatable translatable = typeElement.getAnnotation(Translatable.class);

        keyNameRule = translatable.keyNameRule();

        List<InheritedField> inheritedFieldList = Arrays.asList(translatable.inheritedKeys());
        for (InheritedField inheritedField : inheritedFieldList) {
            inheritedFields.put(inheritedField.fieldName(), inheritedField.keyDefinition());
        }

        shouldCheckForNullDuringSerialize = translatable.checkForNullDuringSerialize();
        shouldThrowExceptions = translatable.shouldThrowFieldExceptions();
        serializeNullCollections = translatable.serializeNullCollections();
        serializeNullObjects = translatable.serializeNullObjects();

        TypeMirror typeMirror = ProcessorUtils.getParseHandlerClassFromAnnotation(translatable);
        if (!ParseHandler.class.getCanonicalName()
                .equals(typeMirror.toString())) {
            parseHandlerMirror = typeMirror;
        }

        typeMirror = ProcessorUtils.getSerializeHandlerClassFromAnnotation(translatable);
        if (!SerializeHandler.class.getCanonicalName()
                .equals(typeMirror.toString())) {
            serializeHandlerMirror = typeMirror;
        }

        typeMirror = ProcessorUtils.getCustomHandlerClassFromAnnotation(translatable);
        if (!BaseTranslator.class.getCanonicalName()
                .equals(typeMirror.toString())) {
            customHandlerMirror = typeMirror;
        }

        List<? extends Element> classElements = manager.getElements()
                .getAllMembers(typeElement);
        List<? extends Element> elements = new ArrayList<>(classElements);
        KeyValidator keyValidator = new KeyValidator(manager);
        KeyListenerValidator keyListenerValidator = new KeyListenerValidator();

        // order the fields by alphabetical order to find them easier
        Collections.sort(elements, new Comparator<Element>() {
            @Override
            public int compare(Element o1, Element o2) {
                return o1.getSimpleName().toString().compareTo(o2.getSimpleName().toString());
            }
        });

        for (Element enclosedElement : elements) {
            Key key = enclosedElement.getAnnotation(Key.class);
            String elementName = enclosedElement.getSimpleName()
                    .toString();
            if (key != null || inheritedFields.containsKey(elementName)) {
                if (inheritedFields.containsKey(elementName)) {
                    key = inheritedFields.remove(elementName);
                }
                KeyDefinition keyDefinition = new KeyDefinition(manager, (VariableElement) enclosedElement, key, this);
                if (keyValidator.validate(manager, keyDefinition)) {
                    keyDefinitions.put(keyDefinition.keyName, keyDefinition);

                    if (keyDefinition.hasCustomTypeConverter) {
                        customTypeConverters.put(keyDefinition, TypeName.get(keyDefinition.customTypeConverterClassName));
                    }

                    if (keyDefinition.typedKeyDefinition.hasReferencedTranslator()) {
                        referencedTranslators.add(keyDefinition.typedKeyDefinition.getReferencedTranslatorClassName());
                    }
                }
            } else if (enclosedElement.getAnnotation(ParseKeyListener.class) != null) {
                KeyListenerDefinition keyListenerDefinition = new KeyListenerDefinition(
                        (ExecutableElement) enclosedElement, true);
                if (keyListenerValidator.validate(manager, keyListenerDefinition)) {
                    parseKeyListenerDefinitions.put(keyListenerDefinition.keyName, keyListenerDefinition);
                }
            } else if (enclosedElement.getAnnotation(SerializeKeyListener.class) != null) {
                KeyListenerDefinition keyListenerDefinition = new KeyListenerDefinition(
                        (ExecutableElement) enclosedElement, false);
                if (keyListenerValidator.validate(manager, keyListenerDefinition)) {
                    serializeKeyListenerDefinitions.put(keyListenerDefinition.keyName, keyListenerDefinition);
                }
            }
        }

        isParseListener = ProcessorUtils.implementsClass(manager, ParseListener.class.getCanonicalName(), typeElement);
        isSerializeListener = ProcessorUtils.implementsClass(manager, SerializeListener.class.getCanonicalName(), typeElement);

        manager.addTranslatableDefinition(typeElement, this);

    }

    public TypeName getParseHandlerClass() {
        if (customHandlerMirror != null) {
            return ClassName.get(customHandlerMirror);
        } else {
            return definitionClassName;
        }
    }

    @Override
    public void onWriteDefinition(TypeSpec.Builder builder) {

        if (customTypeConverters.size() > 0) {
            for (Map.Entry<KeyDefinition, TypeName> keyDefinitionStringEntry : customTypeConverters.entrySet()) {
                builder.addField(FieldSpec.builder(keyDefinitionStringEntry.getValue(),
                        keyDefinitionStringEntry.getKey().keyName + "_converter", Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("new $L()", keyDefinitionStringEntry.getValue())
                        .build());
            }
        }

        if (referencedTranslators.size() > 0) {
            for (ClassName referencedTranslator : referencedTranslators) {

                builder.addField(getReferencedTranslatorTypeName(referencedTranslator),
                        getReferencedTranslatorFieldName(referencedTranslator.simpleName()),
                        Modifier.PRIVATE);
            }
        }

        if (!referencedTranslators.isEmpty()) {
            MethodSpec.Builder initBuilder = MethodSpec.methodBuilder("init")
                    .returns(TypeName.VOID)
                    .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
                    .addAnnotation(Override.class);
            for (ClassName referencedTranslator : referencedTranslators) {
                initBuilder.addStatement("$L = ($T) $T.getTranslator($T.class)",
                        getReferencedTranslatorFieldName(referencedTranslator.simpleName()),
                        getReferencedTranslatorTypeName(referencedTranslator),
                        Acela.class, referencedTranslator);
            }
            builder.addMethod(initBuilder.build());
        }

        MethodSpec.Builder getTypeBuilder = MethodSpec.methodBuilder("getType")
                .returns(ParameterizedTypeName.get(ClassName.get(Class.class), elementClassName))
                .addAnnotation(Override.class)
                .addModifiers(METHOD_MODIFIERS)
                .addStatement("return $T.class", elementClassName);
        builder.addMethod(getTypeBuilder.build());

        if (parseHandlerMirror != null) {
            builder.addField(FieldSpec.builder(TypeName.get(parseHandlerMirror), "customParseHandler", Modifier.FINAL)
                    .initializer("new $T()", TypeName.get(parseHandlerMirror)).build());
        }

        builder.addMethod(MethodSpec.methodBuilder("getInstance")
                .addAnnotation(Override.class)
                .returns(elementClassName)
                .addModifiers(METHOD_MODIFIERS)
                .addStatement("return new $T()", elementClassName)
                .build());

        builder.addMethod(MethodSpec.methodBuilder("shouldThrowFieldExceptions")
                .addAnnotation(Override.class)
                .returns(boolean.class)
                .addModifiers(METHOD_MODIFIERS)
                .addStatement("return $L", shouldThrowExceptions)
                .build());

        writeParse(builder);
        writeSerialize(builder);
    }

    private void writeParse(TypeSpec.Builder builder) {

        MethodSpec.Builder parseBuilder = MethodSpec.methodBuilder("handleParse")
                .returns(void.class)
                .addAnnotation(Override.class)
                .addModifiers(METHOD_MODIFIERS)
                .addParameter(elementClassName, TRANSLATABLE_PARAM_NAME)
                .addParameter(JsonParserWrapper.class, "wrapper")
                .addParameter(JsonParser.class, "parser")
                .addParameter(String.class, "fieldName")
                .addException(IOException.class);

        // collect unique ones to determine size (including keylisteners)
        int size = 0;
        for (KeyDefinition keyDefinition : keyDefinitions.values()) {
            size++;
            if (parseKeyListenerDefinitions.get(keyDefinition.keyName) != null) {
                size++;
            }
        }

        // only leftover keydefinitions
        for (KeyListenerDefinition keyListenerDefinition : parseKeyListenerDefinitions.values()) {
            if (keyDefinitions.get(keyListenerDefinition.keyName) == null) {
                size++;
            }
        }

        int count = 0;
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        int foundCount = 0;
        for (KeyDefinition keyDefinition : keyDefinitions.values()) {

            // match up and see if the listener overrides generated key.
            KeyListenerDefinition keyListenerDefinition = parseKeyListenerDefinitions.get(keyDefinition.keyName);
            if (keyListenerDefinition == null || !keyListenerDefinition.overridesGenerated) {
                keyDefinition.hasKeyListenerAssociate = (keyListenerDefinition != null);
                keyDefinition.isFirst = (count == 0);
                keyDefinition.isLast = (count == size - 1);
                keyDefinition.addCode(codeBlockBuilder);

                count++;
            }

            if (keyListenerDefinition != null) {
                keyListenerDefinition.hasKeyAssociate = true;
                keyListenerDefinition.isFirst = (count == 0);
                keyListenerDefinition.isLast = (count == size - 1);

                keyListenerDefinition.addCode(codeBlockBuilder);
                parseKeyListenerDefinitions.remove(keyListenerDefinition.keyName);

                count++;
                foundCount++;
            }
        }

        // write remaining if any left.
        for (KeyListenerDefinition keyListenerDefinition : parseKeyListenerDefinitions.values()) {
            keyListenerDefinition.isFirst = (foundCount == 0);
            keyListenerDefinition.isLast = (count == size - 1);
            keyListenerDefinition.addCode(codeBlockBuilder);
            count++;
            foundCount++;
        }

        codeBlockBuilder.endControlFlow();
        parseBuilder.addCode(codeBlockBuilder.build());

        // if itself is a parse listener.
        if (isParseListener) {
            parseBuilder.addStatement("$L.onParse($L, $L)", TRANSLATABLE_PARAM_NAME,
                    "wrapper", "fieldName");
        }

        if (parseHandlerMirror != null) {
            parseBuilder.addStatement("customParseHandler.parse($L, $L)", TRANSLATABLE_PARAM_NAME, "parser");
        }

        builder.addMethod(parseBuilder.build());
    }

    private void writeSerialize(TypeSpec.Builder builder) {
        MethodSpec.Builder serializeBuilder = MethodSpec.methodBuilder("handleSerialize")
                .returns(void.class)
                .addAnnotation(Override.class)
                .addModifiers(METHOD_MODIFIERS)
                .addParameter(elementClassName, TRANSLATABLE_PARAM_NAME)
                .addParameter(JsonGeneratorWrapper.class, "wrapper")
                .addParameter(JsonGenerator.class, "generator")
                .addException(IOException.class);

        CodeBlock.Builder keyConditionalBuilder = CodeBlock.builder();
        for (KeyDefinition keyDefinition : keyDefinitions.values()) {

            // match up and see if the listener overrides generated key.
            KeyListenerDefinition keyListenerDefinition = serializeKeyListenerDefinitions.get(keyDefinition.keyName);
            if (keyListenerDefinition == null || !keyListenerDefinition.overridesGenerated) {
                keyDefinition.hasKeyListenerAssociate = (keyListenerDefinition != null);
                keyDefinition.addSerializeCode(keyConditionalBuilder);
            }

            if (keyListenerDefinition != null) {
                keyListenerDefinition.hasKeyAssociate = true;
                keyListenerDefinition.addCode(keyConditionalBuilder);
                serializeKeyListenerDefinitions.remove(keyListenerDefinition.keyName);
            }

            // if itself is a serialize listener.
            emitSerializeListener(keyDefinition.keyName, keyConditionalBuilder);
        }

        // write remaining if any left.
        for (KeyListenerDefinition keyListenerDefinition : serializeKeyListenerDefinitions.values()) {
            keyListenerDefinition.addCode(keyConditionalBuilder);

            // if itself is a serialize listener.
            emitSerializeListener(keyListenerDefinition.keyName, keyConditionalBuilder);
        }

        serializeBuilder.addCode(keyConditionalBuilder.build());

        if (serializeHandlerMirror != null) {
            serializeBuilder.addStatement("customParseHandler.serialize($L, $L)", TRANSLATABLE_PARAM_NAME, "generator");
        }

        builder.addMethod(serializeBuilder.build());
    }

    private void emitSerializeListener(String fieldName, CodeBlock.Builder builder) {
        if (isSerializeListener) {
            builder.addStatement("$L.onSerialize($L, $S)", TRANSLATABLE_PARAM_NAME,
                    "wrapper", fieldName);
        }
    }

    @Override
    protected TypeName getExtendsClass() {
        return ParameterizedTypeName.get(ClassName.get(BaseTranslator.class), elementClassName);
    }

}
