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

package org.maxur.jj.core.scope;

import org.maxur.jj.core.context.Configuration;
import org.maxur.jj.core.context.Context;
import org.maxur.jj.core.context.Role;
import org.maxur.jj.core.entity.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static org.maxur.jj.core.context.Role.HOME_VIEW;
import static org.maxur.jj.core.context.Role.SYSTEM;

/**
 * The Configurator class is only static launcher of Configuration service.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/11/2014</pre>
 */
public class JJSystem extends JJScope<SystemContext> {

    private final static Logger LOGGER = LoggerFactory.getLogger(JJSystem.class);

    public static JJSystem system(final Configuration configuration) {
        final SystemContext context = new SystemContext();
        context.configBy(configuration);
        final JJSystem result = context.system() == null ? new JJSystem() : context.system();
        result.setContext(context);
//        result.add(context.<JJView>view()); // TODO it's different for different system
        return result;
    }

    public static Configuration configBy(Class<? extends Configuration> configurationClass) {
        try {
            // scan directory for Main Configuration Descriptor
            // read Configuration
            return configurationClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error(
                    format("The config from class '%s' is invalid", configurationClass.getName()),
                    e
            );
            throw new IllegalArgumentException(e);
        }
    }

    public void runWith(final String[] args) {
        accept(interpret(args));
        run();
    }

    public void run() {
        while (isActive()) {
            final Command<JJScope> command = getCommand();
            if (command != null) {
                accept(command);              // TODO start Request context
            }
        }
    }

    private Command<JJScope> interpret(final String[] args) {
        return context().interpreter().interpret(args);
    }

    protected static Command<JJScope> getCommand() {
        return JJScope.exitCmd();   // TODO
    }
}

class SystemContext extends Context {

    public final <T extends JJView> T view() {
        return bean(HOME_VIEW);
    }

    public JJSystem system() {
        return bean(SYSTEM);
    }

    public ArgToCmdMapper interpreter() {
        return bean(Role.INTERPRETER, ArgToCmdMapper.DEFAULT);
    }

}
