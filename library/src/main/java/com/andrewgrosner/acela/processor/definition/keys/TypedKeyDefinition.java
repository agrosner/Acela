package com.andrewgrosner.acela.processor.definition.keys;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

public interface TypedKeyDefinition {

    void addParseCode(CodeBlock.Builder codeBlockBuilder);

    void addSerializeCode(CodeBlock.Builder codeBlockBuilder);

    boolean hasReferencedTranslator();

    ClassName getReferencedTranslatorClassName();
}
