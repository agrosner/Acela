package com.andrewgrosner.acela.processor.definition;

import com.andrewgrosner.acela.TypeConverter;
import com.andrewgrosner.acela.processor.AcelaProcessorManager;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * Description: Defines a class that uses the {@link com.andrewgrosner.acela.annotation.TypeConverter}
 * annotation. It must implement {@link TypeConverter} to be used.
 */
public class TypeConverterDefinition extends BaseDefinition {

    public Element modelType;

    public TypeConverterDefinition(TypeElement typeElement, AcelaProcessorManager manager) {
        super(typeElement, manager);
        Types types = manager.getTypes();
        DeclaredType typeConverterSuper = null;
        DeclaredType typeConverter = types.getDeclaredType(
                manager.getElements().getTypeElement(TypeConverter.class.getCanonicalName()),
                types.getWildcardType(null, null), types.getWildcardType(null, null));

        for (TypeMirror superType : types.directSupertypes(typeElement.asType())) {
            if (types.isAssignable(superType, typeConverter)) {
                typeConverterSuper = (DeclaredType) superType;
            }
        }

        if (typeConverterSuper != null) {
            List<? extends TypeMirror> typeArgs = typeConverterSuper.getTypeArguments();
            modelType = manager.getElements().getTypeElement(typeArgs.get(0).toString());
        }

    }

    @Override
    public void onWriteDefinition(TypeSpec.Builder builder) {

    }
}
