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

package org.maxur.jj.core.entity;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>7/15/2014</pre>
 */
public abstract class Visitor<T> extends Entity {

    private State state = State.CONTINUE_TRAVERSAL;

    public State state() {
        return this.state;
    }

    protected void stop() {
        state = State.STOP_TRAVERSAL;
    }

    protected void dontGoDeeper() {
        state = State.CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER;
    }

    public abstract void accept(T subject);

    public static enum State {
        CONTINUE_TRAVERSAL,
        CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER,
        STOP_TRAVERSAL
    }


}
