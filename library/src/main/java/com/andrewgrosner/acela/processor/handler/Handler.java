package com.andrewgrosner.acela.processor.handler;

import com.andrewgrosner.acela.processor.AcelaProcessorManager;

import javax.annotation.processing.RoundEnvironment;

public interface Handler {

    void handle(AcelaProcessorManager manager, RoundEnvironment roundEnvironment);
}
