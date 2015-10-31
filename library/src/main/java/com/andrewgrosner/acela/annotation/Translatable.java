package com.andrewgrosner.acela.annotation;

import com.andrewgrosner.acela.handler.BaseTranslator;
import com.andrewgrosner.acela.handler.ParseHandler;
import com.andrewgrosner.acela.handler.SerializeHandler;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: andrewgrosner
 * Description: Enables parsing code generation.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Translatable {

    /**
     * This enum describes how the conversion from java variable name to the JSON {@link Key} occurs by default.
     * As always, specifying the {@link Key#name()} overrides these rules.
     */
    enum KeyNameRule {

        /**
         * Enabled by default, this option matches the name of the variable specified unless the {@link Key#name()}
         * is specified.
         */
        NORMAL,

        /**
         * Field names are converted into lowercase and underscores for parsing JSON keys. It takes
         * the first capital letter of each word in the variable name.
         * <p></p>
         * Ex: fieldNameIsImportant becomes "field_name_is_important"
         */
        UNDERSCORE_AND_LOWERCASE,

        /**
         * Field names are lower-cased.
         * <p></p>
         * Ex: fieldName becomes "fieldname" or "Fieldname" becomes "fieldname"
         */
        LOWERCASE

    }

    /**
     * @return Define a custom {@link ParseHandler} to provide a custom parse implementation to complement this parse.
     */
    Class<? extends ParseHandler> parseHandler() default ParseHandler.class;

    /**
     * @return define a custom {@link SerializeHandler} to provide a custom serialize implementation to complement the serialize.
     */
    Class<? extends SerializeHandler> serializeHandler() default SerializeHandler.class;

    /**
     * @return If you wish to use a different _Translator than the generated one, then specify
     * the class you want here. It must have the same type parameter as the class you specify this in.
     */
    Class<? extends BaseTranslator> customTranslator() default BaseTranslator.class;

    /**
     * @return If true, any field that the {@link JsonParser} does not find throws an {@link IOException}
     */
    boolean shouldThrowFieldExceptions() default false;

    /**
     * @return If false, we do not generate null checks for faster serializing. Default is true to prevent
     * {@link NullPointerException}. Also note that this may overwrite the {@link #serializeNullObjects()}
     * for fields that qualify.
     */
    boolean checkForNullDuringSerialize() default true;

    /**
     * @return False by default, if true we serialize "null" objects unless {@link #checkForNullDuringSerialize()}
     * is false.
     */
    boolean serializeNullObjects() default false;

    /**
     * @return False by default, if true we serialize collections that are null and any null elements contained.
     */
    boolean serializeNullCollections() default false;

    /**
     * @return An array of optional fields that we define as also being keys to this class. Its useful when you're
     * subclassing a POJO from another module or library.
     */
    InheritedField[] inheritedKeys() default {};

    /**
     * @return The rule by which java field names are converted into keys. Specifying the {@link Key#name()}
     * overrides this rule.
     */
    KeyNameRule keyNameRule() default KeyNameRule.NORMAL;
}
