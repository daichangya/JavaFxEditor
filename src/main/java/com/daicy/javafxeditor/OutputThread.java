package com.daicy.javafxeditor;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;

public class OutputThread extends Thread {

    private final Duration refreshRate;
    private final AtomicReference<StringBuilder> outputBuffer;
    private final java.util.function.Consumer<String> outputAction;

    public OutputThread(Duration refreshRate, AtomicReference<StringBuilder> outputBuffer, java.util.function.Consumer<String> outputAction) {
        this.refreshRate = refreshRate;
        this.outputBuffer = outputBuffer;
        this.outputAction = outputAction;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            tryToOutput();
            try {
                Thread.sleep(refreshRate.toMillis());
            } catch (InterruptedException e) {
                tryToOutput();
                return;
            }
        }
    }

    private void tryToOutput() {
        String textToOutput = outputBuffer.get().toString();
        if (!textToOutput.isEmpty()) {
            Platform.runLater(() -> outputAction.accept(textToOutput));
        }
    }
}
