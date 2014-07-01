/*
 * Copyright (c) 2014 Maxim Yunusov
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.maxur.jj.utils;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.maxur.jj.utils.Contracts.notContainsNull;
import static org.maxur.jj.utils.Contracts.notNull;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/30/14</pre>
 */
public class ContractsTest {

    @Test()
    public void testIsNotContainsNullWithoutNull() throws Exception{
        notContainsNull(new String[]{"1", "2"});
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testIsNotContainsNullWithNull() throws Exception{
        assertArrayEquals(new String[]{"1", "2", null}, notContainsNull(new String[]{"1", "2", null}));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testIsNotContainsNullArrayIsNull() throws Exception{
        notContainsNull(null);
    }

    @Test
    public void testNotNullWithoutNull() throws Exception{
        assertEquals("1", notNull("1"));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testNotNullWithNull() throws Exception{
        notNull(null);
    }



}
