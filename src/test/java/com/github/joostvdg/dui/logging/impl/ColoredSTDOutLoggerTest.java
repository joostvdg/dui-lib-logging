package com.github.joostvdg.dui.logging.impl;


import com.github.joostvdg.dui.logging.LogLevel;
import com.github.joostvdg.dui.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

public class ColoredSTDOutLoggerTest {

    @Test
    public void isALogger() {
        Logger logger = new ColoredSTDOutLogger();
        logger.start(LogLevel.INFO);
        logger.stop();
        Assert.assertTrue(true); // we made it here, so it must be a logger
    }
}
