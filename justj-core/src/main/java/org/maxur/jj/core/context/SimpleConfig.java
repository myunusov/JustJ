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

import java.util.function.Consumer;

/**
* @author Maxim Yunusov
* @version 1.0 25.07.2014
*/
public class SimpleConfig extends Config {

    private Consumer<Config> consumer;

    private SimpleConfig(final Consumer<Config> consumer) {
        this.consumer = consumer;
    }

    public static Config config(final Consumer<Config> consumer) {
        return new SimpleConfig(consumer);
    }

    @Override
    public void config() {
        consumer.accept(this);
    }
}
