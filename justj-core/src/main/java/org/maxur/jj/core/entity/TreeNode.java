package org.maxur.jj.core.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Defines the requirements for an object that can be used as a tree node in a Tree Structure.
 *
 * @author Maxim Yunusov
 * @version 1.0 13.07.2014
 */
public class TreeNode<T extends TreeNode> implements Iterable<T>, Visitable {

    private final List<T> children = new ArrayList<>();

    /**
     * Add new Child element.
     *
     * @param child The child element.
     */
    public void add(T child) {
        children.add(child);
    }

    /**
     * Return iterator of children nodes.
     *
     * @return iterator of children nodes
     */
    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(children).iterator();
    }

    @Override
    public void visitToChildren(final Visitor<Visitable> visitor) {
        for (T child : children) {
            child.accept(visitor);
        }
    }
}
