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

package org.maxur.jj.core.context;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maxur.jj.core.domain.Inject;
import org.maxur.jj.core.domain.JustJSystemException;
import org.maxur.jj.core.domain.Role;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.Supplier;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.maxur.jj.core.domain.Role.role;

@RunWith(MockitoJUnitRunner.class)
public class ContextTest {

    @Spy
    private Context root;

    @Spy
    private Context child;

    @Before
    public void setUp() throws Exception {
        root = new Context();
        child = new Context(root);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBindRoleToNullBean() throws Exception {
        root.put(Role.ANY, (Object) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBindRoleToNullSupplier() throws Exception {
        root.put(Role.ANY, (Supplier) null);
    }

    @Test
    public void testBindRoleToBean() throws Exception {
        final Object bean = new Object();
        root.put(Role.ANY, bean);
        assertEquals(bean, root.bean(Role.ANY));
    }

    @Test
    public void testBindTypeToType() throws Exception {
        root.put(Object.class, Object.class);
        assertEquals(Object.class, root.bean(Object.class).getClass());
    }

    @Test
    public void testBindRoleToType() throws Exception {
        root.put(Role.ANY, Object.class);
        assertEquals(Object.class, root.bean(Role.ANY).getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBindTypeToWrongType() throws Exception {
        root.put(String.class, Object.class);
    }

    @Test
    public void testBindTypeToTypeWithInject() throws Exception {
        root.put(Role.ANY, Dummy1.class);
        final Dummy2 bean2 = new Dummy2();
        root.put(Dummy2.class, bean2);
        final Dummy1 bean = root.bean(Role.ANY);
        assertEquals(Dummy1.class, bean.getClass());
        assertEquals(bean2, bean.dummy2);
    }

    @Test (expected = JustJSystemException.class)
    public void testBindTypeToTypeWithInjectWithoutInjectedBean() throws Exception {
        root.put(Role.ANY, Dummy1.class);
        root.bean(Role.ANY);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testBindTypeToNull() throws Exception {
        root.put(Role.ANY, (Class) null);
    }

    @Test
    public void testInjectBean() throws Exception {
        final Dummy2 bean2 = new Dummy2();
        root.put(Dummy2.class, bean2);
        final Dummy1 bean = new Dummy1();
        root.inject(bean);
        assertEquals(bean2, bean.dummy2);
    }

    @Test
    public void testBindTypeToTypeWithInjectByConstructor() throws Exception {
        root.put(Role.ANY, Dummy3.class);
        final Dummy2 bean2 = new Dummy2();
        root.put(Dummy2.class, bean2);
        final Dummy3 bean = root.bean(Role.ANY);
        assertEquals(Dummy3.class, bean.getClass());
        assertEquals(bean2, bean.dummy2);
    }

    @Test(expected = JustJSystemException.class)
    public void testBindTypeToTypeWithInjectByConstructorWithLotOfOnes() throws Exception {
        root.put(Role.ANY, Dummy4.class);
    }

    @Test(expected = JustJSystemException.class)
    public void testBindTypeToTypeWithInjectByConstructorWithUnavailableOne() throws Exception {
        root.put(Role.ANY, Dummy5.class);
        root.bean(Role.ANY);
    }

    @Test// (expected = JustJSystemException.class)
    public void testBindTypeToTypeWithInjectByConstructorWithLotOfParamsAndUnavailableOne() throws Exception {
        root.put(Role.ANY, Dummy6.class);
        final Dummy2 bean2 = new Dummy2();
        root.put(Dummy2.class, bean2);
        root.bean(Role.ANY);
    }


    @Test
    public void testBindTypeToBean() throws Exception {
        root.put(String.class, "");
        assertEquals("", root.bean(String.class));
    }

    @Test
    public void testBindRoleToSupplier() throws Exception {
        final Object bean = new Object();
        root.put(Role.ANY, () -> bean);
        assertEquals(bean, root.bean(Role.ANY));
    }

    @Test
    public void testBindTypeToSupplier() throws Exception {
        root.put(String.class, () -> "");
        assertEquals("", root.bean(String.class));
    }

    @Test
    public void testAccessToParentsBean() throws Exception {
        final Object bean = new Object();
        root.put(Role.ANY, bean);
        assertEquals(bean, child.bean(Role.ANY));
    }

    @Test(expected = JustJSystemException.class)
    public void testPutDuplicateBeanByRole() throws Exception {
        root.put(Role.ANY, new Object());
        root.put(Role.ANY, new Object());
    }

    @Test(expected = JustJSystemException.class)
    public void testPutDuplicateBeanByType() throws Exception {
        root.put(String.class, "");
        root.put(String.class, "");
    }

    @Test
    public void testAccessToAbsentBean() throws Exception {
        assertNull(root.bean(Role.ANY));
    }

    @Test
    public void testAccessToInvalidSupplier() throws Exception {
        root.put(Role.ANY, () -> null);
        assertNull(root.bean(Role.ANY));
    }

    @Test(expected = JustJSystemException.class)
    public void testPutDuplicateBeanByTypeWithParent() throws Exception {
        root.put(String.class, "");
        child.put(String.class, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutUnsuitableBean() throws Exception {
        root.put(role("Integer", Integer.class), "");
    }

    @Test
    public void testRootWithoutParent() throws Exception {
        assertEquals(root.root(), root);
    }

    @Test
    public void testRootWithParent() throws Exception {
        assertEquals(child.root(), root);
    }

    @Test
    public void testGetParent() throws Exception {
        assertEquals(root, child.parent());
    }

    @Test
    public void testEqualsContract() {
        EqualsVerifier
                .forClass(DummyContext.class)
                .withRedefinedSuperclass()
                .withPrefabValues(Context.class, new Context(), new Context())
                .verify();
    }

    public final static class DummyContext {
    }

}

class Dummy1 {
    @Inject
    Dummy2 dummy2;

    public Dummy1() {
    }
}

class Dummy2 {
}

class Dummy3 {

    final Dummy2 dummy2;

    @Inject
    public Dummy3(Dummy2 dummy2) {
        this.dummy2 = dummy2;
    }
}

class Dummy4 {

    final Dummy2 dummy2;

    @Inject
    public Dummy4(Dummy2 dummy2) {
        this.dummy2 = dummy2;
    }
    @Inject
    public Dummy4(Dummy2 dummy2, String value) {
        this.dummy2 = dummy2;
    }

}

class Dummy5 {
    private Dummy5() {
    }
}

class Dummy6 {
    final Dummy2 dummy2;
    @Inject
    private Dummy6(Dummy2 dummy2) {
        this.dummy2 = dummy2;
    }
}