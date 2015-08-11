package com.andrewgrosner.acela.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Marks an accessible method as listening for a specific key event during parse.
 * The method name must resemble:
 * void onParse{keyName}(ParserWrapper wrapper)
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ParseKeyListener {

    /**
     * @return The key to trigger this method call. It is not guaranteed to be the only call if a field has
     * a {@link Key} annotation tied to it unless {@link #listenerOverridesGenerated()} is true.
     */
    String key();

    /**
     * @return If true, we ignore any fields that contain the {@link Key} annotation corresponding to this method.
     * This is great for overridding inherited {@link Key} functionality.
     */
    boolean listenerOverridesGenerated() default false;
}
