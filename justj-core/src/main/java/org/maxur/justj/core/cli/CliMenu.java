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

    public void register(final Class<CliCommand>... list) {
        for (Class<CliCommand> c : list) {
            final CliCommandInfo info = new CliCommandInfo(c);
            if (info.isDefault()) {
                defaultCommand = info;
            }
            commandsByName.put(info.name(), info);
            commandsByKey.put(info.key(), info);
        }
    }

    public <T extends CliCommand> T makeCommand(final String name) throws CommandFabricationException {
        final CliCommandInfo info = commandsByName.get(name);
        if (info == null) {
            throw new CommandNotFoundException(name);
        }
        return info.copy();
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
        return strategy.bind(command, args, command.copy());
    }

    private Set<CliCommandInfo> selectCommands(final String[] args) throws CommandFabricationException {
        final Set<CliCommandInfo> result = new HashSet<>();
        for (String arg : args) {
            result.addAll(selectCommand(arg));
        }
        return result;
    }

    private Set<CliCommandInfo> selectCommand(final String arg) throws CommandFabricationException {
        if (strategy.isOptionName(arg)) {
            final CliCommandInfo command = getCommandByName(arg);
            if (command != null) {
                return Collections.singleton(command);
            }
        } else if (strategy.isOptionKey(arg)) {
            return getCommandsByKey(arg);
        }
        return Collections.emptySet();
    }


    private CliCommandInfo getCommandByName(final String arg) throws CommandFabricationException {
        final String name = strategy.extractOptionName(arg);
        return commandsByName.get(name);
    }

    private Set<CliCommandInfo> getCommandsByKey(final String arg) throws CommandFabricationException {
        final Set<CliCommandInfo> result = new HashSet<>();
        final Collection<Character> keys = strategy.extractOptionKeys(arg);
        for (Character key : keys) {
            final CliCommandInfo command = commandsByKey.get(key);
            if (command != null) {
                result.add(command);
            }
        }
        return result;
    }


    private InvalidCommandLineException moreThanOneCommandException(String[] args, Collection<CliCommandInfo> commands) {
        String result = "";
        for (Iterator<CliCommandInfo> iterator = commands.iterator(); iterator.hasNext(); ) {
            CliCommandInfo command = iterator.next();
            String separator = iterator.hasNext() ? ", " : " and ";
            result += separator + "'" + command.name() + "'";
        }
        return new InvalidCommandLineException(
            Arrays.toString(args),
            format("You try to call commands '%s' simultaneously",  result)
        );
    }


}
