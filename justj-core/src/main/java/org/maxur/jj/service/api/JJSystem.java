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

package org.maxur.jj.service.api;

import java.util.Scanner;

/**
 * @author Maxim Yunusov
 * @version 1.0 07.07.2014
 */
public abstract class JJSystem {

    // TODO CLI Application Special Case
    final Scanner scanner = new Scanner(System.in);

    private final JJContext context;

    public JJSystem(final JJContext context) {
        this.context = context;
    }

    public final void run(final JJCommand<JJContext> command) {
        onStartSystem();
        process(command);
        onStopSystem();
    }

    protected final void process(final JJCommand<JJContext> command) {
        JJCommand<JJContext> nextCommand = command;                // XXX Async event
        while (!context.isTerminated()) {
            nextCommand = nextCommand != null ? nextCommand : getCommand(context);
            context.startRequest();
            onStartRequest();
            if (nextCommand != null) {
                nextCommand.execute(context);
                nextCommand = null;
            }
            context.stopRequest();
            onStopRequest();
        }
    }

    protected JJCommand<JJContext> getCommand(final JJContext context) {
        final JJCommand<JJContext> command;                    // TODO Must be moved to CLI
        if (scanner.hasNext()) {
            final String token = scanner.next();
            command = context.command(token);
            if (command == null) {
                onInvalidCommand(token);
            }
        } else {
            command = null;
        }
        return command;
    }

    private void onStartRequest() {
    }

    private void onStopRequest() {
    }

    protected void onStopSystem() {
    }

    protected void onStartSystem() {
    }

    protected abstract void onInvalidCommand(final String token);

    public JJContext context() {
        return context;
    }
}
