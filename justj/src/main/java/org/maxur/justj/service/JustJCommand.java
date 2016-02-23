package org.maxur.justj.service;

import org.maxur.justj.core.Command;
import org.maxur.justj.core.Logger;
import org.maxur.justj.core.cli.CliCommand;
import org.maxur.justj.core.cli.Level;
import org.maxur.justj.core.cli.Option;
import org.maxur.justj.core.cli.Slf4jAndLogbackLoggerAdapter;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class JustJCommand extends CliCommand {

    private Logger logger;

    @Option("q")
    private boolean quiet;

    @Option("x")
    private boolean debug;

    @Override
    public Command<Void, Integer> init() {
        logger = Slf4jAndLogbackLoggerAdapter.loggerFor("CONSOLE");
        if (quiet) {
            logger.setLevel(Level.ERROR);
        } else if (debug) {
            logger.setLevel(Level.DEBUG);
        } else {
            logger.setLevel(Level.INFO);
        }
        return this;
    }

    public Logger logger() {
        return logger;
    }



}
