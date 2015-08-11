package com.andrewgrosner.acela.processor.writer;

import com.squareup.javapoet.CodeBlock;

/**
 * Description:
 */
public interface CodeBlockAdder {

    void addCode(CodeBlock.Builder codeBlockBuilder);
}
