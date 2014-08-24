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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maxur.jj.core.domain.Role;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.maxur.jj.core.context.BeanIdentifier.identifier;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConfigTest {

    @Mock
    private BaseScope scope;

    @Spy
    private Config config = new Config() {
        @Override
        protected void config() {
        }
    };

    @Test
    public void testCallContextPutOnBindRoleToObject() throws Exception {
        final Object object = new Object();
        final Answer answer = invocation -> {
            new BaseScope();
            config.bind(Role.any()).to(object);
            return null;
        };
        doAnswer(answer).when(config).applyTo(scope);
        config.applyTo(scope);
        verify(scope).addBean(identifier(Role.any()), object);
    }

    @Test
    public void testCallContextPutOnBindTypeToObject() throws Exception {
        final String object = "";
        final Answer answer = invocation -> {
            config.bind(String.class).to(object);
            return null;
        };
        doAnswer(answer).when(config).applyTo(scope);
        config.applyTo(scope);
        verify(scope).addBean(identifier(String.class), object);
    }

    @Test
    public void testCallContextPutOnBindRoleToSupplier() throws Exception {
        final Supplier<Object> supplier = Object::new;
        final Answer answer = invocation -> {
            config.bind(Role.any()).to(supplier);
            return null;
        };
        doAnswer(answer).when(config).applyTo(scope);
        config.applyTo(scope);
        ArgumentCaptor<Supplier> argument = ArgumentCaptor.forClass(Supplier.class);
        verify(scope).addSupplier(eq(identifier(Role.any())), argument.capture());
        assertEquals(supplier.hashCode(), argument.getValue().hashCode());
    }

    @Test
    public void testCallContextPutOnBindTypeToSupplier() throws Exception {
        final Supplier<String> supplier = () -> "";
        final Answer answer = invocation -> {
            config.bind(String.class).to(supplier);
            return null;
        };
        ArgumentCaptor<Supplier> argument = ArgumentCaptor.forClass(Supplier.class);
        doAnswer(answer).when(config).applyTo(scope);
        config.applyTo(scope);
        verify(scope).addSupplier(eq(identifier(String.class)), argument.capture());
        assertEquals(supplier.hashCode(), argument.getValue().hashCode());
    }

    @Test
    public void testCallContextPutOnBindRoleToType() throws Exception {
        final Answer answer = invocation -> {
            config.bind(Role.any()).to(DummyObject.class);
            return null;
        };
        doAnswer(answer).when(config).applyTo(scope);
        config.applyTo(scope);
        ArgumentCaptor<Class<DummyObject>> argument =
                ArgumentCaptor.forClass((Class) DummyObject.class.getClass());
        verify(scope).addType(eq(identifier(Role.<DummyObject>any())), argument.capture());
        assertEquals(argument.getValue(), DummyObject.class);
    }

   @Test
    public void testCallContextPutOnBindTypeToType() throws Exception {
       final Answer answer = invocation -> {
           config.bind(Object.class).to(DummyObject.class);
           return null;
       };
       doAnswer(answer).when(config).applyTo(scope);
       config.applyTo(scope);
       ArgumentCaptor<Class<DummyObject>> argument =
               ArgumentCaptor.forClass((Class) DummyObject.class.getClass());
       verify(scope).addType(eq(identifier(Object.class)), argument.capture());
       assertEquals(argument.getValue(), DummyObject.class);
    }

    @Test
    public void testEqualsContract() {
        EqualsVerifier
                .forClass(Config.class)
                .suppress(Warning.NULL_FIELDS)
                .withRedefinedSuperclass()
                .withPrefabValues(BaseScope.class, new BaseScope(), new BaseScope())
                .verify();
    }

}

class DummyObject {

}