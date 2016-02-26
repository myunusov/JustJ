package org.maxur.justj.core.cli;

import java.util.*;

public class CLiMenuPosixStrategy implements CLiMenuStrategy {

    private static final String NAME_PREFIX = "--";

    private static final String KEY_PREFIX = "-";

    @Override
    public boolean isOptionName(final String arg) {
        return arg.startsWith(NAME_PREFIX);
    }

    @Override
    public boolean isOptionKey(final String arg) {
        return arg.startsWith(KEY_PREFIX) && !isOptionName(arg);
    }

    @Override
    public String extractOptionName(final String arg) {
        return arg.substring(NAME_PREFIX.length());
    }

    @Override
    public Collection<Character> extractOptionKeys(final String arg) {
        final Set<Character> result = new HashSet<>();
        for (int i = KEY_PREFIX.length(); i < arg.length(); i++) {
            result.add(arg.charAt(i));
        }
        return result;
    }

    @Override
    public <T extends CliCommand> T bind(
        final CliCommandInfo info,
        final String[] args
    ) throws CommandFabricationException {
        T command = info.instance();

        final Iterator<String> iterator = Arrays.asList(args).iterator();
        while (iterator.hasNext()) {
            String arg = iterator.next();
            if (isOptionName(arg)) {
                final String name = extractOptionName(arg);
                info.bind(command, new Argument(name, iterator));
            } else if (isOptionKey(arg)) {
                final Collection<Character> keys = extractOptionKeys(arg);
                for (Character key : keys) {
                    info.bind(command, new Argument(key, iterator));
                }
            }
        }

        return command;
    }


}