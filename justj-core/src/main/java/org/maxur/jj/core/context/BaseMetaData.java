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

package org.maxur.jj.core.context;

import org.maxur.jj.core.domain.JustJSystemException;

import java.util.*;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Optional.empty;

/**
 * @author Maxim Yunusov
 * @version 1.0 20.07.2014
 */
public class BaseMetaData implements MetaData {

    private final Optional<MetaData> parent;

    private final Map<BeanReference, BeanWrapper> beans = new HashMap<>();

    public BaseMetaData() {
        this.parent = empty();
    }

    public BaseMetaData(final BaseMetaData parent) {
        this.parent = Optional.of(parent);
    }

    @Override
    public BeanWrapper wrapper(final BeanReference ref) {
        final BeanWrapper wrapper = beans.get(ref);
        if (wrapper == null && parent.isPresent()) {
            return parent.get().wrapper(ref);
        }
        return wrapper;
    }

    @Override
    public void put(final Supplier<BeanWrapper> supplier, final BeanReference ref) {
        checkDuplicate(ref);
        final BeanWrapper wrap = supplier.get();
        checkType(ref, wrap);
        beans.put(ref, wrap);
    }

    public void checkType(BeanReference id, BeanWrapper wrap) {
        if (!wrap.suitableTo(id.getType())) {
            throw new IllegalArgumentException(format(
                    "The type '%s' is not suitable to %s",
                    wrap.type().getName(),
                    id.toString()
            ));
        }
    }

    private void checkDuplicate(final BeanReference ref) {
        if (wrapper(ref) != null) {
            throw new JustJSystemException("%s is already exists", ref.toString());
        }
    }

}
