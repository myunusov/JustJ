package org.maxur.justj.core.lang;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>27.02.2016</pre>
 */
public class CharacterCursor extends BaseCursor<Character> {

    private final String items;

    private CharacterCursor(final String items) {
        super();
        this.items = items;
    }

    public static CharacterCursor cursor(final String items) {
        return new CharacterCursor(items);
    }

    @Override
    protected int size() {
        return items.length();
    }

    @Override
    protected Character itemBy() {
        return items.charAt(position());
    }
}
