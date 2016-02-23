package org.maxur.justj.core.cli;

import java.util.Objects;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>23.02.2016</pre>
 */
public abstract class CliCommand {

    public abstract CliCommand copy();

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getClass());
    }

}
