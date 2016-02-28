package org.maxur.justj.core.cli

import org.maxur.justj.core.cli.annotation.Command
import org.maxur.justj.core.cli.annotation.Key
import spock.lang.Specification

import static org.maxur.justj.core.cli.info.CliCommandInfo.commandInfo

class CliCommandSpec extends Specification {

    def "Should returns name for class with @Command annotation"() {
        given: "new Command with @Command annotation"
        def command = commandInfo(VersionCommand);
        when: "Client requests command name"
        def name = command.name()
        then: "Command returns name"
        assert name == "version";
    }

    def "Should returns name for class without @Command annotation but with Command postfix in name"() {
        given: "new Command without @Command annotation but with Command postfix in name"
        def command = commandInfo(HelpCommand);
        when: "Client requests command name"
        def name = command.name()
        then: "Command returns name"
        assert name == "help";

    }

    def "Should returns name as class name for class without @Command annotation and without Command postfix in name"() {
        given: "new Command without @Command annotation and without Command postfix in name"
        def command = commandInfo(Validate);
        when: "Client requests command name"
        def name = command.name()
        then: "Command returns null"
        assert name == "validate";
    }

    def "Should returns shortKey for class with @ShortKey annotation"() {
        given: "new Command with @ShortKey annotation"
        def command = commandInfo(HelpCommand);
        when: "Client requests key"
        def shortKey = command.keys()
        then: "Command returns key"
        assert shortKey[0] == "h" as char;
    }

    def "Should returns shortKey for class without @ShortKey annotation but with Command name"() {
        given: "new Command without @Command annotation but with Command postfix in name"
        def command = commandInfo(VersionCommand);
        when: "Client requests key"
        def shortKey = command.keys()
        then: "Command returns key"
        assert shortKey[0] == "v" as char;
    }

    def "Should returns first letter from class name as key for class without @ShortKey annotation and without Command name"() {
        given: "new Command without @Command annotation but with Command postfix in name"
        def command = commandInfo(Validate);
        when: "Client requests key"
        def shortKey = command.keys()
        then: "Command returns key"
        assert shortKey[0] == "v" as char;

    }

    @Command("version")
    static class VersionCommand {
    }

    @Key("h")
    @Command
    static class HelpCommand {
    }

    @Command
    static class Validate {
    }



}
