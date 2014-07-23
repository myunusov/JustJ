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
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maxur.jj.core.domain.Inject;
import org.maxur.jj.core.domain.JustJSystemException;
import org.maxur.jj.core.domain.Role;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.maxur.jj.core.domain.Role.role;

@RunWith(MockitoJUnitRunner.class)
public class ContextTest {

    @Spy
    private DummyContext root;

    @Spy
    private DummyContext child;

    @Before
    public void setUp() throws Exception {
        root = new DummyContext();
        child = new DummyContext(root);
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

    @Test(expected = JustJSystemException.class)
    public void testAccessToAbsentBean() throws Exception {
        root.bean(Role.ANY);
    }

    @Test(expected = JustJSystemException.class)
    public void testAccessToInvalidSupplier() throws Exception {
        root.put(Role.ANY, () -> null );
        root.bean(Role.ANY);
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
    @Ignore
    public void testEqualsContract() {
        EqualsVerifier
                .forClass(Context.class)
                .suppress(Warning.NULL_FIELDS)
                .withRedefinedSuperclass()
                .withPrefabValues(Context.class, root, child)
                .withPrefabValues(ContextImpl.class, new DummyContextImpl(), new DummyContextImpl())
                .verify();
    }

    private static class DummyContextImpl implements ContextImpl {
        @Override
        public BeanWrapper wrapper(BeanIdentifier id) {
            return null;
        }
        @Override
        public void put(Supplier<BeanWrapper> supplier, BeanIdentifier id) {
        }
    }

    private static class DummyContext extends Context<DummyContext> {
        private DummyContext() {
        }
        private DummyContext(DummyContext root) {
            super(root);
        }
        @Override
        public void stop() {
        }
    }


}

class  Dummy1 {
    @Inject
    Dummy2 dummy2;

    public Dummy1() {
    }
}

class Dummy2 {
}