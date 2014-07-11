package org.maxur.jj.sample.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.maxur.jj.core.config.Configurator.config;

/**
 * @author Maxim Yunusov
 * @version 1.0 05.07.2014
 */
public final class Launcher {

    private final static Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        LOGGER.info("Start Application");
        config();
        LOGGER.info("Stop Application");
    }
}