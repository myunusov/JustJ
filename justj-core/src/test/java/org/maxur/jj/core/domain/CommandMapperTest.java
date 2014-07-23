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

package org.maxur.jj.core.domain;

import org.junit.Test;
import org.maxur.jj.test.ImmutableVerifier;


public class CommandMapperTest {

    @Test
    public void testImmutable() throws Exception {
        ImmutableVerifier
                .forInstance(new DummyCommandMapper())
                .withSuperclass(CommandMapper.class)
                .verify();
    }

    private static final class DummyCommandMapper implements CommandMapper {
        @Override
        public Command commandBy(Object input) {
            return null;
        }
    }
}