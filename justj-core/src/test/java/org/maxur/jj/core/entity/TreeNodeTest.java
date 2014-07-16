/*
 * Copyright (c) 2014 Maxim Yunusov
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.maxur.jj.core.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TreeNodeTest {

    @Spy
    private Visitor<TreeNode> dummyVisitor = new Visitor<TreeNode>() {
        @Override
        public void visit(TreeNode subject) {
            if (subject instanceof TreeNode1) {
                dontGoDeeper();
            }
            if (subject instanceof TreeNode2) {
                stop();
            }

        }
    };

    @Test
    public void testEmptyIterator() throws Exception {
        final TreeNode<TreeNode> root = new TreeNode<>();
        assertFalse(root.iterator().hasNext());
    }

    @Test
    public void testAdd() throws Exception {
        final TreeNode<TreeNode> root = new TreeNode<>();
        final TreeNode child = new TreeNode();
        root.add(child);
        final Iterator<TreeNode> iterator = root.iterator();
        assertTrue(iterator.hasNext());
        while (iterator.hasNext()) {
            final TreeNode next = iterator.next();
            assertEquals(child, next);
            assertNotEquals(root, next);
            assertFalse(iterator.hasNext());
        }
    }


    @Test(expected = UnsupportedOperationException.class)
    public void testImmutableIterator() throws Exception {
        final TreeNode<TreeNode> root = new TreeNode<>();
        final TreeNode child = new TreeNode();
        root.add(child);
        final Iterator<TreeNode> iterator = root.iterator();
        while (iterator.hasNext()) {
            iterator.remove();
        }
    }

    @Test
    public void testVisitToAll() throws Exception {
        final TreeNode<TreeNode> root = new TreeNode<>();
        final TreeNode<TreeNode> childA = new TreeNode<>();
        root.add(childA);
        final TreeNode<TreeNode> childB = new TreeNode<>();
        root.add(childB);
        final TreeNode<TreeNode> child1 = new TreeNode<>();
        childB.add(child1);
        final TreeNode<TreeNode> child2 = new TreeNode<>();
        childB.add(child2);

        root.accept(dummyVisitor);

        verify(dummyVisitor, times(1)).visit(root);
        verify(dummyVisitor, times(1)).visit(childA);
        verify(dummyVisitor, times(1)).visit(childB);
        verify(dummyVisitor, times(1)).visit(child1);
        verify(dummyVisitor, times(1)).visit(child2);

    }

    @Test
    public void testVisitWithoutDeep() throws Exception {
        final TreeNode<TreeNode> root = new TreeNode<>();
        final TreeNode<TreeNode> childA = new TreeNode<>();
        root.add(childA);
        final TreeNode<TreeNode> childB = new TreeNode1();
        root.add(childB);
        final TreeNode<TreeNode> child1 = new TreeNode<>();
        childB.add(child1);
        final TreeNode<TreeNode> child2 = new TreeNode<>();
        childB.add(child2);

        root.accept(dummyVisitor);

        verify(dummyVisitor, times(1)).visit(root);
        verify(dummyVisitor, times(1)).visit(childA);
        verify(dummyVisitor, times(1)).visit(childB);
        verify(dummyVisitor, times(0)).visit(child1);
        verify(dummyVisitor, times(0)).visit(child2);

    }

    @Test
    public void testVisitWithStop() throws Exception {
        final TreeNode<TreeNode> root = new TreeNode<>();
        final TreeNode<TreeNode> childA = new TreeNode2();
        root.add(childA);
        final TreeNode<TreeNode> childB = new TreeNode<>();
        root.add(childB);
        final TreeNode<TreeNode> child1 = new TreeNode<>();
        childB.add(child1);
        final TreeNode<TreeNode> child2 = new TreeNode<>();
        childB.add(child2);

        root.accept(dummyVisitor);

        verify(dummyVisitor, times(1)).visit(root);
        verify(dummyVisitor, times(1)).visit(childA);
        verify(dummyVisitor, times(0)).visit(childB);
        verify(dummyVisitor, times(0)).visit(child1);
        verify(dummyVisitor, times(0)).visit(child2);

    }


    private static class TreeNode1 extends TreeNode<TreeNode> {
    }
    private static class TreeNode2 extends TreeNode<TreeNode> {
    }
}