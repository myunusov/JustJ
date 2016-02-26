package org.maxur.justj.core.cli;

import java.util.Iterator;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.02.2016</pre>
 */
class Argument {

    private final Character key;

    private final String name;

    private final Iterator<String> iterator;

    Argument(final Character key, final Iterator<String> iterator) {
        this.key = key;
        this.iterator = iterator;
        this.name = null;
    }

    Argument(final String name, final Iterator<String> iterator) {
        this.iterator = iterator;
        this.key = null;
        this.name = name;
    }

    String asString() {
        return key != null ? "" + key : name;
    }

    boolean isKey() {
        return key != null;
    }

    Character key() {
        return key;
    }

    String name() {
        return name;
    }

    String next() {
        return iterator.hasNext() ? iterator.next() : null;
    }
}
