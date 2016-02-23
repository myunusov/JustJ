package org.maxur.justj.core.cli

import spock.lang.Specification
/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
class CliMenuSpec extends Specification {

    private CliMenu sut

    void setup() {
        sut = new CliMenu()
    }

    def "Should registers new command and returns it by name in annotation"() {
        given: "new Command with @Command annotation"
        def command = new VersionCommand();
        when: "Client registers the command in the menu"
        sut.register(command)
        then: "Menu returns command by it's name"
        assert sut.makeCommand("version") == command;
        and: "Menu returnd copy of new command"
        assert !sut.makeCommand("version").is(command);
    }

    def "Should registers new command and returns it by it's class name if annotation is not present"() {
        given: "new Command without @Command annotation"
        def command = new HelpCommand();
        when: "Client registers the command in the menu"
        sut.register(command)
        then: "Menu returns command by it's name"
        assert sut.makeCommand("help") == command;
        and: "Menu returnd copy of new command"
        assert !sut.makeCommand("help").is(command);
    }

    def "Should returns error if command is not register"() {
        when: "Client not registers the command in the menu"
        and: "try get command from menu"
        sut.makeCommand("help")
        then: "Menu throws Command not Found Exception"
        thrown CommandNotFoundException;
    }

    def "Should returns command if command line contains command flag"() {
        given: "new Command"
        def command = new HelpCommand();
        and: "command line with commands flag"
        String[] args = ["--help"]
        when: "Client registers the command in the menu"
        sut.register(command)
        then: "Menu returns command by command line flag"
        assert sut.makeCommand(args) == command;
    }

    def "Should returns null if command line is not contains any command flag"() {
        given: "new Command"
        def command = new HelpCommand();
        and: "command line without any commands flag"
        String[] args = []
        when: "Client registers the command in the menu"
        sut.register(command)
        then: "Menu returns command by command line flag"
        assert sut.makeCommand(args) == null;
    }

    def "Should returns error if command line contains unknown commands name"() {
        given: "new Command"
        def command = new HelpCommand();
        and: "command line without any commands flag"
        String[] args = ["--all"]
        when: "Client registers the command in the menu as default"
        sut.register(command)
        and: "try get command from menu"
        sut.makeCommand(args)
        then: "Menu throws Command not Found Exception"
        thrown CommandNotFoundException;
    }

    def "Should returns error if command line contains two and more commands names"() {
        given: "new Commands"
        def command1 = new HelpCommand();
        def command2 = new VersionCommand();
        and: "command line without any commands flag"
        String[] args = ["--help", "--version"]
        when: "Client registers the command in the menu as default"
        sut.register(command1, command2)
        and: "try get command from menu"
        sut.makeCommand(args)
        then: "Menu throws Invalid Command Line"
        thrown InvalidCommandLineException;
    }

    @Command("version")
    class VersionCommand extends CliCommand {

        @Override
        VersionCommand copy() {
            return new VersionCommand();
        }
    }

    class HelpCommand extends CliCommand {

        @Override
        HelpCommand copy() {
            return new HelpCommand();
        }
    }

}
