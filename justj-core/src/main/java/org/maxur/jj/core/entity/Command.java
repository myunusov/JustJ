package org.maxur.jj.core.entity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0 12.07.2014
 */
public abstract class Command<T> extends Visitor<TreeNode> {

    private final Class genericType;

    public static <T> Command<T> command(final CommandFunction<T> command) {
        return new Command<T>() {
            @Override
            protected void process(T subject) {
                command.execute(subject);
            }
        };
    }

    public static <T> BatchCommand<T> batch() {
        return new BatchCommand<>();
    }

    protected Command() {
        super();
        final Type superclass = getClass().getGenericSuperclass();
        genericType = superclass instanceof ParameterizedType &&
                (((ParameterizedType) superclass).getActualTypeArguments()[0]) instanceof Class ?
                (Class) (((ParameterizedType) superclass).getActualTypeArguments()[0])
                : Object.class;
    }

    @Override
    public void visit(final TreeNode subject) {
        execute(subject);
    }

    @SuppressWarnings("unchecked")
    public final void execute(final Object subject) {
        if (applicableType(subject) && isApplicableTo((T) subject)) {
            process((T) subject);
        }
    }

    public boolean isApplicableTo(@SuppressWarnings("UnusedParameters") final T subject) {
        return true;
    }

    private boolean applicableType(final Object subject) {
        //noinspection unchecked
        return genericType == null || genericType.isAssignableFrom(subject.getClass());
    }

    protected abstract void process(final T subject);

    @Override
    public String toString() {
        return format("Command '%s' {%s}", getClass().getName(), getId());
    }

    @FunctionalInterface
    public interface CommandFunction<T> {
        void execute(T value);
    }

    public static class BatchCommand<T> extends Command<T> {

        private final List<Command<? extends T>> commands = new ArrayList<>();

        private BatchCommand() {
        }

        public BatchCommand<T> add(final Command<? extends T> command) {
            this.commands.add(command);
            return this;
        }

        public BatchCommand<T> add(final CommandFunction<? extends T> command) {
            this.commands.add(command(command));
            return this;
        }

        @Override
        protected void process(T subject) {
            for (Command command : commands) {
                command.execute(subject);
            }
        }

        @Override
        public boolean isApplicableTo(@SuppressWarnings("UnusedParameters") Object subject) {
            return true;
        }
    }
}

