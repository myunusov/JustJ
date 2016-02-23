package org.maxur.justj.core.cli;

import org.maxur.justj.core.Command;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;


/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public abstract class CliCommand implements Command<Void, Integer> {

    private ErrorCode errorCode = ErrorCode.OK;

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

    @Override
    public abstract CliCommand execute();

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
