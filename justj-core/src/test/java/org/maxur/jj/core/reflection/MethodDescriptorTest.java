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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.maxur.jj.core.reflection.ClassDescriptor.meta;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MethodDescriptorTest {

    @Mock
    private Dummy3 mock;

    @Test
    public void testCreate() throws Exception {
        final ClassDescriptor<Dummy1> meta = meta(Dummy1.class);
        final Method method = Dummy1.class.getDeclaredMethod("a");
        assertNotNull(MethodDescriptor.meta(method, meta));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNullClassDescriptor() throws Exception {
        final Method method = Dummy1.class.getDeclaredMethod("a");
        assertNotNull(MethodDescriptor.meta(method, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNullField() throws Exception {
        final ClassDescriptor<Dummy1> meta = meta(Dummy1.class);
        assertNotNull(MethodDescriptor.meta(null, meta));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithWrongField() throws Exception {
        final ClassDescriptor<Dummy1> meta = meta(Dummy1.class);
        final Method method = Dummy2.class.getDeclaredMethod("b");
        assertNotNull(MethodDescriptor.meta(method, meta));
    }

    @Test
    public void testGetType() throws Exception {
        final ClassDescriptor<Dummy1> meta = meta(Dummy1.class);
        final Method method = Dummy1.class.getDeclaredMethod("a");
        final MethodDescriptor<Dummy1> metaMethod = MethodDescriptor.meta(method, meta);
        assertEquals(void.class,  metaMethod.getType());
    }
    @Test
    public void testOverridesFor() throws Exception {
        final MethodDescriptor<Dummy3> metaMethod1 = MethodDescriptor.meta(
                Dummy3.class.getDeclaredMethod("a"),
                meta(Dummy3.class)
        );
        final MethodDescriptor<Dummy4> metaMethod2 = MethodDescriptor.meta(
                Dummy4.class.getDeclaredMethod("a"),
                meta(Dummy4.class)
        );
        assertTrue(metaMethod2.overridesFor(metaMethod1));
    }
    @Test
    public void testOverridesForWithWrongDirection() throws Exception {
        final MethodDescriptor<Dummy3> metaMethod1 = MethodDescriptor.meta(
                Dummy3.class.getDeclaredMethod("a"),
                meta(Dummy3.class)
        );
        final MethodDescriptor<Dummy4> metaMethod2 = MethodDescriptor.meta(
                Dummy4.class.getDeclaredMethod("a"),
                meta(Dummy4.class)
        );
        assertFalse(metaMethod1.overridesFor(metaMethod2));
    }
    @Test
    public void testOverridesForWithWrongName() throws Exception {
        final MethodDescriptor<Dummy3> metaMethod1 = MethodDescriptor.meta(
                Dummy3.class.getDeclaredMethod("a"),
                meta(Dummy3.class)
        );
        final MethodDescriptor<Dummy4> metaMethod2 = MethodDescriptor.meta(
                Dummy4.class.getDeclaredMethod("b"),
                meta(Dummy4.class)
        );
        assertFalse(metaMethod2.overridesFor(metaMethod1));
    }
    @Test
    public void testOverridesForWithParams() throws Exception {
        final MethodDescriptor<Dummy3> metaMethod1 = MethodDescriptor.meta(
                Dummy3.class.getDeclaredMethod("c", Object.class),
                meta(Dummy3.class)
        );
        final MethodDescriptor<Dummy4> metaMethod2 = MethodDescriptor.meta(
                Dummy4.class.getDeclaredMethod("c", Object.class),
                meta(Dummy4.class)
        );
        assertTrue(metaMethod2.overridesFor(metaMethod1));
    }
    @Test
    public void testOverridesForWithOverloadByParamsNumber() throws Exception {
        final MethodDescriptor<Dummy3> metaMethod1 = MethodDescriptor.meta(
                Dummy3.class.getDeclaredMethod("c", Object.class),
                meta(Dummy3.class)
        );
        final MethodDescriptor<Dummy4> metaMethod2 = MethodDescriptor.meta(
                Dummy4.class.getDeclaredMethod("c", Object.class, Object.class),
                meta(Dummy4.class)
        );
        assertFalse(metaMethod2.overridesFor(metaMethod1));
    }
    @Test
    public void testOverridesForWithOverloadByParamsType() throws Exception {
        final MethodDescriptor<Dummy3> metaMethod1 = MethodDescriptor.meta(
                Dummy3.class.getDeclaredMethod("d", Object.class),
                meta(Dummy3.class)
        );
        final MethodDescriptor<Dummy4> metaMethod2 = MethodDescriptor.meta(
                Dummy4.class.getDeclaredMethod("d", String.class),
                meta(Dummy4.class)
        );
        assertFalse(metaMethod2.overridesFor(metaMethod1));
    }
    @Test
    public void testOverridesForWithDifferentGeneric() throws Exception {
        final MethodDescriptor<Dummy3> metaMethod1 = MethodDescriptor.meta(
                Dummy3.class.getDeclaredMethod("e", List.class),
                meta(Dummy3.class)
        );
        final MethodDescriptor<Dummy4> metaMethod2 = MethodDescriptor.meta(
                Dummy4.class.getDeclaredMethod("e", List.class),
                meta(Dummy4.class)
        );
        assertTrue(metaMethod2.overridesFor(metaMethod1));
    }
    @Test
    public void testInvoke() throws Exception {
        final MethodDescriptor<Dummy3> meta = MethodDescriptor.meta(
                Dummy3.class.getDeclaredMethod("d", Object.class),
                meta(Dummy3.class)
        );
        meta.invoke(mock, "a");
        verify(mock).d("a");
    }

    @Test
    public void testEqualsContract() {
        EqualsVerifier
                .forClass(MethodDescriptor.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

}

class Dummy3  {
    void a() {
    }
    void c(Object a) {
    }
    void d(Object a) {
    }
    void e(List<Object> a) {
    }
}

class Dummy4 extends Dummy3 {
    void a() {
    }
    void b() {
    }
    void c(Object a) {
    }
    void c(Object a, Object b) {
    }
    void d(String a) {
    }
    void e(List a) {
    }

}