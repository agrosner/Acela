package com.andrewgrosner.acela.processor;

import com.andrewgrosner.acela.annotation.Key;
import com.andrewgrosner.acela.annotation.Translatable;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

/**
 * Author: andrewgrosner
 * Contributors: { }
 * Description:
 */
public class ProcessorUtils {
    public static boolean implementsClass(AcelaProcessorManager manager, String fqTn, TypeElement element) {
        TypeElement typeElement = manager.getElements().getTypeElement(fqTn);
        if (typeElement == null) {
            manager.logError("Type Element was null for: " + fqTn + "" +
                    "ensure that the visibility of the class is not private.");
            return false;
        } else {
            TypeMirror classMirror = typeElement.asType();
            return manager.getTypes().isAssignable(element.asType(), classMirror);
        }
    }

    public static TypeMirror getParseHandlerClassFromAnnotation(Translatable annotation) {
        TypeMirror typeMirror = null;
        if (annotation != null) {
            try {
                annotation.parseHandler();
            } catch (MirroredTypeException mte) {
                typeMirror = mte.getTypeMirror();
            }
        }
        return typeMirror;
    }

    public static TypeMirror getSerializeHandlerClassFromAnnotation(Translatable annotation) {
        TypeMirror typeMirror = null;
        if (annotation != null) {
            try {
                annotation.serializeHandler();
            } catch (MirroredTypeException mte) {
                typeMirror = mte.getTypeMirror();
            }
        }
        return typeMirror;
    }

    public static TypeMirror getCustomHandlerClassFromAnnotation(Translatable annotation) {
        TypeMirror typeMirror = null;
        if (annotation != null) {
            try {
                annotation.customTranslator();
            } catch (MirroredTypeException mte) {
                typeMirror = mte.getTypeMirror();
            }
        }
        return typeMirror;
    }

    public static TypeMirror getCustomTypeConverter(Key annotation) {
        TypeMirror typeMirror = null;
        if (annotation != null) {
            try {
                annotation.typeConverter();
            } catch (MirroredTypeException mte) {
                typeMirror = mte.getTypeMirror();
            }
        }
        return typeMirror;
    }

    public static String getKeyName(String keyName, Translatable.KeyNameRule keyNameRule) {
        String newName = keyName;
        switch (keyNameRule) {
            case UNDERSCORE_AND_LOWERCASE:
                String[] words = keyName.split("(?=\\p{Upper})");
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < words.length; i++) {
                    if (i > 0) {
                        builder.append("_");
                    }
                    builder.append(words[i].toLowerCase());
                }
                newName = builder.toString();
                break;
            case LOWERCASE:
                newName = keyName.toLowerCase();
                break;
        }
        return newName;
    }
}
