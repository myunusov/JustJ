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

package org.maxur.jj.test;

import java.util.function.Supplier;

/**
 * {@code SingletonVerify} can be used in unit tests to verify whether the
 * class is singleton.
 *
 * @author Maxim Yunusov
 * @version 1.0 19.07.2014
 */
public class SingletonVerify<T>{

    private final Supplier<T> supplier;

    /**
     * Factory method. For general use.
     *
     * @param supplier The supplier of instance for which the immutable should be tested.
     * @param <T> The type of instance for which the immutable should be tested.
     *
     * @return  The instance of ImmutableVerifier.
     */
    public static <T> SingletonVerify forInstance(final Supplier<T> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier must not be null");
        }
        return new SingletonVerify<>(supplier);
    }

    private SingletonVerify(final Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Performs the verification of the contracts for singleton classes.
     *
     * @throws AssertionError If the contract is not met
     */
    public void verify() {
        if (!supplier.get().equals(supplier.get())) {
            throw new AssertionError("Instances of class is not same");
        }
    }

}
