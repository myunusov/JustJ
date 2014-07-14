package org.maxur.jj.core.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Maxim Yunusov
 * @version 1.0 13.07.2014
 */
public class TreeNode<T extends TreeNode> implements Iterable<T> {

    private final List<T> children = new ArrayList<>();

    public void add(T child) {
        children.add(child);
    }

    public int size() {
        return children.size();
    }

    public Iterator<T> iterator() {
        return Collections.unmodifiableList(children).iterator();
    }
}
