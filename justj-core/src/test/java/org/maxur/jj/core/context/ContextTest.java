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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maxur.jj.core.domain.JustJSystemException;
import org.maxur.jj.core.domain.Role;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Inject;
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

    @Test (expected = IllegalArgumentException.class)
    public void testBindTypeToNull() throws Exception {
        root.put(Role.ANY, (Class) null);
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


    public final static class DummyContext {
    }

    @Test
    public void testEqualsContract() {
        EqualsVerifier
                .forClass(DummyContext.class)
                .withRedefinedSuperclass()
                .withPrefabValues(Context.class, new Context(), new Context())
                .verify();
    }


    // InjectByConstructor

    @Test
    public void testBindTypeToTypeWithInjectByConstructor() throws Exception {
        root.put(Role.ANY, Dummy2.class);
        final Dummy bean2 = new Dummy();
        root.put(Dummy.class, bean2);
        final Dummy2 bean = root.bean(Role.ANY);
        assertEquals(Dummy2.class, bean.getClass());
        assertEquals(bean2, bean.dummy);
    }

    @Test(expected = JustJSystemException.class)
    public void testBindTypeToTypeWithInjectByConstructorWithSecondConstructor() throws Exception {
        root.put(Dummy.class, new Dummy());
        root.put(String.class, "");
        root.put(Role.ANY, Dummy3.class);
    }

    @Test(expected = JustJSystemException.class)
    public void testBindTypeToTypeWithInjectByConstructorWithUnavailableOne() throws Exception {
        root.put(Role.ANY, Dummy4.class);
        root.bean(Role.ANY);
    }

    @Test
    public void testBindTypeToTypeWithInjectByConstructorWithTwoParams() throws Exception {
        root.put(Role.ANY, Dummy5.class);
        root.put(String.class, "");
        final Dummy bean2 = new Dummy();
        root.put(Dummy.class, bean2);
        final Dummy5 bean = root.bean(Role.ANY);
        assertEquals(Dummy5.class, bean.getClass());
        assertEquals("", bean.value);
        assertEquals(bean2, bean.dummy);
    }

    @Test(expected = JustJSystemException.class)
    public void testBindTypeToTypeWithInjectByConstructorWithAbsentOneParam() throws Exception {
        root.put(Role.ANY, Dummy5.class);
        final Dummy bean2 = new Dummy();
        root.put(Dummy.class, bean2);
        root.bean(Role.ANY);
    }

    @Test(expected = JustJSystemException.class)
    public void testBindTypeToTypeWithInjectByConstructorWithAbsentAllParams() throws Exception {
        root.put(Role.ANY, Dummy5.class);
        root.bean(Role.ANY);
    }

    @Test(expected = JustJSystemException.class)
    public void testBindTypeToTypeWithInjectByConstructorWithAbstractClass() throws Exception {
        root.put(Role.ANY, Dummy6.class);
        final Dummy bean2 = new Dummy();
        root.put(Dummy.class, bean2);
        root.bean(Role.ANY);
    }

    @Test
    @Ignore
    public void testBindTypeToTypeWithInjectByConstructorWithCircularDependencies() throws Exception {
        root.put(Dummy7.class, Dummy7.class);
        root.put(Dummy8.class, Dummy8.class);
        root.bean(Dummy7.class);
        root.bean(Dummy8.class);
    }

    // InjectByField

    @Test
    public void testInjectByFieldBean() throws Exception {
        final Dummy bean2 = new Dummy();
        root.put(Dummy.class, bean2);
        final Dummy10 bean = new Dummy10();
        root.inject(bean);
        assertEquals(bean2, bean.a);
    }

    @Test
    public void testBindTypeToTypeWithInjectByField() throws Exception {
        root.put(Role.ANY, Dummy10.class);
        final Dummy bean2 = new Dummy();
        root.put(Dummy.class, bean2);
        final Dummy10 bean = root.bean(Role.ANY);
        assertEquals(Dummy10.class, bean.getClass());
        assertEquals(bean2, bean.a);
    }

    @Test (expected = JustJSystemException.class)
    public void testInjectByFieldWithoutInjectedBean() throws Exception {
        root.inject(new Dummy10());
    }

    @Test
    @Ignore
    public void testInjectByFieldWithCircularDependencies() {
        root.put(Dummy11.class, Dummy11.class);
        root.put(Dummy12.class, Dummy12.class);
        final Dummy11 dummy11 = root.bean(Dummy11.class);
        final Dummy12 dummy12 = root.bean(Dummy12.class);
        assertEquals(dummy11, dummy12.dummy);
    }


}


class Dummy {
}



class Dummy2 {
    final Dummy dummy;
    @Inject
    public Dummy2(Dummy dummy) {
        this.dummy = dummy;
    }
}

class Dummy3 {
    final Dummy dummy;
    @Inject
    public Dummy3(Dummy dummy) {
        this.dummy = dummy;
    }
    @Inject
    public Dummy3(Dummy dummy, String value) {
        this.dummy = dummy;
    }
}

class Dummy4 {
    private Dummy4() {
    }
}

class Dummy5 {
    final Dummy dummy;
    final String value;
    @Inject
    public Dummy5(Dummy dummy, String value) {
        this.dummy = dummy;
        this.value = value;
    }
}

abstract class Dummy6 {
    final Dummy dummy;
    @Inject
    private Dummy6(Dummy dummy) {
        this.dummy = dummy;
    }
}

class Dummy7 {
    final Dummy8 dummy;
    @Inject
    private Dummy7(Dummy8 dummy) {
        this.dummy = dummy;
    }
}
class Dummy8 {
    final Dummy7 dummy;
    @Inject
    private Dummy8(Dummy7 dummy) {
        this.dummy = dummy;
    }
}


class Dummy10 {
    @Inject
    Dummy a;
    Dummy b;
    public Dummy10() {
    }
}


class Dummy11 {
    @Inject
    Dummy12 dummy;
    public Dummy11() {
    }
}

class Dummy12 {
    @Inject
    Dummy11 dummy;
    public Dummy12() {
    }
}