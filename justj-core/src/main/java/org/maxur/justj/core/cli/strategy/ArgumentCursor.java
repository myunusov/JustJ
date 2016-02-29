package org.maxur.justj.core.cli.strategy;

import org.maxur.justj.core.cli.argument.Argument;
import org.maxur.justj.core.cli.info.OptionType;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>27.02.2016</pre>
 */
public class ArgumentCursor {

    private static final Character[] TOKEN_SYMBOLS = {'_', '?'};

    private static final String NAME_PREFIX = "--";

    private static final String KEY_PREFIX = "-";

    private static final String TEXT_SYMBOL = "\"";

    private static final String DELIMITER = " ";

    private final OptionDetector detector;

    private final String expression;

    private int pos = 0;

    private State state = State.INIT;

    private ArgumentCursor(final String expression, final OptionDetector detector) {
        this.expression = expression;
        this.detector = detector;
    }

    public static ArgumentCursor cursor(final String[] items, final OptionDetector detector) {
        final String expression = Arrays.stream(items).collect(Collectors.joining(DELIMITER));
        return new ArgumentCursor(expression, detector);
    }

    public boolean hasNext() {
        return pos < expression.length();
    }

    public Argument next() {
        final Argument result;
        switch (state) {
            case INIT:
                skipSpace();
                result = argument();
                populate(result);
                break;
            case OPERATOR:
                skipSpace();
                result = new Argument(readKey());
                populate(result);
                break;
            default:
                throw new IllegalStateException("Unreachable statement");
        }
        return result;
    }

    private Argument argument() {
        if (startWith(NAME_PREFIX)) {
            skip(NAME_PREFIX);
            return new Argument(readName());
        } else if (startWith(KEY_PREFIX)) {
            skip(KEY_PREFIX);
            return new Argument(readKey());
        } else {
            final Argument result = new Argument();
            result.setOptionArgument(operand());
            return result;
        }
    }

    private void populate(final Argument argument) {
        if (detector.findInfoBy(argument, OptionType.OPTION)) {
            argument.setOptionArgument(optionArgument());
        } else if (detector.findInfoBy(argument, OptionType.LIST)) {
            argument.setOptionArgument(optionArgument());
        }
    }


    private String optionArgument() {
        skipSpace();
        if (startWith(TEXT_SYMBOL)) {
            return readText();
        } else {
            return readWord();
        }
    }

    private String operand() {
        return readOperand();
    }

    private boolean startWith(final String s) {
        skipSpace();
        return expression.startsWith(s, pos);
    }

    private void skip(final String s) {
        skipSpace();
        if (startWith(s)) {
            pos += s.length();
        } else {
            throw new IllegalArgumentException(format(
                    "Invalid expression '%s'. Must be '%s' in '%d' position"
                    , expression, s, pos
            )
            );
        }
        skipSpace();
    }

    private void skipSpace() {
        while (hasNext() && expression.charAt(pos) == ' ') {
            pos++;
        }
    }

    private Character readKey() {
        state = State.OPERATOR;
        if (isTokenSymbol()) {
            if (hasNext()) {
                return expression.charAt(pos++);
            }
        } else {
            state = State.INIT;
        }
        return null;
    }

    private String readName() {
        if (isTokenSymbol()) {
            return readToken();
        }
        return null;
    }

    private String readWord() {
        skipSpace();
        String word = "";
        while (!startWith(DELIMITER) && hasNext()) {
            word += Character.toString(expression.charAt(pos++));
        }
        return word;
    }

    private String readOperand() {
        String operand = "";
        while (!startWith(KEY_PREFIX) && hasNext()) {
            operand += Character.toString(expression.charAt(pos++));
        }
        return operand;
    }

    private String readText() {
        skip(TEXT_SYMBOL);
        String text = "";
        while (!expression.startsWith(TEXT_SYMBOL, pos)) {
            text += Character.toString(expression.charAt(pos++));
            if (!hasNext()) {
                throw new IllegalArgumentException(format(
                        "Invalid expression '%s'. Must be '%s' in '%d' position"
                        , expression, "\"", pos
                ));
            }
        }
        skip(TEXT_SYMBOL);
        return text;
    }

    private String readToken() {
        String token = "";
        while (hasNext() && isTokenSymbol()) {
            token += Character.toString(expression.charAt(pos++));
        }
        return token;
    }

    private boolean isTokenSymbol() {
        final Character character = expression.charAt(pos);
        return Character.isLetterOrDigit(expression.charAt(pos)) ||
                Arrays.asList(TOKEN_SYMBOLS).contains(character);
    }

    private enum State {
        INIT,
        OPERATOR
    }
}
