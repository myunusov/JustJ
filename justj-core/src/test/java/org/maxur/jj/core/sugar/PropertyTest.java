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

package org.maxur.jj.core.sugar;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static java.time.LocalDate.now;
import static org.junit.Assert.assertEquals;
import static org.maxur.jj.core.sugar.Property.empty;
import static org.maxur.jj.core.sugar.Property.getter;
import static org.maxur.jj.core.sugar.Property.ro;

public class PropertyTest {

    private Dummy dummy;

    @Before
    public void setUp() throws Exception {
        dummy = new Dummy();
    }

    @Test
    public void testSimpleReadOnlyProperties() throws Exception {
        assertEquals("Иван", dummy.name.get());

    }

    @Test
    public void testSimpleReadWriteProperties() throws Exception {
        dummy.birthdayDate.set(LocalDate.of(2001, 1, 31));
        assertEquals(LocalDate.of(2001, 1, 31), dummy.birthdayDate.get());
    }

    @Test
    public void testReadOnlyProperties() throws Exception {
        dummy.birthdayDate.set(LocalDate.of(now().getYear() - 35, 1, 1));
        assertEquals(new Long(35), dummy.age.get());
    }

    @Test
    public void testReadWriteProperties() throws Exception {
        dummy.rating.set(10);
        assertEquals(new Integer(10), dummy.rating.get());
    }


}

class Dummy {

    public final RO<String> name = ro("Иван");

    public final RW<LocalDate> birthdayDate = empty();

    public final RO<Long> age = getter(
            () -> ChronoUnit.YEARS.between(birthdayDate.get(), now())
    ).build();

    private Integer m_rating;

    public final RW<Integer> rating =
            getter(() -> m_rating)
            .setter(r -> m_rating = r)
            .build();

}