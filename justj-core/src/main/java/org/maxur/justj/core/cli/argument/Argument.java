package org.maxur.justj.core.cli.argument;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>26.02.2016</pre>
 */
public class Argument {

    private final Character key;

    private final String name;

    private String optionArgument;

    public Argument(final Character key) {
        this.key = key;
        this.name = null;
    }

    public Argument(final String name) {
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
        return optionArgument;
    }

    public void setOptionArgument(final String optionArgument) {
        this.optionArgument = optionArgument;
    }
}
