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
        sut = new CliMenu(new CLiMenuPosixStrategy())
    }

    def "Should registers new command and returns it by name in annotation"() {
        when: "Client registers the command in the menu"
        sut.register(VersionCommand)
        then: "Menu returns command by it's name"
        assert sut.makeCommand("version") instanceof VersionCommand;
    }

    def "Should registers new command and returns it by it's class name if annotation is not present"() {
        when: "Client registers the command in the menu"
        sut.register(HelpCommand)
        then: "Menu returns command by it's name"
        assert sut.makeCommand("help") instanceof HelpCommand;
    }

    def "Should returns error if command is not register"() {
        when: "Client not registers the command in the menu"
        and: "try get command from menu"
        sut.makeCommand("help")
        then: "Menu throws Command not Found Exception"
        thrown CommandNotFoundException;
    }

    def "Should returns command if command line contains command flag"() {
        given: "command line with commands flag"
        String[] args = ["--help"]
        when: "Client registers the command in the menu"
        sut.register(HelpCommand)
        then: "Menu returns command by command line flag"
        assert sut.makeCommand(args) instanceof HelpCommand;
    }

    def "Should returns null if command line is not contains any command flag"() {
        given: "command line without any commands flag"
        String[] args = []
        when: "Client registers the command in the menu"
        sut.register(HelpCommand)
        then: "Menu returns null"
        assert sut.makeCommand(args) == null;
    }

    def "Should returns error if command line contains two and more commands names"() {
        given: "command line with two commands flag"
        String[] args = ["--help", "--version"]
        when: "Client registers the command in the menu as default"
        sut.register(HelpCommand, VersionCommand)
        and: "try get command from menu"
        sut.makeCommand(args)
        then: "Menu throws Invalid Command Line"
        thrown InvalidCommandLineException;
    }

    def "Should returns command on valid flag only "() {
        given: "command line with commands flag and a operand"
        String[] args = ["--help", "++version"]
        when: "Client registers the command in the menu as default"
        sut.register(HelpCommand, VersionCommand)
        and: "try get command from menu"
        def result = sut.makeCommand(args)
        then: "Menu returns command by command line flag"
        assert result instanceof HelpCommand;
    }

    def "Should returns command if command line contains command and commands option "() {
        given: "command line with any commands flag and option flag"
        String[] args = ["--help", "--all"]
        when: "Client registers the command in the menu as default"
        sut.register(HelpCommand)
        and: "try get command from menu"
        HelpCommand result = sut.makeCommand(args)
        then: "Menu returns command by command line flag"
        assert result instanceof HelpCommand;
        and: "Flag is set by annotations value"
        assert result.all
    }

    def "Should returns command if command line contains command and commands option by method name"() {
        given: "command line with any commands flag and option flag"
        String[] args = ["--version", "--all"]
        when: "Client registers the command in the menu as default"
        sut.register(VersionCommand)
        and: "try get command from menu"
        VersionCommand result = sut.makeCommand(args)
        then: "Menu returns command by command line flag"
        assert result instanceof VersionCommand;
        and: "Flag is set by field name"
        assert result.all
    }

    def "Should returns error if command line contains unknown commands option"() {
        given: "command line without any commands flag and invalid option flag"
        String[] args = ["--help", "--invalid"]
        when: "Client registers the command in the menu as default"
        sut.register(HelpCommand)
        and: "try get command from menu"
        sut.makeCommand(args)
        then: "Menu throws Command not Found Exception"
        thrown InvalidCommandArgumentException;
    }

    def "Should returns default commands if default command is registered and command line is empty"() {
        given: "command line without any commands flag"
        String[] args = []
        when: "Client registers the command in the menu"
        sut.register(ProcessCommand)
        then: "Menu returns default command by command line flag"
        assert sut.makeCommand(args) instanceof ProcessCommand;
    }

    def "Should returns command if command line contains command and commands option without flag annotation"() {
        given: "command line with any commands flag and option flag"
        String[] args = ["--process", "--all"]
        when: "Client registers the command in the menu as default"
        sut.register(ProcessCommand)
        and: "try get command from menu"
        ProcessCommand result = sut.makeCommand(args)
        then: "Menu returns command by command line flag"
        assert result instanceof ProcessCommand;
        and: "Flag is set by field name"
        assert result.all
    }

    def "Should returns command if command line contains short command flag without shortkey annotation"() {
        given: "command line with commands flag"
        String[] args = ["-v"]
        when: "Client registers the command in the menu"
        sut.register(VersionCommand)
        then: "Menu returns command by command line flag"
        assert sut.makeCommand(args) instanceof VersionCommand;
    }

    def "Should returns command if command line contains short command flag"() {
        given: "command line with commands flag"
        String[] args = ["-?"]
        when: "Client registers the command in the menu"
        sut.register(HelpCommand)
        then: "Menu returns command by command line flag"
        assert sut.makeCommand(args) instanceof HelpCommand;
    }

    def "Should returns command if command line contains command and commands option by shortkey"() {
        given: "command line with any commands flag and option flag"
        String[] args = ["-v", "-a"]
        when: "Client registers the command in the menu as default"
        sut.register(VersionCommand)
        and: "try get command from menu"
        VersionCommand result = sut.makeCommand(args)
        then: "Menu returns command by command line flag"
        assert sut.makeCommand(args) instanceof VersionCommand;
        and: "Flag is set by field name"
        assert result.all
    }

    def "Should returns command if command line contains command and commands option by shortkey from superclass"() {
        given: "command line with any commands flag and option flag"
        String[] args = ["-q", "-v", "-a"]
        when: "Client registers the command in the menu as default"
        sut.register(VersionCommand)
        and: "try get command from menu"
        VersionCommand result = sut.makeCommand(args)
        then: "Menu returns command by command line flag"
        assert result instanceof VersionCommand;
        and: "Flag is set by field name"
        assert result.quit
    }

    def "Should returns command if command line contains command and commands option in compact form"() {
        given: "command line with any commands flag and option flag"
        String[] args = ["-va"]
        when: "Client registers the command in the menu as default"
        sut.register(VersionCommand)
        and: "try get command from menu"
        VersionCommand result = sut.makeCommand(args)
        then: "Menu returns command by command line flag"
        assert result instanceof VersionCommand;
        and: "Flag is set by field name"
        assert result.all
    }

    def "Should returns error if command line contains two and more commands names in compact form"() {
        given: "command line with two commands flag"
        String[] args = ["-?v"]
        when: "Client registers the command in the menu as default"
        sut.register(HelpCommand, VersionCommand)
        and: "try get command from menu"
        sut.makeCommand(args)
        then: "Menu throws Invalid Command Line"
        thrown InvalidCommandLineException;
    }

    def "Should returns command if command line contains command and commands option as trigger (compact form)"() {
        given: "command line with any commands flag and option flag"
        String[] args = ["-px"]
        when: "Client registers the command in the menu as default"
        sut.register(ProcessCommand)
        and: "try get command from menu"
        ProcessCommand result = sut.makeCommand(args)
        then: "Menu returns command by command line flag"
        assert result instanceof ProcessCommand;
        and: "Flag is set by field name"
        assert result.logLevel == LogLevel.DEBUG
    }

    def "Should returns command if command line contains command and commands option as trigger"() {
        given: "command line with any commands flag and option flag"
        String[] args = ["-p", "--debug"]
        when: "Client registers the command in the menu as default"
        sut.register(ProcessCommand)
        and: "try get command from menu"
        ProcessCommand result = sut.makeCommand(args)
        then: "Menu returns command by command line flag"
        assert result instanceof ProcessCommand;
        and: "Flag is set by field name"
        assert result.logLevel == LogLevel.DEBUG
    }

    def "Should returns command if command line contains command and commands option as trigger with flag"() {
        given: "command line with any commands flag and option flag"
        String[] args = ["-p", "--quiet"]
        when: "Client registers the command in the menu as default"
        sut.register(ProcessCommand)
        and: "try get command from menu"
        ProcessCommand result = sut.makeCommand(args)
        then: "Menu returns command by command line flag"
        assert result instanceof ProcessCommand;
        and: "Flag is set by field name"
        assert result.logLevel == LogLevel.OFF
    }

    def "Should returns command if command line contains short command flag with alias"() {
        given: "command line with commands flag"
        String[] args = ["-h"]
        when: "Client registers the command in the menu"
        sut.register(HelpCommand)
        then: "Menu returns command by command line flag"
        assert sut.makeCommand(args) instanceof HelpCommand;
    }

    def "Should returns command if command line contains options name with options argument"() {
        given: "command line with commands flag"
        String[] args = ["--process", "--settings", "~/settings.xml"]
        when: "Client registers the command in the menu"
        sut.register(ProcessCommand)
        then: "Menu returns command by command line flag"
        assert sut.makeCommand(args) instanceof ProcessCommand;
    }

    public static enum LogLevel {
        @Key("x")
                DEBUG,
        @Key("q")
        @Flag("quiet")
                OFF,
        INFO
    }

    static abstract class TestCommand implements CliCommand {
        boolean quit
    }

    @Command("version")
    static class VersionCommand extends TestCommand {
        @Flag
        boolean all
    }

    @Command
    @Default
    static class ProcessCommand extends TestCommand {
        boolean all
        LogLevel logLevel
        String settings
    }

    def "Should returns error if command line contains unknown commands option in compact form"() {
        given: "command line without any commands flag and invalid option flag"
        String[] args = ["-?i"]
        when: "Client registers the command in the menu as default"
        sut.register(HelpCommand)
        and: "try get command from menu"
        sut.makeCommand(args)
        then: "Menu throws Command not Found Exception"
        thrown InvalidCommandArgumentException;
    }

}
