package org.maxur.jj.sample.scanner;

import org.maxur.jj.core.system.JJSystem;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim Yunusov
 * @version 1.0 12.07.2014
 */
public class MazeApplication extends JJSystem {

    private static final Logger LOGGER = getLogger(MazeApplication.class);

    @Override
    protected void beforeStop() {
        LOGGER.info("Stop Application");
    }

    @Override
    protected void afterStart() {
        LOGGER.info("Start Application");
    }
}
