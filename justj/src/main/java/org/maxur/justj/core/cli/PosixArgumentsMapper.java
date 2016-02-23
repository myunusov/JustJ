package org.maxur.justj.core.cli;

/**
 * see http://pubs.opengroup.org/onlinepubs/9699919799/
 * <p>
 * utility_name[-a][-b][-c option_argument][-d|-e][-f[option_argument]][operand...]
 * <p>
 * The utility in the example is named utility_name.
 * It is followed by options, option-arguments, and operands.
 * The arguments that consist of <hyphen> characters and single letters or digits, such as 'a', are known as "options''
 * (or, historically, "flags").
 * Certain options are followed by an "option-argument",
 * as shown with [ -c option_argument].
 * <p>
 * The arguments following the last options and option-arguments are named "operands".
 * <p>
 * Option-arguments are shown separated from their options by <blank> characters
 * <p>
 * Frequently, names of parameters that require substitution by actual values are shown with embedded
 * <underscore> characters.
 * Alternatively, parameters are shown as follows: <parameter name>
 * <p>
 * Utilities with many flags generally show all of the individual flags
 * utility_name [-abcDxyz][-p arg][operand]
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>21.02.2016</pre>
 */
public class PosixArgumentsMapper<O> implements ArgumentsMapper<O> {

    private final Class<O> argumentsClass;

    public PosixArgumentsMapper(Class<O> argumentsClass) {
        this.argumentsClass = argumentsClass;
    }

    @Override
    public O  readValue(String[] args) throws OptionsProcessingException {
        final ArgumentBuilder<O> argumentBuilder = new ArgumentBuilder<>();
        return argumentBuilder
                .withClass(argumentsClass)
                .withArgs(args)
                .build();
    }


}
