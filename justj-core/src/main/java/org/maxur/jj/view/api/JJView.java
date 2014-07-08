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
import org.maxur.jj.service.api.JJEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maxim Yunusov
 * @version 1.0 07.07.2014
 */
public abstract class JJView extends JJEntity implements JJWidget {

    private final CommandHolder holder = new CommandHolder();

    private final List<JJWidget> widgets = new ArrayList<>();

    public JJView(final String name) {
        super(name);
    }

    public JJView(final String id, final String name) {
        super(id, name);
    }

    public <T extends JJView, O extends JJView> JJCommand<T, O> add(final JJActionCommand<T, O> command) {
        widgets.add(command);
        return holder.add(command);
    }

    public JJLabel add(final JJLabel label) {
        return widgets.add(label) ? label : null;
    }

    public void show() {
        for (JJWidget widget : widgets) {
            widget.show();
        }
    }

    public JJCommand command(String token) {
        return holder.command(token);
    }

}
