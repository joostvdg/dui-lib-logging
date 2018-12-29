package com.github.joostvdg.dui.logging.impl;

import com.github.joostvdg.dui.logging.LogLevel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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


    /** FORMAT:
     * { "time": "", "message" : "", "severity" : "" }
     * Where the time format is 'time.RFC3339Nano': "2006-01-02T15:04:05.999999999Z07:00"
     * https://stackoverflow.com/questions/6038136/how-do-i-parse-rfc-3339-datetimes-with-java/41569330
     */
    public String toJSON() {
        String jsonFormat = " { \"time\": \"%s\", \"message\" : \"%s\", \"severity\" : \"%s\" }";
        return String.format(jsonFormat, dateTime, message, severity);
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
            // LocalDateTime does not have "InstantSeconds", which we need to use the ISO_INSTANT
            // Which is the closest thing we get to an RFC3339.
            ZonedDateTime zonedDateTime = ZonedDateTime.of(timeToFormat, ZoneId.systemDefault());
            this.dateTime = zonedDateTime.format(DateTimeFormatter.ISO_INSTANT);
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
