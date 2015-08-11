package com.andrewgrosner.acela.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Marks a class to register as a {@link com.andrewgrosner.acela.TypeConverter} automatically.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface TypeConverter {

}
