package com.github.joostvdg.dui.logging.impl;

import com.github.joostvdg.dui.logging.KubernetesCompatible;
import com.github.joostvdg.dui.logging.LogLevel;
import com.github.joostvdg.dui.logging.Logger;

import java.io.Serializable;

/**
 * Implements Logger, with a JSON format on STD Out.
 * The JSON format should be compatible with FluentD/FluentBit and Kubernetes logging.
 */
@KubernetesCompatible
public class JsonSTDOutLogger implements Logger, Serializable {

    /* FORMAT:
     * { "time": "", "message" : "", "severity" : "" }
     * Where the time format is 'time.RFC3339Nano': "2006-01-02T15:04:05.999999999Z07:00"
     * https://stackoverflow.com/questions/6038136/how-do-i-parse-rfc-3339-datetimes-with-java/41569330
     */

    private static final long serialVersionUID = 1L;

    private transient final LoggerThread loggerThread;
    private transient LogLevel level;
    private transient boolean shutDown = false;
    private transient int queued;

    public JsonSTDOutLogger(){
        this.loggerThread = new LoggerThread(this);
    }

    @Override
    public void start(LogLevel level) {
        this.level = level;
        this.loggerThread.start();
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

    }

    @Override
    public void log(LogLevel level, String component, long threadId, String... messageParts) {

    }
}
