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

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.maxur.jj.utils.Arrays.toSet;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/29/14</pre>
 */
public class ArraysTest {

    @Test
    public void testToSetOne() throws Exception {
        final Set<String> result = toSet("1");
        assertEquals(1, result.size());
        assertEquals("1", result.iterator().next());
    }

    @Test
    public void testToSetLot() throws Exception {
        final Set<String> result = toSet("1", "2");
        assertEquals(2, result.size());
        assertTrue(result.contains("1"));
        assertTrue(result.contains("2"));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testToSetArrayWithNull() throws Exception {
        toSet("1", null);
    }


    @Test
    public void testContains() throws Exception {

    }
}
