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

package org.maxur.jj.view.api;

import org.maxur.jj.service.api.CommandHolder;
import org.maxur.jj.service.api.JJCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maxim Yunusov
 * @version 1.0 07.07.2014
 */
public abstract class JJView extends JJWidget {

    private final CommandHolder holder = new CommandHolder();

    private final List<JJWidget> widgets = new ArrayList<>();

    public JJView(final String name, final String text)    {
        super(name, text);
    }

    public JJButton add(final JJButton button) {
        widgets.add(button);
        return holder.add(button);
    }

    public JJLabel add(final JJLabel label) {
        return widgets.add(label) ? label : null;
    }

    public final void doShow() {
        showHeader();
        showBody(widgets);
        showFooter();
    }

    protected void showHeader() {
        System.out.println(getText()); // TODO Must be move to CLI
    }

    protected void showBody(final List<JJWidget> children) {
        for (JJWidget widget : children) {
            widget.show();
        }
    }

    protected void showFooter() {
    }

    public JJCommand command(String token) {
        return holder.command(token);
    }

}
