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

package org.maxur.jj.test;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.maxur.jj.test.ImmutableVerifier.forClass;
import static org.maxur.jj.test.ImmutableVerifier.forInstance;

public class ImmutableVerifierTest {

    @Test
    public void testForInstance() throws Exception {
        assertNotNull(forInstance(new Immutable()));
    }

    @Test
    public void testForClass() throws Exception {
        assertNotNull(forClass(Immutable.class));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testForInstanceWithNull() throws Exception {
        forInstance(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForClassWithNull() throws Exception {
        forClass(null);
    }

    @Test
    public void testVerifyImmutable() throws Exception {
        forClass(Immutable.class).verify();
    }

    @Test(expected = AssertionError.class)
    public void testVerifyMutable() throws Exception {
        forClass(Mutable.class).verify();
    }

    @Test
    public void testObjectMustBeDirectChildForDirectChild() throws Exception {
        forClass(DirectChild.class).
                        withSuperclass(Mutable.class).
                        verify();
    }

    @Test(expected = AssertionError.class)
    public void testObjectMustBeDirectChildForUnDirectChild() throws Exception {
        forClass(UnDirectChild.class).
                withSuperclass(Mutable.class).
                verify();
    }

    @Test
    public void testVerifyNonFinalWithSuppress() throws Exception {
        forClass(NonFinal.class).
                suppress(Warning.NON_FINAL_CLASS).
                verify();
    }

    @Test
    public void testAllFieldsMustBeFinal() throws Exception {
        forClass(AllFieldFinal.class).verify();
    }

    @Test(expected = AssertionError.class)
    public void testNotAllFieldsMustBeFinal() throws Exception {
        forClass(NotAllFieldFinal.class).verify();
    }

    @Test(expected = AssertionError.class)
    public void testAllFieldsMustBeImmutableWithNotAllFieldImmutable() throws Exception {
        forClass(NotAllFieldImmutable.class).verify();
    }

    @Test
    public void testAllFieldsMustBeImmutableWithSuppress() throws Exception {
        forClass(NotAllFieldImmutable.class)
                .suppress(Warning.IMMUTABLE_FIELDS)
                .verify();
    }

    @Test
    public void testAllFieldsMustBeImmutable() throws Exception {
        forClass(AllFieldImmutable.class)
                .verify();
    }

    @Test
    public void testAllFieldsMustBeImmutableWithCircularDependencies() throws Exception {
        forClass(CircularDependencies.class)
                .verify();
    }


}

final class Immutable {
}

class Mutable {
}

final class DirectChild extends Mutable {
}

class Mediator extends Mutable {
}

final class UnDirectChild extends Mediator {
}

class NonFinal {
}

final class AllFieldFinal {
    final String a;
    final String b;

    AllFieldFinal() {
        a = null;
        b = null;
    }
}

final class NotAllFieldFinal {
    final String a;
    String b;

    NotAllFieldFinal() {
        a = null;
        b = null;
    }
}

final class NotAllFieldImmutable {
    final NotAllFieldFinal a;
    final String b;

    NotAllFieldImmutable() {
        a = null;
        b = null;
    }
}

final class AllFieldImmutable {
    final Immutable a;
    final String b;

    AllFieldImmutable() {
        a = null;
        b = null;
    }
}


final class CircularDependencies {

    final CircularDependencies a;

    CircularDependencies() {
        a = null;
    }
}