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

import org.maxur.jj.service.api.JJActionCommand;
import org.maxur.jj.service.api.JJCommand;
import org.maxur.jj.service.api.JJContext;
import org.maxur.jj.view.api.JJButton;
import org.maxur.jj.view.api.JJLabel;
import org.maxur.jj.view.api.JJView;
import org.maxur.jj.view.api.JJWidget;

/**
 * @author Maxim Yunusov
 * @version 1.0 07.07.2014
 */
public class MainView extends JJView {

    private final JJContext context;

    private boolean isNew = true;

    public MainView(final JJContext context) {
        super("mainView", "Maze Application");
        this.context = context;
        label("mainMenu", "Main Menu");
        button("E&xit", new JJActionCommand<>("exit", v -> null));
        button("&Refresh", new JJActionCommand<>("refresh", v -> new MainView(this.context)));
    }

    protected JJLabel label(final String name, final String text) {
        return add(new JJLabel(name, text));
    }

    protected JJButton button(
            final String text,
            final JJCommand<? extends JJWidget, ? extends JJWidget> command
    ) {
        return add(new JJButton(command.getName(), text, command));
    }


    @Override
    public boolean isVisible() {
        return isNew;
    }

    protected void afterShow() {
        isNew = false;
    }

}
