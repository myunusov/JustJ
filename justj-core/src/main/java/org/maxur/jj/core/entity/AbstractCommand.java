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
public abstract class AbstractCommand<T> extends Visitor<TreeNode> {


    private final Class genericType;

    public static <T> AbstractCommand<T> command(final Command<T> command) {
        return new AbstractCommand<T>() {
            @Override
            protected void process(T subject) {
                command.execute(subject);
            }
        };
    }

    public static BatchCommand batch() {
        return new BatchCommand();
    }

    protected AbstractCommand() {
        super();
        final Type superclass = getClass().getGenericSuperclass();
        genericType = superclass instanceof ParameterizedType &&
                (((ParameterizedType) superclass).getActualTypeArguments()[0]) instanceof Class ?
                (Class) (((ParameterizedType) superclass).getActualTypeArguments()[0])
                : Object.class;
    }

    @Override
    public void accept(final TreeNode subject) {
        execute(subject);
    }

    @SuppressWarnings("unchecked")
    public final void execute(final Object subject) {
        if (applicableType(subject)) {
            if (isApplicableTo((T) subject)) {
                process((T) subject);
            }
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
        return format("command '%s' {%s}", getClass().getName(), id);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof AbstractCommand && id.equals(((AbstractCommand) o).id);
    }


    @FunctionalInterface
    public interface Command<T> {
        void execute(T value);
    }

    public static class BatchCommand extends AbstractCommand {

        private final List<AbstractCommand> commands = new ArrayList<>();

        private BatchCommand() {

        }

        public BatchCommand add(final AbstractCommand command) {
            this.commands.add(command);
            return this;
        }

        public BatchCommand add(final Command command) {
            this.commands.add(command(command));
            return this;
        }

        @Override
        protected void process(Object subject) {
            for (AbstractCommand command : commands) {
                command.execute(subject);
            }
        }

        @Override
        public boolean isApplicableTo(@SuppressWarnings("UnusedParameters") Object subject) {
            return true;
        }
    }
}

