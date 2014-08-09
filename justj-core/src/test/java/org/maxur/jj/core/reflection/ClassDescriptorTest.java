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

package org.maxur.jj.core.reflection;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.maxur.jj.core.reflection.ClassDescriptor.meta;

public class ClassDescriptorTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNull() throws Exception {
        meta(null);
    }

    @Test
    public void testObjectParents() throws Exception {
        assertEquals(Collections.<ClassDescriptor>singletonList(meta(Object.class)), meta(Object.class).parents());
    }

    @Test
    public void testParents() throws Exception {
        assertTrue(meta(Dummy2.class).parents().contains(meta(Dummy1.class)));
    }

    @Test
    public void testHierarchyLevelFor() throws Exception {
        assertEquals(1, meta(Dummy2.class).getHierarchyLevelFor(Dummy1.class));
        assertEquals(2, meta(Dummy2.class).getHierarchyLevelFor(Dummy2.class));
        final Class stringClass = String.class;
        assertEquals(-1, meta(Dummy2.class).getHierarchyLevelFor(stringClass));
    }

    @Test
    public void testMethods() throws Exception {
        assertEquals(meta(Object.class).methods().size() + 2 , meta(Dummy1.class).methods().size());
    }

    @Test
    public void testFields() throws Exception {
        final ClassDescriptor<Dummy1> meta = meta(Dummy1.class);
        assertTrue(meta.fields().contains(FieldDescriptor.meta(Dummy1.class.getDeclaredField("i"), meta)));
    }

    @Test
    public void testMethodsWithOverload() throws Exception {
        assertEquals(meta(Dummy1.class).methods().size() + 2, meta(Dummy2.class).methods().size());
    }

    @Test
    public void testIsAssignable() throws Exception {
        assertTrue(meta(Dummy2.class).isAssignable(Dummy1.class));
        assertFalse(meta(Dummy1.class).isAssignable(Dummy2.class));

    }

    @Test
    @Ignore
    public void testEqualsContract() {
        EqualsVerifier
                .forClass(ClassDescriptor.class)
                .suppress(Warning.NULL_FIELDS)
                .withRedefinedSuperclass()
                .verify();
    }

}

