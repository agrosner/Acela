package com.andrewgrosner.acela.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Allows subclasses to explicitly include {@link Key} fields from non-translatable objects.
 * These fields <i>must</i> be accessible to the subclass either by package private or public.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface InheritedField {

    /**
     * @return The key definition that you would use if the field was defined as a member variable.
     */
    Key keyDefinition();

    /**
     * @return The name of the field we're referencing.
     */
    String fieldName();
}
