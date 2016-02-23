package org.maxur.justj.core;

import org.maxur.justj.core.cli.Level;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public interface Logger {

    void error(Exception cause);

    void error(String message);

    void info(String message);

    void setLevel(Level level);
}
