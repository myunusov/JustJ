package org.maxur.jj.core.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

    public Stream<? extends TreeNode> stream() {
        return Stream.concat(Stream.<TreeNode>of(this), children.stream().flatMap(TreeNode::stream));
    }

    public Stream<? extends TreeNode> streamBy(final Predicate<TreeNode> predicate) {
        if (predicate.test(this)) {
            return Stream.concat(Stream.<TreeNode>of(this), children.stream().flatMap(n -> n.streamBy(predicate)));
        } else {
            return Stream.empty();
        }
    }



}
