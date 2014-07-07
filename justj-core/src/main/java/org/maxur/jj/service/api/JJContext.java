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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Maxim Yunusov
 * @version 1.0 07.07.2014
 */
public abstract class JJContext {

    public static final String MAIN_VIEW = "mainView";
    public static final String SYSTEM = "system";

    private final Set<ContextMapper> set = new HashSet<>();

    public void init() {
    }

    public void load() {
    }

    protected void mainView(final JJView bean) {
        name(MAIN_VIEW).map(bean);
    }

    protected void system(final JJSystem system) {
        name(SYSTEM).map(system);
    }

    protected ContextMapper name(final String name) {
        return new ContextMapper(this, name);
    }

    public JJView mainView() throws JJContextException {
        return bean(MAIN_VIEW);
    }

    public JJSystem system() throws JJContextException {
        return bean(SYSTEM);
    }

    public JJView view(final String name) throws JJContextException {
        return bean(name);
    }

    public <T> T bean(final String name) throws JJContextException {
        final Set<ContextMapper> namesakes = set.stream().filter(m -> m.name.equals(name)).collect(Collectors.toSet());
        final List<T> suitable = new ArrayList<>();
        for (ContextMapper mapper : namesakes) {
            try {
                //noinspection unchecked
                suitable.add((T) mapper.bean);                 // TODO
            } catch (ClassCastException e) {
                continue;
            }
        }
        switch (suitable.size()) {
            case 0: throw new JJContextException("Bean '%s' not found", name);
            case 1: return suitable.get(0);
            default: throw new JJContextException("Find more than one bean with name '%s'", name);
        }
    }

    private void persist(final ContextMapper mapper) {
        set.add(mapper);
    }

    public static class ContextMapper {

        private final JJContext context;
        private String name;
        private Class<?> type;
        private Object bean;

        public ContextMapper(final JJContext context, final String name) {
            this.context = context;
            this.name = name;
        }

        public void map(final Object bean) {
            this.bean = bean;
            if (type == null) {
                this.type = bean.getClass();
            }
            if (name == null) {
                this.name = "";
            }
            context.persist(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ContextMapper)) {
                return false;
            }
            final ContextMapper that = (ContextMapper) o;
            return name.equals(that.name) && type.equals(that.type);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + type.hashCode();
            return result;
        }
    }
}
