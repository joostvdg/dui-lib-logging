package com.github.joostvdg.dui.logging.impl;

import com.github.joostvdg.dui.logging.Default;
import com.github.joostvdg.dui.logging.LogLevel;
import com.github.joostvdg.dui.logging.Logger;

import java.io.Serializable;
import java.time.LocalDateTime;

@Default
public class ColoredSTDOutLogger implements Logger, Serializable {

    private static final long serialVersionUID = 1L;

    private transient final SimpleLoggerThread loggerThread;
    private transient LogLevel level;
    private transient boolean shutDown = false;

    public ColoredSTDOutLogger() {
        this.loggerThread = new SimpleLoggerThread(this);
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

        String LogLevelColor = Constants.ANSI_WHITE;
        switch(level) {
            case DEBUG:
                LogLevelColor = Constants.ANSI_WHITE;
                break;
            case INFO:
                LogLevelColor = Constants.ANSI_CYAN;
                break;
            case WARN:
                LogLevelColor = Constants.ANSI_YELLOW;
                break;
            case ERROR:
                LogLevelColor = Constants.ANSI_RED;
                break;
        }

        StringBuffer logBuffer = new StringBuffer("");
        if (level.equals(LogLevel.ERROR)) {
            logBuffer.append("[ERROR]");
        }
        logBuffer.append("[");
        logBuffer.append(Constants.ANSI_PURPLE);
        logBuffer.append(mainComponent);
        logBuffer.append(Constants.ANSI_RESET);
        if (mainComponent.length() < 8) {
            logBuffer.append("]\t\t\t\t\t\t[");
        } else if (mainComponent.length() < 18) {
            logBuffer.append("]\t\t\t\t[");
        } else if (mainComponent.length() < 23) {
            logBuffer.append("]\t\t\t[");
        } else if (mainComponent.length() < 30) {
            logBuffer.append("]\t\t[");
        } else {
            logBuffer.append("]\t[");
        }
        logBuffer.append(LogLevelColor);
        logBuffer.append(level);
        logBuffer.append(Constants.ANSI_RESET);
        logBuffer.append("]\t[");
        logBuffer.append(Constants.ANSI_GREEN);
        logBuffer.append(LocalDateTime.now().toLocalTime());
        logBuffer.append(Constants.ANSI_RESET);
        logBuffer.append("]\t[");
        logBuffer.append(Constants.ANSI_GREEN);
        logBuffer.append(threadId);
        logBuffer.append(Constants.ANSI_RESET);
        if (threadId < 10) {
            logBuffer.append("]\t");
        } else {
            logBuffer.append("]\t");
        }
        logBuffer.append(LogLevelColor);
        if (subComponent != null ) {
            logBuffer.append("[");
            logBuffer.append(subComponent);
            if (subComponent.length() < 6) {
                logBuffer.append("]\t\t\t\t");
            } else if (subComponent.length() < 10) {
                logBuffer.append("]\t\t\t");
            } else if (subComponent.length() < 20){
                logBuffer.append("]\t\t");
            } else {
                logBuffer.append("]\t");
            }
        } else {
            logBuffer.append("]\t");
        }
        for (String part : messageParts) {
            logBuffer.append(part);
        }
        logBuffer.append(Constants.ANSI_RESET);
        try {
            loggerThread.log(logBuffer.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(LogLevel level, String component, long threadId, String... messageParts) {
        log(level, component, null, threadId, messageParts);
    }

}
