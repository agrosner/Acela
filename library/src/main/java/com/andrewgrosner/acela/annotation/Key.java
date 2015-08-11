package com.andrewgrosner.acela.annotation;

import com.andrewgrosner.acela.TypeConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Marks a field as referring to a specific JSON field.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Key {

    /**
     * @return The name of the JSON key to associate this field with. This overrides any {@link Translatable#keyNameRule()}
     */
    String name() default "";

    /**
     * The default value of the object. This is a literal string that will be placed into the class so
     * it can be anything. If using an object, make sure to use its fully qualified class name.
     * For any non primitive type it will set to null, while primitives will be corresponding "0" for int,
     * "false" for boolean, etc.
     *
     * @return The default value in string format to be pasted in the parse definition.
     */
    String defValue() default "";

    /**
     * @return If true (default), we check for null before serializing. If false, we will serialize
     * it without checking for a slight performance gain, which may cause a possible {@link NullPointerException}.
     * This is useful for fields that we know will never be null.
     */
    boolean checkForNullDuringSerialize() default true;

    /**
     * @return A custom {@link TypeConverter} used for this specific field.
     */
    Class<? extends TypeConverter> typeConverter() default TypeConverter.class;
}

