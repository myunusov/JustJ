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

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.maxur.jj.core.reflection.ClassDescriptor.meta;

public class FieldDescriptorTest {

    @Test
    public void testCreate() throws Exception {
        final ClassDescriptor<Dummy1> meta = meta(Dummy1.class);
        final Field field = Dummy1.class.getDeclaredField("i");
        assertNotNull(FieldDescriptor.meta(field, meta));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNullClassDescriptor() throws Exception {
        final Field field = Dummy1.class.getDeclaredField("i");
        assertNotNull(FieldDescriptor.meta(field, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNullField() throws Exception {
        final ClassDescriptor<Dummy1> meta = meta(Dummy1.class);
        assertNotNull(FieldDescriptor.meta(null, meta));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithWrongField() throws Exception {
        final ClassDescriptor<Dummy1> meta = meta(Dummy1.class);
        final Field field = Dummy2.class.getDeclaredField("j");
        assertNotNull(FieldDescriptor.meta(field, meta));
    }

    @Test
    public void testSetValue() throws Exception {
        final ClassDescriptor<Dummy1> meta = meta(Dummy1.class);
        final Field field = Dummy1.class.getDeclaredField("i");
        final FieldDescriptor<Dummy1> metaField = FieldDescriptor.meta(field, meta);
        final Dummy1 dummy1 = new Dummy1();
        metaField.setValue(dummy1, 1);
        assertEquals(1, dummy1.i);
    }

    @Test
    public void testGetType() throws Exception {
        final ClassDescriptor<Dummy1> meta = meta(Dummy1.class);
        final Field field = Dummy1.class.getDeclaredField("i");
        final FieldDescriptor<Dummy1> metaField = FieldDescriptor.meta(field, meta);
        assertEquals(int.class,  metaField.getType());
    }

    @Test
    public void testEqualsContract() {
        EqualsVerifier
                .forClass(FieldDescriptor.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

}


