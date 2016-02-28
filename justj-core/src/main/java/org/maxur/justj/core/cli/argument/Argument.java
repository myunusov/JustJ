package org.maxur.justj.core.cli.argument;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.02.2016</pre>
 */
public class Argument {

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

    public String asString() {
        return key != null ? "" + key : name;
    }

    public boolean isKey() {
        return key != null;
    }

    public Character key() {
        return key;
    }

    public String name() {
        return name;
    }

    public String optionArgument() {
        return cursor.nextOptionArgument();
    }

}
