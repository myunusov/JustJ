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

package org.maxur.jj.view.api;

import org.maxur.jj.service.api.JJEntity;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/8/2014</pre>
 */
public abstract class JJWidget extends JJEntity {

    private final String text;

    public JJWidget(final String name, final String text) {
        super(name);
        this.text = text;
    }

    public final void show() {
        beforeShow();
        if (isVisible()) {
            doShow();
        }
        afterShow();
    }

    protected void afterShow() {
    }

    protected void beforeShow() {
    }

    protected abstract void doShow();

    public String getText() {
        return text;
    }

    public boolean isVisible() {
        return true;
    }

}
