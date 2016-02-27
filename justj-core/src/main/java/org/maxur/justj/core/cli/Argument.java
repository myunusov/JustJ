package org.maxur.justj.core.cli;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.02.2016</pre>
 */
class Argument {

    private final Character key;

    private final String name;

    private final ArgumentCursor cursor;

    Argument(final Character key, final ArgumentCursor cursor) {
        this.key = key;
        this.cursor = cursor;
        this.name = null;
    }

    Argument(final String name, final ArgumentCursor cursor) {
        this.cursor = cursor;
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

    String optionArgument() {
        return cursor.nextOptionArgument();
    }

}
