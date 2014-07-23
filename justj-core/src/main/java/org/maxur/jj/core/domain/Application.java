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

import static org.maxur.jj.core.domain.Role.role;

/**
 * @author Maxim Yunusov
 * @version 1.0 23.07.2014
 */
public interface Application {

    Role APPLICATION = role("Application", Application.class);

    default void runWith(final String[] args) {
        // It's hook
    }

    default void run() {
        runWith(new String[]{});
    }
}
