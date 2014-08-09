/*
 * Copyright (c) 2014 Maxim Yunusov
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.maxur.jj.core.reflection;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.maxur.jj.core.reflection.ClassDescriptor.meta;

public class MemberDescriptorTest {

    @Test
    public void testModifier() throws Exception {
        final MethodDescriptor<Dummy5> a2 = MethodDescriptor.meta(
                Dummy5.class.getDeclaredMethod("a2"),
                meta(Dummy5.class)
        );
        final MethodDescriptor<Dummy5> b2 = MethodDescriptor.meta(
                Dummy5.class.getDeclaredMethod("b2"),
                meta(Dummy5.class)
        );
        final MethodDescriptor<Dummy5> c2 = MethodDescriptor.meta(
                Dummy5.class.getDeclaredMethod("c2"),
                meta(Dummy5.class)
        );
        final MethodDescriptor<Dummy5> d2 = MethodDescriptor.meta(
                Dummy5.class.getDeclaredMethod("d2"),
                meta(Dummy5.class)
        );
        final FieldDescriptor<Dummy5> a1 = FieldDescriptor.meta(
                Dummy5.class.getDeclaredField("a1"), meta(Dummy5.class)
        );
        final FieldDescriptor<Dummy5> b1 = FieldDescriptor.meta(
                Dummy5.class.getDeclaredField("b1"), meta(Dummy5.class)
        );
        final FieldDescriptor<Dummy5> c1 = FieldDescriptor.meta(
                Dummy5.class.getDeclaredField("c1"), meta(Dummy5.class)
        );
        final FieldDescriptor<Dummy5> d1 = FieldDescriptor.meta(
                Dummy5.class.getDeclaredField("d1"), meta(Dummy5.class)
        );
        assertTrue(a1.isPrivate());
        assertTrue(b1.isDefault());
        assertTrue(c1.isProtected());
        assertTrue(d1.isPublic());
        assertTrue(a2.isPrivate());
        assertTrue(b2.isDefault());
        assertTrue(c2.isProtected());
        assertTrue(d2.isPublic());

        assertTrue(b1.isFinal());
        assertTrue(b2.isFinal());

    }
}

class Dummy5  {

    private int a1;
    final int b1 = 0;
    protected int c1;
    public int d1;

    private void a2() {
    }
    final void b2() {
    }
    protected void c2() {
    }
    public void d2() {
    }
}
