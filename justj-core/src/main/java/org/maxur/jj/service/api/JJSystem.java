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

import org.maxur.jj.view.api.JJView;

import java.util.Scanner;

/**
 * @author Maxim Yunusov
 * @version 1.0 07.07.2014
 */
public abstract class JJSystem {

    private final JJContext context;

    public JJSystem(final JJContext context) {
        this.context = context;
    }

    public final void run() {
        onStart();
        process();
        onStop();
    }

    protected void process() {
        JJView currentView = context().mainView();
                                               // TODO CLI Application Special Case
        final Scanner scanner = new Scanner(System.in);
        while (currentView != null) {
            currentView.show();
            if (scanner.hasNext()) {
                final String token = scanner.next();
                currentView.command(token);
                final JJCommand command = currentView.command(token);
                if (command == null) {
                    onInvalidCommand(token);
                } else {
                    //noinspection unchecked
                    currentView = (JJView) command.execute(currentView);
                }
            }
        }
    }



    public JJContext context() {
        return context;
    }

    protected abstract void onStop();

    protected abstract void onStart();

    protected abstract void onInvalidCommand(final String token);
}
