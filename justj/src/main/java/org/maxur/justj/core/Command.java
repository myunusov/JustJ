package org.maxur.justj.core;

import java.util.Optional;

/**
 * The interface Command.
 *
 * @param <T> the Type of command execution result.
 * @param <E> the Type of command execution error.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public interface Command<T, E> {

    /**
     * Init.
     */
    Command<T, E> init();

    /**
     * Execute.
     */
    Command<T, E> execute();

    /**
     * Error.
     *
     * @return the optional
     */
    Optional<E> error();

    /**
     * Result.
     *
     * @return the optional
     */
    Optional<T> result();

}
