package org.maxur.jj.core.entity;

/**
 * @author Maxim Yunusov
 * @version 1.0 17.07.2014
 */
public abstract class Transformer<T extends Visitable, Z> extends Visitor<T> {

  /*  private final Class genericType;

    public static <T extends Visitable, Z> Transformer<T, Z> transformer(final Function<T, Z> function) {
        return new Transformer<T, Z>() {
            @Override
            protected Z process(T subject) {
                return function.apply(subject);
            }
        };
    }

    public static <T extends Visitable> Batch<T> batch() {
        return new Batch<>();
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
    public Z visit(final T subject) {
        return transform(subject);
    }

    @SuppressWarnings("unchecked")
    public final Z transform(final T subject) {
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

    protected abstract Z process(final T subject);

    @Override
    public String toString() {
        return format("Command '%s' {%s}", getClass().getName(), getId());
    }

    @FunctionalInterface
    public interface CommandFunction<T> {
        void execute(T value);
    }

    public static class Batch<T> extends Command<T> {

        private final List<Command<? extends T>> commands = new ArrayList<>();

        private Batch() {
        }

        public Batch<T> add(final Command<? extends T> command) {
            this.commands.add(command);
            return this;
        }

        public Batch<T> add(final CommandFunction<? extends T> command) {
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
*/

                    // TODO


}
