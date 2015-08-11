package com.andrewgrosner.acela.processor;

import com.andrewgrosner.acela.Acela;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.andrewgrosner.acela.processor.definition.ClassNames;
import com.andrewgrosner.acela.processor.definition.TranslatableDefinition;
import com.andrewgrosner.acela.processor.definition.TypeConverterDefinition;
import com.andrewgrosner.acela.processor.handler.Handler;
import com.andrewgrosner.acela.processor.writer.TypeDefinition;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Description:
 */
public class AcelaProcessorManager implements Handler, TypeDefinition {

    private List<Handler> handlers = Lists.newArrayList();
    private Map<String, TranslatableDefinition> translatableDefinitionHashMap = Maps.newHashMap();
    private Map<String, TypeConverterDefinition> typeConverterDefinitionMap = Maps.newHashMap();

    private final Elements elements;
    private final Types types;
    private final Filer filer;
    private final Messager messager;

    public AcelaProcessorManager(ProcessingEnvironment processingEnvironment) {
        elements = processingEnvironment.getElementUtils();
        types = processingEnvironment.getTypeUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
    }

    public void addAnnotationHandler(Handler handler) {
        handlers.add(handler);
    }

    @Override
    public void handle(AcelaProcessorManager manager, RoundEnvironment roundEnvironment) {
        for (Handler handler : handlers) {
            handler.handle(manager, roundEnvironment);
        }

        if (roundEnvironment.processingOver()) {
            try {
                PackageElement packageName = manager.getElements().getPackageElement(ClassNames.BASE_PACKAGE);
                JavaFile javaFile = JavaFile.builder(packageName.getQualifiedName().toString(), getTypeSpec())
                        .build();
                javaFile.writeTo(manager.getFiler());
            } catch (IOException e) {
            }
        }
    }

    public void addTranslatableDefinition(Element element, TranslatableDefinition translatableDefinition) {
        translatableDefinitionHashMap.put(element.getSimpleName().toString(), translatableDefinition);
    }

    public void addTypeConverterDefinition(Element element, TypeConverterDefinition typeConverterDefinition) {
        typeConverterDefinitionMap.put(element.getSimpleName().toString(), typeConverterDefinition);
    }

    public Elements getElements() {
        return elements;
    }

    public Types getTypes() {
        return types;
    }

    public Filer getFiler() {
        return filer;
    }

    public void logError(String error, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(error, args));
    }

    @Override
    public TypeSpec getTypeSpec() {
        TypeSpec.Builder managerSpecBuilder = TypeSpec.classBuilder("AcelaManager")
                .addModifiers(Modifier.FINAL);

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        for (TranslatableDefinition translatableDefinition : translatableDefinitionHashMap.values()) {
            constructorBuilder.addStatement("$T.addTranslator($T.class, new $T())",
                    ClassName.get(Acela.class),
                    translatableDefinition.getElementClassName(),
                    translatableDefinition.getParseHandlerClass());
        }

        for (TypeConverterDefinition typeConverterDefinition : typeConverterDefinitionMap.values()) {
            constructorBuilder.addStatement("$T.registerTypeConverterForClass($T.class, new $T())",
                    ClassName.get(Acela.class),
                    typeConverterDefinition.modelType.asType(),
                    typeConverterDefinition.getElementClassName());
        }
        return managerSpecBuilder.addMethod(constructorBuilder.build())
                .build();
    }
}
