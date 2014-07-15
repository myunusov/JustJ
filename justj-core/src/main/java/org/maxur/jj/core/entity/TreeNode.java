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
public class TreeNode<T extends TreeNode> implements Iterable<T> {

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
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(children).iterator();
    }

    public void visit(final Visitor<TreeNode> visitor) {
        if (!Visitor.State.STOP_TRAVERSAL.equals(visitor.state())) {
            visitor.accept(this);
        }
        if (Visitor.State.CONTINUE_TRAVERSAL.equals(visitor.state())) {
            processChildren(visitor);
        }
    }

    protected void processChildren(final Visitor<TreeNode> visitor) {
        for (TreeNode child : children) {
            child.visit(visitor);
        }
    }

}
