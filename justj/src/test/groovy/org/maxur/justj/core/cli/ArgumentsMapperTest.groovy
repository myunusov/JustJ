package org.maxur.justj.core.cli

import spock.lang.Specification

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
/**
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
class ArgumentsMapperTest extends Specification {

    PosixArgumentsMapper sut

    def "Should returns empty arguments on command without arguments"() {
        given: "command without arguments"
        sut = new PosixArgumentsMapper<ArgumentsOne>(ArgumentsOne.class)
        String[] var = [];
        when: "read arguments"
        def arguments = sut.readValue(var);
        then: "returns empty arguments"
        assert arguments != null
        assert arguments.operands.size() == 0;
    }

    def "Should returns one operand on command with one operand"() {
        given: "command with one operand"
        sut = new PosixArgumentsMapper<ArgumentsOne>(ArgumentsOne.class)
        String[] var = ["one"];
        when: "read arguments"
        def arguments = sut.readValue(var);
        then: "returns empty arguments"
        assert arguments != null
        assert arguments.operands.size() == 1;
        assert arguments.operands[0] == "one";
    }


    def "Should returns few operands  on command with few operands"() {
        given: "command with few operands ('one','two','three')"
        sut = new PosixArgumentsMapper<ArgumentsOne>(ArgumentsOne.class)
        String[] var = ["one", "two", "three"];
        when: "read arguments"
        def arguments = sut.readValue(var);
        then: "returns three operands"
        assert arguments.operands.size() == 3;
        and: "first operand is 'one'"
        assert arguments.operands[0] == "one"
        and: "second operand is 'two'"
        assert arguments.operands[1] == "two"
        and: "third operand is 'three'"
        assert arguments.operands[2] == "three"
    }

    def "Should returns one flag on command with one flag '-'"() {
        given: "command with one flag"
        sut = new PosixArgumentsMapper<ArgumentsOne>(ArgumentsOne.class)
        String[] var = ["-v"];
        when: "read arguments"
        def arguments = sut.readValue(var);
        then: "set flag"
        assert arguments.version;
        and: "don't set any operands"
        assert arguments.operands.size() == 0;
    }

    def "Should returns one flag on command with one flag '--'"() {
        given: "command with one flag"
        sut = new PosixArgumentsMapper<ArgumentsOne>(ArgumentsOne.class)
        String[] var = ["--all"];
        when: "read arguments"
        def arguments = sut.readValue(var);
        then: "set flag"
        assert arguments.all;
        and: "don't set any operands"
        assert arguments.operands.size() == 0;
    }

    def "Should returns error on invalid command option"() {
        given: "command with one invalid option"
        sut = new PosixArgumentsMapper<ArgumentsOne>(ArgumentsOne.class)
        String[] var = ["-e"];
        when: "read arguments"
        sut.readValue(var);
        then: "throws exception"
        thrown OptionsProcessingException
    }

    def "Should returns one flag and one operand if command has one flag and one operand"() {
        given: "command with one option and one operand"
        sut = new PosixArgumentsMapper<ArgumentsOne>(ArgumentsOne.class)
        String[] var = ["-v", "one"];
        when: "read arguments"
        def arguments = sut.readValue(var);
        then: "set flag"
        assert arguments.version;
        and: "set one operand"
        assert arguments.operands.size() == 1;
        and: "operand is 'one'"
        assert arguments.operands[0] == "one";
    }

    def "Should returns one option with argument if commandline contains one option with argument"() {
        given: "command with one option with argument"
        sut = new PosixArgumentsMapper<ArgumentsOne>(ArgumentsOne.class)
        String[] var = ["-i", "one"];
        when: "read arguments"
        def arguments = sut.readValue(var);
        then: "set option"
        assert arguments.inputFile == "one";
        and: "don't set any operands"
        assert arguments.operands.size() == 0;
    }

    def "Should returns one option with number argument if commandline contains one option with argument"() {
        given: "command with one option with number"
        sut = new PosixArgumentsMapper<ArgumentsOne>(ArgumentsOne.class)
        String[] var = ["-n", "1"];
        when: "read arguments"
        def arguments = sut.readValue(var);
        then: "set option"
        assert arguments.number == 1;
        and: "don't set any operands"
        assert arguments.operands.size() == 0;
    }

    def "Should returns one option with none string argument if commandline contains one option with argument"() {
        given: "command with one option and not string argument"
        sut = new PosixArgumentsMapper<ArgumentsOne>(ArgumentsOne.class)
        String[] var = ["-g", "MAN"];
        when: "read arguments"
        def arguments = sut.readValue(var);
        then: "set option"
        assert arguments.gender == Gender.MAN;
        and: "don't set any operands"
        assert arguments.operands.size() == 0;
    }

    def "Should returns error on command option without mandatary arguments"() {
        given: "command with one option without mandatary arguments"
        sut = new PosixArgumentsMapper<ArgumentsOne>(ArgumentsOne.class)
        String[] var = ["-i"];
        when: "read arguments"
        sut.readValue(var);
        then: "throws exception"
        thrown OptionsProcessingException
    }

    def "Should returns flags if commandline contains flags"() {
        given: "command with lot of flags "
        sut = new PosixArgumentsMapper<ArgumentsOne>(ArgumentsOne.class)
        String[] var = ["-v", "-a"];
        when: "read arguments"
        def arguments = sut.readValue(var);
        then: "set flags"
        assert arguments.all;
        assert arguments.version;
        and: "don't set any operands"
        assert arguments.operands.size() == 0;
    }

    def "Should returns flags if commandline contains flags as one argument"() {
        given: "command with lot of flags as one argument"
        sut = new PosixArgumentsMapper<ArgumentsOne>(ArgumentsOne.class)
        String[] var = ["-va"];
        when: "read arguments"
        def arguments = sut.readValue(var);
        then: "set flags"
        assert arguments.all;
        assert arguments.version;
        and: "don't set any operands"
        assert arguments.operands.size() == 0;
    }

    def "Should returns flags if commandline contains flags with operand between"() {
        given: "command with lot of flags as one argument"
        sut = new PosixArgumentsMapper<ArgumentsOne>(ArgumentsOne.class)
        String[] var = ["-v", "one", "-a"];
        when: "read arguments"
        def arguments = sut.readValue(var);
        then: "set flags"
        assert arguments.all;
        assert arguments.version;
        and: "set one operand"
        assert arguments.operands.size() == 1;
        and: "operand is 'one'"
        assert arguments.operands[0] == "one";
    }


    static class ArgumentsOne {

        @Operands
        private final List<String> operands = new ArrayList<>();

        @Option("a")
        private boolean all;

        @Option("v")
        private boolean version;

        @Option("i")
        private String inputFile;

        @Option("n")
        private Integer number;

        @Option("g")
        private Gender gender;

    }

    def "Should returns error if required options is absent"() {
        given: "command without required options"
        sut = new PosixArgumentsMapper<ArgumentsTwo>(ArgumentsTwo.class)
        String[] var = ["-i", "one"];
        when: "read arguments"
        sut.readValue(var);
        then: "throws exception"
        thrown OptionsProcessingException
    }

    def "Should returns error if options argument is not valid"() {
        given: "command with invalid options argument"
        sut = new PosixArgumentsMapper<ArgumentsTwo>(ArgumentsTwo.class)
        String[] var = ["-i", "one", "-o", "two", "-n", "1"];
        when: "read arguments"
        sut.readValue(var);
        then: "throws exception"
        thrown OptionsProcessingException
    }

    static class ArgumentsTwo {
        @NotNull
        @Option("i")
        private String inputFile;
        @NotNull
        @Option("o")
        private String outputFile;
        @Min(10l)
        @Option("n")
        private Integer number;
    }

    def "Should returns error if command contains operand but arguments is not"() {
        given: "command with operand"
        sut = new PosixArgumentsMapper<ArgumentsThree>(ArgumentsThree.class)
        String[] var = ["one"];
        when: "read arguments"
        sut.readValue(var);
        then: "throws exception"
        thrown OptionsProcessingException
    }

    static class ArgumentsThree {
    }

    def "Should returns few operands on command with few none string operands"() {
        given: "command with few operands ('MAN','WOMAN')"
        sut = new PosixArgumentsMapper<ArgumentsFour>(ArgumentsFour.class)
        String[] var = ["MAN", "WOMAN"];
        when: "read arguments"
        def arguments = sut.readValue(var);
        then: "returns three operands"
        assert arguments.genders.size() == 2;
        and: "first operand is 'MAN'"
        assert arguments.genders[0] == Gender.MAN
        and: "second operand is 'WOMAN'"
        assert arguments.genders[1] == Gender.WOMAN
    }

    static class ArgumentsFour {

        @Operands
        private final List<Gender> genders = new ArrayList<>();

    }

    enum Gender {
        MAN, WOMAN
    }


}
