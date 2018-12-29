package com.github.joostvdg.dui.logging.impl;

import com.github.joostvdg.dui.logging.KubernetesCompatible;
import com.github.joostvdg.dui.logging.LogLevel;
import com.github.joostvdg.dui.logging.Logger;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;


/**
 * Implements Logger, with a JSON format on STD Out.
 * The JSON format should be compatible with FluentD/FluentBit and Kubernetes logging.
 */
@KubernetesCompatible
public class JsonSTDOutLogger implements Logger, Serializable {

    private static final long serialVersionUID = 1L;
    private transient final SimpleLoggerThread loggerThread;
    private transient LogLevel level;
    private transient boolean shutDown = false;

    public JsonSTDOutLogger(SimpleLoggerThread loggerThread){
        this.loggerThread = loggerThread;
    }

    public JsonSTDOutLogger(){
        this.loggerThread = new SimpleLoggerThread(this);
    }

    @Override
    public void start(LogLevel level) {
        this.level = level;
        loggerThread.start();
    }

    @Override
    public void stop() {
        synchronized (this) {
            shutDown = true;
        }
        loggerThread.interrupt();
    }

    @Override
    public void log(LogLevel level, String mainComponent, String subComponent, long threadId, String... messageParts) {
        var component = mainComponent + " - " + subComponent;
        var message = formatMessage(component, threadId, messageParts);
        log(LocalDateTime.now(), message, level);
    }

    @Override
    public void log(LogLevel level, String component, long threadId, String... messageParts) {
        var message = formatMessage(component, threadId, messageParts);
        log(LocalDateTime.now(), message, level);
    }

    private String formatMessage(String component, long threadId, String[] messageParts) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("[");
        messageBuilder.append(component);
        messageBuilder.append("][");
        messageBuilder.append(threadId);
        messageBuilder.append("][");
        Arrays.stream(messageParts).forEach(messageBuilder::append);
        messageBuilder.append("]");
        return messageBuilder.toString();
    }

    private void log(LocalDateTime now, String message, LogLevel level) {
        synchronized (this) {
            if (shutDown) {
                throw new IllegalStateException("We are shutdown, stop trying to log");
            }
            loggerThread.queue();
        }
        if (level.getLevel() < this.level.getLevel()) {
            // we should not log this
            return;
        }
        LogMessage logMessage = new LogMessage.LogMessageBuilder().dateTime(now).message(message).severity(level).build();
        try {
            loggerThread.log(logMessage.toJSON());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
