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
import org.maxur.jj.core.annotation.Optional;
import org.maxur.jj.core.domain.JustJSystemException;
import org.maxur.jj.core.domain.Role;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Inject;
import java.util.function.Supplier;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        root.put(Role.ANY, (Supplier<Object>) null);
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
        assertEquals(root, child.parent().get());
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

/*
    Injectable fields:

    are annotated with @Inject.
    are not final.
    may have any otherwise valid name
*/

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

    @Test
    public void testInjectByFieldWithOptionalField() {
        root.put(Dummy13.class, Dummy13.class);
        final Dummy dummy = new Dummy();
        root.put(Dummy.class, dummy);
        final Dummy13 bean = root.bean(Dummy13.class);
        assertEquals(dummy, bean.a);
        assertNull(bean.b);
    }

    @Test
    public void testInjectByFieldWithDuplicateFields() {
        root.put(Dummy14.class, Dummy14.class);
        final Dummy dummy = new Dummy();
        root.put(Dummy.class, dummy);
        final Dummy14 bean = root.bean(Dummy14.class);
        assertEquals(dummy, bean.a);
        assertEquals(dummy, bean.b);
    }

    @Test
    public void testInjectByFieldWithPrivateFinalModifier() {
        root.put(Dummy15.class, Dummy15.class);
        final Dummy dummy = new Dummy();
        root.put(Dummy.class, dummy);
        final Dummy15 dummy15 = root.bean(Dummy15.class);
        assertEquals(dummy, dummy15.getA());
    }

    @Test
    public void testInjectByFieldWithStaticModifier() {
        final Dummy dummy = new Dummy();
        root.put(Dummy.class, dummy);
        root.inject(new Dummy31());
        assertEquals(dummy, Dummy31.a);
    }

    @Test
    public void testInjectByFieldWithSuperTypeMethod() {
        final Dummy dummy = new Dummy();
        root.put(Dummy.class, dummy);
        root.put(Dummy33.class, Dummy33.class);
        final Dummy33 bean = root.bean(Dummy33.class);
        assertEquals(dummy, bean.a);
        assertEquals(dummy, bean.c);
    }


    // InjectByMethod

    /*
    Injectable methods:

    are annotated with @Inject.
    are not abstract.
    do not declare type parameters of their own.
    may return a result
    may have any otherwise valid name.
    accept zero or more dependencies as arguments.
    */

    @Test
    public void testInjectByMethod() {
        root.put(Dummy20.class, Dummy20.class);
        final Dummy20 bean = root.bean(Dummy20.class);
        assertNotNull(bean.a);
    }

    @Test
    public void testInjectByMethodWithParams() {
        root.put(Dummy21.class, Dummy21.class);
        final Dummy dummy = new Dummy();
        root.put(Dummy.class, dummy);
        final Dummy21 bean = root.bean(Dummy21.class);
        assertEquals(dummy, bean.a);
    }

    @Test
    public void testInjectByMethodWithTwoSameParams() {
        root.put(Dummy22.class, Dummy22.class);
        final Dummy dummy = new Dummy();
        root.put(Dummy.class, dummy);
        final Dummy22 bean = root.bean(Dummy22.class);
        assertEquals(dummy, bean.a);
        assertEquals(dummy, bean.b);
    }

    @Test
    public void testInjectByMethodWithTwoParams() {
        root.put(Dummy23.class, Dummy23.class);
        final Dummy dummy = new Dummy();
        root.put(Dummy.class, dummy);
        root.put(Dummy2.class, Dummy2.class);
        final Dummy23 bean = root.bean(Dummy23.class);
        assertEquals(dummy, bean.a);
        assertNotNull(bean.b);
    }

    @Test(expected = JustJSystemException.class)
    public void testInjectByMethodWithAbsentParams() {
        root.put(Dummy21.class, Dummy21.class);
        root.bean(Dummy21.class);
    }

    // A method with no @Inject annotation that overrides a method annotated with @Inject will not be injected.
    @Test()
    public void testInjectByMethodWithOverrideWithoutInject() {
        root.put(Dummy25.class, Dummy25.class);
        final Dummy dummy = new Dummy();
        root.put(Dummy.class, dummy);
        final Dummy25 bean = root.bean(Dummy25.class);
        assertNull(bean.a);
    }

    //A method annotated with @Inject that overrides another method annotated with @Inject will
    // only be injected once per injection request per instance
    @Test()
    public void testInjectByMethodWithOverrideWithInject() {
        root.put(Dummy25.class, Dummy25.class);
        final Dummy dummy = new Dummy();
        root.put(Dummy.class, dummy);
        final Dummy25 bean = root.bean(Dummy25.class);
        assertEquals(1, bean.count);
        assertEquals(dummy, bean.b);

    }

    @Test()
    @Ignore
    public void testGetBySuperType() {
        final Dummy25 dummy25 = new Dummy25();
        root.put(Dummy25.class, dummy25);
        final Dummy dummy = new Dummy();
        root.put(Dummy.class, dummy);
        final Dummy24 bean = root.bean(Dummy24.class);
        assertEquals(bean, dummy25);
    }

    @Test
    @Ignore
    public void testInjectByMethodWithCircularDependencies() {
        final Dummy27 dummy27 = new Dummy27();
        final Dummy26 dummy26 = new Dummy26();
        root.put(Dummy27.class, dummy27);
        root.put(Dummy26.class, dummy26);
        final Dummy26 bean = root.bean(Dummy26.class);
        assertEquals(dummy27, bean.a);
    }

    @Test(expected = JustJSystemException.class)
    public void testInjectByMethodWithThrowsException() {
        root.put(Dummy28.class, Dummy28.class);
        root.bean(Dummy28.class);
    }

    @Test
    public void testInjectByMethodWithPrivateModifier() {
        root.put(Dummy29.class, Dummy29.class);
        final Dummy dummy = new Dummy();
        root.put(Dummy.class, dummy);
        final Dummy29 bean = root.bean(Dummy29.class);
        assertEquals(dummy, bean.a);
    }

    @Test
    public void testInjectByMethodWithStaticModifier() {
        final Dummy dummy = new Dummy();
        root.put(Dummy.class, dummy);
        root.inject(new Dummy30());
        assertEquals(dummy, Dummy30.a);
    }

    @Test
    public void testInjectByMethodWithSuperTypeMethod() {
        final Dummy dummy = new Dummy();
        root.put(Dummy.class, dummy);
        root.put(Dummy33.class, Dummy33.class);
        final Dummy33 bean = root.bean(Dummy33.class);
        assertEquals(dummy, bean.b);
        assertEquals(dummy, bean.d);
        assertNull(bean.e);
        assertEquals(dummy, bean.f);
        assertEquals(dummy, bean.g);
        assertEquals(1, bean.count);
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

class Dummy13 {
    @Inject
    Dummy a;
    @Inject
    @Optional
    Dummy2 b;
    public Dummy13() {
    }
}

class Dummy14 {
    @Inject
    Dummy a;
    @Inject
    Dummy b;
    public Dummy14() {
    }
}

class Dummy15 {
    @Inject
    private final Dummy a;

    public Dummy15() {
        this.a = null;
    }

    public Dummy getA() {
        return a;
    }
}

class Dummy20 {

    Dummy a;

    public Dummy20() {
    }

    @Inject
    public void makeA() {
        this.a = new Dummy();
    }

}

class Dummy21 {

    Dummy a;

    public Dummy21() {
    }

    @Inject
    public void setA(Dummy a) {
        this.a = a;
    }

}

class Dummy22 {

    Dummy a;
    Dummy b;

    public Dummy22() {
    }

    @Inject
    public void init(Dummy a, Dummy b) {
        this.a = a;
        this.b = b;
    }

}

class Dummy23 {

    Dummy a;
    Dummy2 b;

    public Dummy23() {
    }

    @Inject
    public void init(Dummy a, Dummy2 b) {
        this.a = a;
        this.b = b;
    }

}


class Dummy24 {

    Dummy a;
    Dummy b;
    int   count;

    public Dummy24() {
    }

    @Inject
    public void setA(Dummy a) {
        this.a = a;
    }

    @Inject
    public void setB(Dummy b) {
        this.b = null;
        count++;
    }

}

class Dummy25 extends Dummy24 {

    Dummy a;
    Dummy b;

    public Dummy25() {
        count = 0;
    }

    @Override
    public void setA(Dummy a) {
        this.a = a;
    }

    @Inject
    @Override
    public void setB(Dummy b) {
        this.b = b;
        count++;
    }

}


class Dummy26  {

    Dummy27 a;

    public Dummy26() {
    }


    @Inject
    public void setA(Dummy27 a) {
        this.a = a;
    }


}

class Dummy27  {

    Dummy26 a;

    public Dummy27() {
    }

    @Inject
    public void setA(Dummy26 a) {
        this.a = a;
    }
}

class Dummy28  {
    public Dummy28() {
    }
    @Inject
    public void setA() {
        throw new RuntimeException();
    }
}

class Dummy29  {
    Dummy a;
    public Dummy29() {
    }
    @Inject
    private void setA(Dummy a) {
        this.a = a;
    }
}

class Dummy30  {

    static Dummy a;

    public Dummy30() {
    }
    @Inject
    public static void setA(Dummy a) {
        Dummy30.a = a;
    }
}

class Dummy31  {
    @Inject
    static Dummy a;

    public Dummy31() {
    }
}

class Dummy32  {
    @Inject
    Dummy a;
    Dummy b;
    Dummy e;
    Dummy f;
    Dummy g;

    int count = 0;

    public Dummy32() {
    }
    @Inject
    public void setB(Dummy b) {
        this.b = b;
    }
    @Inject
    public void setE(Dummy e) {
        this.e = e;
    }
    @Inject
    public void setF(Dummy f) {
        this.f = f;
        count++;
    }

    public void setG(Dummy g) {
        this.g = g;
    }

}

class Dummy33 extends Dummy32  {
    @Inject
    Dummy c;
    Dummy d;
    public Dummy33() {
    }
    @Inject
    public void setD(Dummy d) {
        this.d = d;
    }
    public void setE(Dummy e) {
        this.e = e;
    }
    @Inject
    public void setF(Dummy f) {
        this.f = f;
        count++;
    }
    @Inject
    public void setG(Dummy g) {
        this.g = g;
    }

}