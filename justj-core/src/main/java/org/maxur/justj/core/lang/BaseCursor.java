package org.maxur.justj.core.lang;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>27.02.2016</pre>
 */
public abstract class BaseCursor<T> implements Cursor<T> {

    private int position;

    protected BaseCursor() {
        this.position = -1;
    }

    @Override
    public void next() {
        if (!hasNext()) {
            throw new IllegalStateException("Next item is not exist");
        }
        position++;
    }

    @Override
    public boolean hasNext() {
        return position + 1 < size();
    }

    @Override
    public T current() {
        if (position == -1) {
            throw new IllegalStateException("You need call next before current");
        }
        return itemBy();
    }

    protected abstract int size();

    protected abstract T itemBy();

    public int position() {
        return position;
    }
}
