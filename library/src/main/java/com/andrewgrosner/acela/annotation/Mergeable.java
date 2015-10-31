package com.andrewgrosner.acela.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Enables at a class-level support for merging together different sources of information, where
 * any fields left out of the parsing default to the existing data. On a field-level, this replaces the {@link Key#defValue()}
 * with the current field's value.
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Mergeable {
}
