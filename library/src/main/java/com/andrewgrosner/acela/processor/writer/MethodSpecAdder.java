package com.andrewgrosner.acela.processor.writer;

import com.squareup.javapoet.MethodSpec;

/**
 * Description:
 */
public interface MethodSpecAdder {

    public void addStatement(MethodSpec.Builder methodSpecBuilder);
}
