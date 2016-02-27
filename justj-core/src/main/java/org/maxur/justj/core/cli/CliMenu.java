package org.maxur.justj.core.cli;

import java.util.*;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>23.02.2016</pre>
 */
public class CliMenu {

    private final Map<String, CliCommandInfo> commandsByName = new HashMap<>();

    private final Map<Character, CliCommandInfo> commandsByKey = new HashMap<>();

    private CliCommandInfo defaultCommand = null;

    private final CLiMenuStrategy strategy;

    public CliMenu(final CLiMenuStrategy strategy) {
        this.strategy = strategy;
    }

    @SafeVarargs
    public final void register(final Class<CliCommand>... list) {
        for (Class<CliCommand> c : list) {
            final CliCommandInfo info = new CliCommandInfo(c);
            if (info.isDefault()) {
                defaultCommand = info;
            }
            commandsByName.put(info.name(), info);
            info.keys().forEach(key -> commandsByKey.put(key, info));
        }
    }

    public <T extends CliCommand> T makeCommand(final String name) throws CommandFabricationException {
        final CliCommandInfo info = commandsByName.get(name);
        if (info == null) {
            throw new CommandNotFoundException(name);
        }
        return info.instance();
    }

    @SuppressWarnings("unchecked")
    public <T extends CliCommand> T makeCommand(final String[] args) throws CommandFabricationException {
        CliCommandInfo command;
        final Collection<CliCommandInfo> commands = selectCommands(args);
        switch (commands.size()) {
            case 0:
                if (defaultCommand == null) {
                    return null;
                }
                command = defaultCommand;
                break;
            case 1:
                command = commands.iterator().next();
                break;
            default:
                throw moreThanOneCommandException(args, commands);
        }
        return strategy.bind(command, args);
    }

    private Set<CliCommandInfo> selectCommands(final String[] args) throws CommandFabricationException {
        final Set<CliCommandInfo> result = new HashSet<>();

        final ArgumentCursor cursor = ArgumentCursor.cursor(args);
        while (cursor.hasNext()) {
            final Argument argument = cursor.nextOption();
            if (argument.isKey()) {
                final CliCommandInfo command = commandsByKey.get(argument.key());
                if (command != null) {
                    result.add(command);
                }
            } else {
                final CliCommandInfo command = commandsByName.get(argument.name());
                if (command != null) {
                    result.add(command);
                }
            }
        }
        return result;
    }

    private InvalidCommandLineException moreThanOneCommandException(String[] args, Collection<CliCommandInfo> commands) {
        final Iterator<CliCommandInfo> iterator = commands.iterator();
        String result = "'" + iterator.next().name() + "'";
        while (iterator.hasNext()) {
            CliCommandInfo command = iterator.next();
            String separator = iterator.hasNext() ? ", " : " and ";
            result += separator + "'" + command.name() + "'";
        }
        return new InvalidCommandLineException(
            Arrays.toString(args),
            format("You try to call commands %s simultaneously",  result)
        );
    }


}
