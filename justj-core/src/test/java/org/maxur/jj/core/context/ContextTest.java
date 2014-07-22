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
import org.junit.Ignore;
import org.junit.Test;
import org.maxur.jj.core.domain.JustJSystemException;
import org.maxur.jj.core.domain.Role;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.maxur.jj.core.domain.Role.role;

public class ContextTest {

    @Test(expected = IllegalArgumentException.class)
    public void testBindRoleToNullBean() throws Exception {
        final Context context = new Context();
        context.put(Role.ANY, (Object) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBindRoleToNullSupplier() throws Exception {
        final Context context = new Context();
        context.put(Role.ANY, (Supplier) null);
    }

    @Test
    public void testBindRoleToBean() throws Exception {
        final Context context = new Context();
        final Object bean = new Object();
        context.put(Role.ANY, bean);
        assertEquals(bean, context.bean(Role.ANY));
    }

    @Test
    public void testBindTypeToBean() throws Exception {
        final Context context = new Context();
        context.put(String.class, "");
        assertEquals("", context.bean(String.class));
    }

    @Test
    public void testBindRoleToSupplier() throws Exception {
        final Context context = new Context();
        final Object bean = new Object();
        context.put(Role.ANY, () -> bean);
        assertEquals(bean, context.bean(Role.ANY));
    }


    @Test
    public void testBindTypeToSupplier() throws Exception {
        final Context context = new Context();
        context.put(String.class, () -> "");
        assertEquals("", context.bean(String.class));
    }

    @Test
    public void testAccessToParentsBean() throws Exception {
        final Context parent = new Context();
        final Context context = new Context(parent);
        final Object bean = new Object();
        parent.put(Role.ANY, bean);
        assertEquals(bean, context.bean(Role.ANY));
    }

    @Test(expected = JustJSystemException.class)
    public void testPutDuplicateBeanByRole() throws Exception {
        final Context context = new Context();
        context.put(Role.ANY, new Object());
        context.put(Role.ANY, new Object());
    }

    @Test(expected = JustJSystemException.class)
    public void testPutDuplicateBeanByType() throws Exception {
        final Context context = new Context();
        context.put(String.class, "");
        context.put(String.class, "");
    }

    @Test(expected = JustJSystemException.class)
    public void testAccessToAbsentBean() throws Exception {
        new Context().bean(Role.ANY);
    }

    @Test(expected = JustJSystemException.class)
    public void testAccessToInvalidSupplier() throws Exception {
        final Context context = new Context();
        context.put(Role.ANY, () -> null );
        context.bean(Role.ANY);
    }

    @Test(expected = JustJSystemException.class)
    public void testPutDuplicateBeanByTypeWithParent() throws Exception {
        final Context parent = new Context();
        parent.put(String.class, "");
        final Context context = new Context(parent);
        context.put(String.class, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutUnsuitableBean() throws Exception {
        final Context context = new Context();
        context.put(role("Integer", Integer.class), "");
    }


    @Test
    @Ignore
    public void testEqualsContract() {
        EqualsVerifier
                .forClass(Context.class)
                .suppress(Warning.NULL_FIELDS)
                .withRedefinedSuperclass()
                .withPrefabValues(Context.class, new Context(), new Context())
                .withPrefabValues(BeansHolder.class, new DummyBeansHolder(), new DummyBeansHolder())
                .verify();
    }

    private static class DummyBeansHolder implements BeansHolder {
        @Override
        public BeanWrapper wrapper(BeanIdentifier id) {
            return null;
        }
        @Override
        public void put(Supplier<BeanWrapper> supplier, BeanIdentifier id) {
        }
        @Override
        public List<BeanWrapper> wrappers() {
            return null;
        }
    }
}