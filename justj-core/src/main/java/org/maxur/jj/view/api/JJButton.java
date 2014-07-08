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

import org.maxur.jj.service.api.JJCommand;
import org.maxur.jj.service.api.JJContext;
import org.maxur.jj.utils.Strings;

/**
 * @author Maxim Yunusov
 * @version 1.0 08.07.2014
 */
public class JJButton extends JJWidget {

    private final String hotKey;

    private final JJCommand<? extends JJContext> command;

    public JJButton(
            final String name,
            final String text,
            final JJCommand<? extends JJContext> command
    ) {
        super(name, Strings.extract(text, '&'));
        this.command = command;
        final int i = text.indexOf('&');
        hotKey = i == -1 ? null : ("" + text.charAt(i + 1)).toUpperCase();
    }

    @Override
    public void doShow() {
        if (hotKey != null) {
            System.out.printf("(%s)\t", hotKey);
        }
        System.out.println(getName()); // TODO to CLI
    }

    public String getHotKey() {
        return hotKey;
    }

    public JJCommand<? extends JJContext> getCommand() {
        return command;
    }

}
