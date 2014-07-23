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
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.function.Supplier;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConfigTest {

    @Mock
    private Context context;

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
            config.bind(Role.ANY).to(object);
            return null;
        };
        doAnswer(answer).when(config).config(context);
        config.config(context);
        verify(context).put(Role.ANY, object);
    }

    @Test
    public void testCallContextPutOnBindTypeToObject() throws Exception {
        final Object object = "";
        final Answer answer = invocation -> {
            config.bind(String.class).to(object);
            return null;
        };
        doAnswer(answer).when(config).config(context);
        config.config(context);
        verify(context).put(String.class, object);
    }

    @Test
    public void testCallContextPutOnBindRoleToSupplier() throws Exception {
        final Supplier<Object> supplier = Object::new;
        final Answer answer = invocation -> {
            config.bind(Role.ANY).to(supplier);
            return null;
        };
        doAnswer(answer).when(config).config(context);
        config.config(context);
        verify(context).put(Role.ANY, supplier);
    }

    @Test
    public void testCallContextPutOnBindTypeToSupplier() throws Exception {
        final Supplier<Object> supplier = () -> "";
        final Answer answer = invocation -> {
            config.bind(String.class).to(supplier);
            return null;
        };
        doAnswer(answer).when(config).config(context);
        config.config(context);
        verify(context).put(String.class, supplier);
    }

    @Test
    public void testEqualsContract() {
        EqualsVerifier
                .forClass(Config.class)
                .suppress(Warning.NULL_FIELDS)
                .withRedefinedSuperclass()
                .withPrefabValues(Context.class, new Context(), new Context())
                .verify();
    }


}