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

package org.maxur.jj.core.context.dummy;

import javax.inject.Inject;

/**
 * @author Maxim Yunusov
 * @version 1.0 09.08.2014
 */
public class Dummy32  {
    @Inject
    public Dummy a;
    public Dummy b;
    public Dummy e;
    public Dummy f;
    public Dummy g;

    public int count = 0;

    public Dummy32() {
    }
    @Inject
    public void setB(Dummy b) {
        this.b = b;
    }
    @Inject
    public void setE(Dummy e) {
        this.e = e;
    }
    @Inject
    public void setF(Dummy f) {
        this.f = f;
        count++;
    }

    public void setG(Dummy g) {
        this.g = g;
    }

}
