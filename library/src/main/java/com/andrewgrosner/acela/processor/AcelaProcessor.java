package com.andrewgrosner.acela.processor;

import com.google.auto.service.AutoService;
import com.andrewgrosner.acela.annotation.Translatable;
import com.andrewgrosner.acela.annotation.TypeConverter;
import com.andrewgrosner.acela.processor.handler.TranslatableHandler;
import com.andrewgrosner.acela.processor.handler.TypeConverterHandler;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

/**
 * Description: The main entry point into the annotation processor.
 */
@AutoService(Processor.class)
public class AcelaProcessor extends AbstractProcessor {

    private AcelaProcessorManager manager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        manager = new AcelaProcessorManager(processingEnv);
        manager.addAnnotationHandler(new TypeConverterHandler());
        manager.addAnnotationHandler(new TranslatableHandler());
    }

    /**
     * @return The top-level annotations that this processor listens for.
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> classes = new LinkedHashSet<>();
        classes.add(Translatable.class.getCanonicalName());
        classes.add(TypeConverter.class.getCanonicalName());
        return classes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        manager.handle(manager, roundEnv);
        return true;
    }
}
