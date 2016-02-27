package org.maxur.justj.core.lang;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>27.02.2016</pre>
 */
public interface Cursor<T> {

    void next();

    boolean hasNext();

    T current();
}
