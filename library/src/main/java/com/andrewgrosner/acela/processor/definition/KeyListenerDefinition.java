package com.andrewgrosner.acela.processor.definition;

import com.andrewgrosner.acela.annotation.ParseKeyListener;
import com.andrewgrosner.acela.annotation.SerializeKeyListener;
import com.andrewgrosner.acela.processor.writer.CodeBlockAdder;
import com.squareup.javapoet.CodeBlock;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

/**
 * Description: Holds information about a {@link ParseKeyListener}
 */
public class KeyListenerDefinition implements CodeBlockAdder {

    public String methodName;
    public String keyName;

    public boolean overridesGenerated;

    public boolean hasKeyAssociate = false;
    public boolean isParse;
    public boolean isFirst;
    public boolean isLast;

    public List<? extends VariableElement> parameterList;

    public KeyListenerDefinition(ExecutableElement executableElement, boolean isParse) {
        this.isParse = isParse;
        methodName = executableElement.getSimpleName().toString();
        parameterList = executableElement.getParameters();

        if (isParse) {
            ParseKeyListener keyListener = executableElement.getAnnotation(ParseKeyListener.class);
            keyName = keyListener.key();
            overridesGenerated = keyListener.listenerOverridesGenerated();
        } else {
            SerializeKeyListener keyListener = executableElement.getAnnotation(SerializeKeyListener.class);
            keyName = keyListener.key();
            overridesGenerated = keyListener.listenerOverridesGenerated();
        }
    }

    @Override
    public void addCode(CodeBlock.Builder codeBlockBuilder) {
        if (!hasKeyAssociate && isParse) {
            String condition = String.format("if (\"%1s\".equals(%1s))", keyName, "fieldName");

            if (isFirst) {
                codeBlockBuilder.beginControlFlow(condition);
            } else {
                codeBlockBuilder.nextControlFlow("else " + condition);
            }
        }
        codeBlockBuilder.addStatement("$L.$L($L)", TranslatableDefinition.TRANSLATABLE_PARAM_NAME,
                methodName, parameterList.size() == 1 ? "wrapper" : "");
    }

}
