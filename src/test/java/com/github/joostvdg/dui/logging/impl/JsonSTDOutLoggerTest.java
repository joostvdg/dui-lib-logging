package com.github.joostvdg.dui.logging.impl;

import com.github.joostvdg.dui.logging.LogLevel;
import com.github.joostvdg.dui.logging.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonSTDOutLoggerTest {

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void isALogger() {
        Logger logger = new JsonSTDOutLogger();
        try {
            logger.start(LogLevel.INFO);
        } finally {
            logger.stop();
        }
        Assert.assertTrue(true); // we made it here, so it must be a logger
    }

    @Test
    public void logsInJson() {
        var loggerThread = new SimpleLoggerThread(this);
        loggerThread.disableOutput();
        Logger logger = new JsonSTDOutLogger(loggerThread);
        try {
            logger.start(LogLevel.INFO);
            // LogLevel level, String component, long threadId, String...messageParts
            var level = LogLevel.INFO;
            var component = "test";
            var threadId = 10L;
            var messageParts = "something";
            logger.log(level, component, threadId, messageParts);
            var message = loggerThread.copyAMessage();
            Assert.assertNotEquals("N.A", message);

            // Regex to match
            // \{ "time": "\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\w+", "message" : "\[\w+\]\[\w+\]\[\w+\]", "severity" : "\w+" \}
            // http://www.vogella.com/tutorials/JavaRegularExpressions/article.html
            // https://www.regexplanet.com/advanced/java/index.html
            String regex = "\\{ \"time\": \"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\w+\", \"message\" : \"\\[\\w+\\]\\[\\w+\\]\\[\\w+\\]\", \"severity\" : \"\\w+\" \\}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(message);

            System.out.println(message);
            Assert.assertTrue(matcher.find());
        } finally {
            logger.stop();
        }
    }
}
