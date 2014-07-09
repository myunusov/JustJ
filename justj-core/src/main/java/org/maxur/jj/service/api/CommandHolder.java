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

package org.maxur.jj.service.api;

import org.maxur.jj.view.api.JJView;

import java.util.HashMap;
import java.util.Map;

import static org.maxur.jj.utils.Strings.left;
import static org.maxur.jj.utils.Strings.right;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/8/2014</pre>
 */
public class CommandHolder {

    public static final char SEPARATOR = ' ';

    public Map<String, JJCommand<? extends JJContext>> commands = new HashMap<>();

    public <O extends JJView> JJCommand command(final String token) {
        final String name = left(token, SEPARATOR);
        final JJCommand<? extends JJContext> command = commands.get(name.toUpperCase());
        if (command == null) {
            return null;
        }
        final JJCommand jjCommand = command.clone();
        jjCommand.params(right(token, SEPARATOR));
        return jjCommand;
    }

    public void add(final JJCommand<? extends JJContext> command) {
        commands.put(command.getName().toUpperCase(), command);
    }
}
