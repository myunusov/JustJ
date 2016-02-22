package org.maxur.justj.core.cli;

import org.maxur.justj.core.Command;
import org.maxur.justj.core.Logger;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;


/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public abstract class CliCommand<O> implements Command<Void, Integer> {

    private final String[] args;

    private final PosixArgumentsMapper<O> argumentsMapper;

    private ErrorCode errorCode = ErrorCode.OK;

    private O arguments;

    private final Logger logger;

    public CliCommand(final String[] args, final Class<O> argumentsClass, final Logger logger) {
        this.args = args;
        argumentsMapper = makeArgumentsMapper(argumentsClass);
        this.logger = logger;
    }

    @Override
    public final Optional<Integer> error() {
        return errorCode.isError() ? of(errorCode.code) : empty();
    }

    @Override
    public Optional<Void> result() {
        return empty();
    }

    public final int errorCode() {
        return errorCode.code;
    }

    public final O arguments() {
        return arguments;
    }

    @Override
    public final CliCommand<O> init() {
        try {
            arguments = argumentsMapper.readValue(args);
        } catch (OptionsProcessingException e) {
            logger.error(e);
            errorCode = ErrorCode.SYSTEM_ERROR;
        }
        return this;
    }

    protected PosixArgumentsMapper<O> makeArgumentsMapper(Class<O> argumentsClass) {
        return new PosixArgumentsMapper<>(argumentsClass);
    }

    @Override
    public abstract CliCommand<O> execute();


    private enum ErrorCode {

        OK(0),

        SYSTEM_ERROR(5);

        private final int code;

        ErrorCode(int code) {
            this.code = code;
        }

        public int code() {
            return code;
        }

        public boolean isError() {
            return !this.equals(OK);
        }

    }

}
