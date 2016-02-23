package org.maxur.justj.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents set of ui commands as prototypes
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>23.02.2016</pre>
 */
public class Menu {

    private final Set<Command> commands = new HashSet<>();

    public Set<Command> commands() {
        return Collections.unmodifiableSet(commands);
    }

    public void register(final Command command) {
        commands.add(command);
    }


}
