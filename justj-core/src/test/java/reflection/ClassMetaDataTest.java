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

package reflection;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static reflection.ClassMetaData.meta;

public class ClassMetaDataTest {

    @Test
    public void testObjectParents() throws Exception {
        assertEquals(Collections.<ClassMetaData>singletonList(meta(Object.class))  ,meta(Object.class).parents());
    }

    @Test
    public void testParents() throws Exception {
        assertTrue(meta(Dummy2.class).parents().contains(meta(Dummy1.class)));
    }

    @Test
    public void testMethods() throws Exception {
        assertEquals(meta(Object.class).methods().size() + 1 , meta(Dummy1.class).methods().size());
    }

    @Test
    public void testMethodsWithOverload() throws Exception {
        assertEquals(meta(Dummy1.class).methods().size(), meta(Dummy2.class).methods().size());
    }

}

class Dummy1 {

    void a() {
    }

}

class Dummy2 extends Dummy1 {

    void a() {
    }

}