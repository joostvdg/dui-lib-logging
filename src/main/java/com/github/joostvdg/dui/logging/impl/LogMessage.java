package com.github.joostvdg.dui.logging.impl;

import com.github.joostvdg.dui.logging.LogLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogMessage {
    private final String dateTime;
    private final String message;
    private final String severity;

    private LogMessage(LogMessageBuilder builder) {
        this.dateTime = builder.dateTime;
        this.message = builder.message;
        this.severity = builder.severity;
    }

    public static final class LogMessageBuilder {
        private String dateTime;
        private String message;
        private String severity;

        public LogMessageBuilder dateTime(final LocalDateTime dateTime) {
            LocalDateTime timeToFormat = dateTime;
            if (dateTime == null) {
                timeToFormat = LocalDateTime.now();
            }
            this.dateTime = timeToFormat.format(DateTimeFormatter.ISO_INSTANT);
            return this;
        }

        public LogMessageBuilder message(final String message) {
            this.message = message;
            return this;
        }

        public LogMessageBuilder severity(final LogLevel severity) {
            this.severity = severity.toString();
            return this;
        }

        public LogMessage build() {
            return new LogMessage(this);
        }
    }
}
