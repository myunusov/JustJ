package org.maxur.justj.core.cli;

import org.maxur.justj.core.lang.ArrayCursor;
import org.maxur.justj.core.lang.CharacterCursor;
import org.maxur.justj.core.lang.Cursor;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>27.02.2016</pre>
 */
public class ArgumentCursor {

    private static final String NAME_PREFIX = "--";

    private static final String KEY_PREFIX = "-";

    private static final CharacterCursor NULL_CURSOR = CharacterCursor.cursor("");

    private final Cursor<String> argCursor;

    private CharacterCursor keyCursor = NULL_CURSOR;

    private ArgumentCursor(final Cursor<String> cursor) {
        this.argCursor = cursor;
    }

    public static ArgumentCursor cursor(final String[] items) {
        return new ArgumentCursor(ArrayCursor.cursor(items));
    }

    private boolean isOptionName(final String arg) {
        return arg.startsWith(NAME_PREFIX);
    }

    private boolean isOptionKey(final String arg) {
        return arg.startsWith(KEY_PREFIX) && !isOptionName(arg);
    }

    private String extractOptionName(final String arg) {
        return arg.substring(NAME_PREFIX.length());
    }

    private String extractOptionKeys(final String arg) {
        return arg.substring(KEY_PREFIX.length());
    }

    public boolean hasNext() {
        return argCursor.hasNext() || (keyCursor.hasNext());
    }

    public Argument nextOption() {
        if (keyCursor.hasNext()) {
            return nextKey();
        } else {
            return nextArgument();
        }
    }

    private Argument nextArgument() {
        argCursor.next();
        final String arg = argCursor.current();
        if (isOptionName(arg)) {
            final String name = extractOptionName(arg);
            keyCursor = NULL_CURSOR;
            return new Argument(name, this);
        } else if (isOptionKey(arg)) {
            keyCursor = CharacterCursor.cursor(extractOptionKeys(arg));
            if (keyCursor.hasNext()) {
                return nextKey();
            }
        }
        throw new IllegalStateException(format("argument %s is invalid", arg));
    }

    private Argument nextKey() {
        keyCursor.next();
        return new Argument(keyCursor.current(), this);
    }

    public String nextOptionArgument() {
        if (keyCursor.hasNext()) {
            keyCursor.next();
            final String result = argCursor.current().substring(keyCursor.position() + 1);
            keyCursor = NULL_CURSOR;
            return result;
        } else {
            if (argCursor.hasNext()) {
                argCursor.next();
                return argCursor.current();
            } else {
                return null;
            }
        }
    }
}
