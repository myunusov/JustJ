package org.maxur.justj.core.lang;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>27.02.2016</pre>
 */
public class ArrayCursor<T> extends BaseCursor<T> {

    private final T[] items;

    private ArrayCursor(final T[] items) {
        this.items = items;
    }

    public static <T> Cursor<T> cursor(final T[] items) {
        return new ArrayCursor<>(items);
    }
    
    @Override
    protected int size() {
        return items.length;
    }

    @Override
    protected T itemBy() {
        return items[position()];
    }

}
