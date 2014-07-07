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

package org.maxur.jj.sample.adm.view;

import org.maxur.jj.service.api.JJContext;
import org.maxur.jj.view.api.JJView;

/**
 * @author Maxim Yunusov
 * @version 1.0 07.07.2014
 */
public class MainView implements JJView {

    private final JJContext context;

    public MainView(final JJContext context) {
        this.context = context;
    }

    @Override
    public void show() {
        System.out.println("Hello World");
    }

}
