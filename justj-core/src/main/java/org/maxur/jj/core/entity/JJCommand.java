package org.maxur.jj.core.entity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.UUID;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0 12.07.2014
 */
public abstract class JJCommand<T> {

    private final String id;

    private State state = State.CONTINUE_TRAVERSAL;

    private final Class genericType;

    public static <T> JJCommand<T> command(final Command<T> command) {
        return new JJCommand<T>() {
            @Override
            protected void process(T subject) {
                command.execute(subject);
            }
        };
    }

    protected JJCommand() {
        id = UUID.randomUUID().toString();
        final Type superclass = getClass().getGenericSuperclass();
        genericType = superclass instanceof ParameterizedType &&
                (((ParameterizedType) superclass).getActualTypeArguments()[0]) instanceof Class ?
                (Class) (((ParameterizedType) superclass).getActualTypeArguments()[0])
                : Object.class;
    }

    @SuppressWarnings("unchecked")
    public final void execute(final Object subject) {
        if (!State.STOP_TRAVERSAL.equals(state) && applicableType(subject)) {
            if (isApplicableTo((T) subject)) {
                process((T) subject);
            }
        }
        if (State.CONTINUE_TRAVERSAL.equals(state)) {
            processChildren(subject);
        }
    }

    public boolean isApplicableTo(@SuppressWarnings("UnusedParameters") final T subject) {
        return true;
    }

    protected void processChildren(Object subject) {
        if (subject instanceof TreeNode) {
            final TreeNode node = (TreeNode) subject;
            for (Object child : node) {
                this.execute(child);
            }
        }
    }

    private boolean applicableType(final Object subject) {
        //noinspection unchecked
        return genericType.isAssignableFrom(subject.getClass());
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void stop() {
        state = State.STOP_TRAVERSAL;
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void dontGoDeeper() {
        state = State.CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER;
    }

    protected abstract void process(final T subject);

    @Override
    public String toString() {
        return format("command '%s' {%s}", getClass().getName(), id);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof JJCommand && id.equals(((JJCommand) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private static enum State {
        CONTINUE_TRAVERSAL,
        CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER,
        STOP_TRAVERSAL
    }

    @FunctionalInterface
    public interface Command<T> {
        void execute(T value);
    }
}

