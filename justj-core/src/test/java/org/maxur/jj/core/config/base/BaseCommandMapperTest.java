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

package org.maxur.jj.core.config.base;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.maxur.jj.core.domain.CommandMapper;
import org.maxur.jj.core.context.Context;
import org.maxur.jj.test.ImmutableVerifier;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BaseCommandMapperTest {

    @Mock
    private Context context;

    @Test
    public void testImmutable() throws Exception {
        ImmutableVerifier
                .forInstance(new BaseCommandMapper())
                .withSuperclass(CommandMapper.class)
                .verify();
    }
}