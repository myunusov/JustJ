package org.maxur.jj.core.config;

import org.maxur.jj.core.entity.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0 12.07.2014
 */
public class Context {

    private final static Logger LOGGER = LoggerFactory.getLogger(Context.class);

    private Context parent = null;

    final private Map<Role, BeanWrapper> roleToBean = new HashMap<>();

    public void configBy(final Configuration configuration) {
        configuration.config(this);
    }

    public <T> T bean(final Role role) {
        BeanWrapper wrapper = roleToBean.get(role);
        if (wrapper == null && parent != null) {
            wrapper = parent.bean(role);
        }
        //noinspection unchecked
        return wrapper != null ? (T) wrapper.bean() : null; //TODO
    }

    public <T> T bean(final Role role, T defaultBean) {
        final T bean = bean(role);
        return bean == null ? defaultBean : bean;
    }


    void put(final Role role, final Class<?> beanClass) {
        put(role, BeanWrapper.wrap(beanClass));
    }

    void put(final Role role, final Object bean) {
        put(role, BeanWrapper.wrap(bean));
    }

    private void put(final Role role, final BeanWrapper wrap) {
        if (wrap.suitableTo(role)) {
            roleToBean.put(role, wrap);
            return;
        }
        final String message = format(
                "We type '%s' is not suitable to role '%s'",
                wrap.type().getName(),
                role.toString()
        );
        LOGGER.error(message);
        throw new IllegalArgumentException(message);
    }

    public void setParent(final Context parent) {
        this.parent = parent;
    }

    private static class BeanWrapper {

        private final Object bean;

        private static BeanWrapper wrap(final Class<?> beanClass) {
            return new BeanWrapper(beanClass);
        }
        public static BeanWrapper wrap(final Object bean) {
            return new BeanWrapper(bean);
        }

        public BeanWrapper(final Object bean) {
            this.bean = bean;
        }
        private BeanWrapper(final Class<?> beanClass) {
            try {
                bean = beanClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error(
                        format("We cannot create instance of class '%s'", beanClass.getName()),
                        e
                );
                throw new IllegalArgumentException(e);
            }
        }

        public Class<?> type() {
            return bean.getClass();
        }

        public <T> T bean() {
            //noinspection unchecked
            return (T) bean;  // TODO must be catch exception
        }

        private boolean suitableTo(final Role role) {
            return true;    // TODO
        }
    }


}
