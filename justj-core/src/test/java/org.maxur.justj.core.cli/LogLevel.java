package org.maxur.justj.core.cli;

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
