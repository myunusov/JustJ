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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.maxur.jj.utils.Strings.empty;
import static org.maxur.jj.utils.Strings.isBlank;
import static org.maxur.jj.utils.Strings.left;
import static org.maxur.jj.utils.Strings.quote;
import static org.maxur.jj.utils.Strings.right;


/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>1/29/14</pre>
 */
public class StringsTest {

    @Test
    public void testIsBlankForEmptyContent() throws Exception {
        assertTrue(isBlank(""));
    }
    @Test
    public void testIsBlankForNullContent() throws Exception {
        assertTrue(isBlank(null));
    }
    @Test
    public void testIsBlankForBlankContent() throws Exception {
        assertTrue(isBlank(" "));
    }
    @Test
    public void testIsBlankForContent() throws Exception {
        assertFalse(isBlank("a"));
    }

    @Test
    public void testLeftForBlank() throws Exception {
        assertEquals("", left("", '.'));
    }

    @Test
    public void testRightForBlank() throws Exception {
        assertEquals("", right("", '.'));
    }

    @Test
    public void testLeftForNull() throws Exception {
        assertNull(left(null, '.'));
    }

    @Test
    public void testRightForNull() throws Exception {
        assertNull(right(null, '.'));
    }

    @Test
    public void testLeftForWholeString() throws Exception {
        assertEquals("aaa bbb", left("aaa bbb", '.'));
    }

    @Test
    public void testRightForWholeString() throws Exception {
        assertEquals("aaa bbb", right("aaa bbb", '.'));
    }

    @Test
    public void testLeftForSeparatedString() throws Exception {
        assertEquals("aaa", left("aaa.bbb", '.'));
    }

    @Test
    public void testRightForSeparatedString() throws Exception {
        assertEquals("bbb", right("aaa.bbb", '.'));
    }

    @Test
    public void testLeftWithoutRight() throws Exception {
        assertEquals("aaa", left("aaa.", '.'));
    }

    @Test
    public void testRightWithoutRight() throws Exception {
        assertEquals("", right("aaa.", '.'));
    }

    @Test
    public void testLeftWithoutLeft() throws Exception {
        assertEquals("", left(".bbb", '.'));
    }

    @Test
    public void testRightWithoutLeft() throws Exception {
        assertEquals("bbb", right(".bbb", '.'));
    }

    @Test
    public void testEmpty() throws Exception {
        assertTrue(empty().equals(""));
    }

    @Test
    public void testQuote() throws Exception {
        assertEquals("\"s\"" ,quote("s"));
    }

    @Test
    public void testQuoteWithNull() throws Exception {
        assertEquals("\"null\"" ,quote(null));
    }

}
