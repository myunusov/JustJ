package org.maxur.justj.core.cli;

import org.maxur.justj.core.cli.annotation.Flag;
import org.maxur.justj.core.cli.annotation.Key;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.02.2016</pre>
 */
public enum LogLevel {
    @Key("x")
    DEBUG,
    @Key("q")
    @Flag("quiet")
    OFF,
    INFO
}
