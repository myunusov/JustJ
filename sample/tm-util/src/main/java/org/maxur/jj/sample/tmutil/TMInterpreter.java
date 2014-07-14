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

package org.maxur.jj.sample.tmutil;

import org.maxur.jj.core.entity.JJCommand;
import org.maxur.jj.core.system.CommandInterpreter;

import static org.maxur.jj.core.entity.JJCommand.command;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/14/2014</pre>
 */
public class TMInterpreter extends CommandInterpreter {

    @Override
    public JJCommand interpret(final String[] args) {
        return command(c -> System.out.println("Hello"));      // TODO Must be Compound (Batch) command
    }

}
