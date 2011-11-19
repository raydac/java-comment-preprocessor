package com.igormaznitsa.jcpreprocessor.expression;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.functions.AbstractFunction;
import com.igormaznitsa.jcpreprocessor.expression.functions.FunctionDefinedByUser;
import com.igormaznitsa.jcpreprocessor.expression.operators.AbstractOperator;
import com.igormaznitsa.jcpreprocessor.expression.operators.OperatorSUB;
import com.igormaznitsa.jcpreprocessor.extension.PreprocessorExtension;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class ExpressionParser {

    private enum ParserState {

        WAIT,
        NUMBER,
        HEX_NUMBER,
        FLOAT_NUMBER,
        STRING,
        SPECIAL_CHAR,
        VALUE_OR_FUNCTION,
        OPERATOR
    }

    public enum SpecialItem implements ExpressionStackItem {

        BRACKET_OPENING('('),
        BRACKET_CLOSING(')'),
        COMMA(',');
        private final char chr;

        private SpecialItem(final char chr) {
            this.chr = chr;
        }

        public ExpressionStackItemPriority getPriority() {
            return null;
        }

        public ExpressionStackItemType getStackItemType() {
            return null;
        }
    }
    private static final ExpressionParser INSTANCE = new ExpressionParser();
    private static final OperatorSUB OPERATOR_SUB = AbstractOperator.findForClass(OperatorSUB.class);

    public static ExpressionParser getInstance() {
        return INSTANCE;
    }

    public ExpressionTree parse(final String str, final PreprocessorContext context) throws IOException {
        final PushbackReader reader = new PushbackReader(new StringReader(str));
        final ExpressionTree result = new ExpressionTree();
        if (readExpression(reader, result, context, false, false) != null) {
            throw new IllegalStateException("Wrong result during expression parsing");
        }

        result.postProcess();

        return result;
    }

    public ExpressionStackItem readExpression(final PushbackReader reader, final ExpressionTree tree, final PreprocessorContext context, final boolean insideBracket, final boolean argument) throws IOException {
        boolean working = true;

        ExpressionStackItem result = null;

        while (working) {
            final ExpressionStackItem nextItem = nextItem(reader, context);
            if (nextItem == null) {
                working = false;
                result = null;
            } else {

                if (nextItem.getStackItemType() == null) {
                    if (nextItem == SpecialItem.BRACKET_CLOSING) {
                        if (insideBracket) {
                            working = false;
                            result = nextItem;
                        } else {
                            if (argument) {
                                working = false;
                                result = nextItem;
                            } else {
                                throw new IllegalStateException("Closing bracket without any opening one detected");
                            }
                        }
                    } else if (nextItem == SpecialItem.BRACKET_OPENING) {
                        final ExpressionTree subExpression = new ExpressionTree();
                        if (SpecialItem.BRACKET_CLOSING != readExpression(reader, subExpression, context, true, false)) {
                            throw new IllegalStateException("Unclosed bracket detected");
                        }
                        tree.addTree(subExpression);
                    } else if (nextItem == SpecialItem.COMMA) {
                        return nextItem;
                    }
                } else {
                    if (nextItem.getStackItemType() == ExpressionStackItemType.FUNCTION) {
                        final AbstractFunction function = (AbstractFunction) nextItem;

                        final ExpressionStackItem expectedBracket = nextItem(reader, context);
                        if (nextItem == null) {
                            throw new IllegalStateException("A function without parameters detected [" + function.getName() + ']');
                        }

                        final int arity = function.getArity();

                        ExpressionTree functionTree = null;
                        
                        if (arity == 0) {
                            final ExpressionTree subExpression = new ExpressionTree();
                            final ExpressionStackItem lastItem = readFunctionArguments(reader, subExpression, context);
                            if (SpecialItem.BRACKET_CLOSING != lastItem) {
                                throw new IllegalArgumentException("There is not closing bracket for function [" + function.getName() + ']');
                            } else if (subExpression.getRoot() != null){
                               throw new IllegalStateException("The function \'"+function.getName()+"\' doesn't need arguments");
                            } else {
                                functionTree = new ExpressionTree();
                                functionTree.addItem(function);
                            }
                        } else {

                            final List<ExpressionTree> arguments = new ArrayList<ExpressionTree>(arity);
                            for (int i = 0; i < function.getArity(); i++) {
                                final ExpressionTree subExpression = new ExpressionTree();
                                final ExpressionStackItem lastItem = readFunctionArguments(reader, subExpression, context);

                                if (SpecialItem.BRACKET_CLOSING == lastItem) {
                                    arguments.add(subExpression);
                                    break;
                                } else if (SpecialItem.COMMA == lastItem) {
                                    arguments.add(subExpression);
                                    continue;
                                } else {
                                    throw new IllegalArgumentException("Wrong argument definition for function detected [" + function.getName() + ']');
                                }
                            }

                            functionTree = new ExpressionTree();
                            functionTree.addItem(function);
                            ExpressionTreeElement functionTreeElement = functionTree.getRoot();

                            if (arguments.size() != functionTreeElement.getArity()) {
                                throw new IllegalArgumentException("Wrong argument number for function \'" + function.getName() + "\', it needs " + function.getArity() + " argument(s)");
                            }

                            functionTreeElement.fillArguments(arguments);
                        }
                        tree.addTree(functionTree);
                    } else {
                        tree.addItem(nextItem);
                    }
                }
            }
        }
        return result;
    }

    ExpressionStackItem readFunctionArguments(final PushbackReader reader, final ExpressionTree tree, final PreprocessorContext context) throws IOException {
        boolean working = true;
        ExpressionStackItem result = null;
        while (working) {
            final ExpressionStackItem nextItem = nextItem(reader, context);
            if (nextItem == null) {
                throw new IllegalStateException("Non-closed function detected");
            } else if (SpecialItem.COMMA == nextItem) {
                result = nextItem;
                working = false;
            } else if (SpecialItem.BRACKET_OPENING == nextItem) {
                final ExpressionTree subExpression = new ExpressionTree();
                if (SpecialItem.BRACKET_CLOSING != readExpression(reader, subExpression, context, true, false)) {
                    throw new IllegalStateException("Non-closed bracket inside a function argument detected");
                }
                tree.addTree(subExpression);
            } else if (SpecialItem.BRACKET_CLOSING == nextItem) {
                result = nextItem;
                working = false;
            } else {
                tree.addItem(nextItem);
            }
        }
        return result;
    }

    private static boolean isDelimiterOrOperatorChar(final char chr) {
        return isDelimiter(chr) || isOperatorChar(chr);
    }

    private static boolean isDelimiter(final char chr) {
        switch (chr) {
            case ',':
            case '(':
            case ')':
                return true;
            default:
                return false;
        }
    }

    private static boolean isOperatorChar(final char chr) {
        switch (chr) {
            case '-':
            case '+':
            case '%':
            case '*':
            case '/':
            case '&':
            case '|':
            case '!':
            case '^':
            case '=':
            case '<':
            case '>':
                return true;
            default:
                return false;
        }
    }

    ExpressionStackItem nextItem(final PushbackReader reader, final PreprocessorContext context) throws IOException {
        ParserState state = ParserState.WAIT;
        final StringBuilder builder = new StringBuilder(12);

        boolean found = false;

        while (!found) {
            final int data = reader.read();

            if (data < 0) {
                if (state != ParserState.WAIT) {
                    found = true;
                }
                break;
            }

            final char chr = (char) data;

            switch (state) {
                case WAIT: {
                    if (Character.isWhitespace(chr)) {
                        continue;
                    } else if (chr == ',') {
                        return SpecialItem.COMMA;
                    } else if (chr == '(') {
                        return SpecialItem.BRACKET_OPENING;
                    } else if (chr == ')') {
                        return SpecialItem.BRACKET_CLOSING;
                    } else if (Character.isDigit(chr)) {
                        builder.append(chr);
                        if (chr == '0') {
                            state = ParserState.HEX_NUMBER;
                        } else {
                            state = ParserState.NUMBER;
                        }
                    } else if (chr == '.') {
                        builder.append('.');
                        state = ParserState.FLOAT_NUMBER;
                    } else if (Character.isLetter(chr) || chr == '$' || chr == '_') {
                        builder.append(chr);
                        state = ParserState.VALUE_OR_FUNCTION;
                    } else if (chr == '\"') {
                        state = ParserState.STRING;
                    } else if (isOperatorChar(chr)) {
                        builder.append(chr);
                        state = ParserState.OPERATOR;
                    } else {
                        throw new IllegalArgumentException("Unsupported token character detected \'" + chr + '\'');
                    }
                }
                break;
                case OPERATOR: {
                    if (!isOperatorChar(chr) || isDelimiter(chr)) {
                        reader.unread(data);
                        found = true;
                    } else {
                        builder.append(chr);
                    }
                }
                break;
                case FLOAT_NUMBER: {
                    if (Character.isDigit(chr)) {
                        builder.append(chr);
                    } else {
                        found = true;
                        reader.unread(data);
                    }
                }
                break;
                case HEX_NUMBER: {
                    if (builder.length() == 1) {
                        if (chr == 'X' || chr == 'x') {
                            builder.append(chr);
                        } else {
                            if (chr == '.') {
                                builder.append(chr);
                                state = ParserState.FLOAT_NUMBER;
                            } else {
                                if (Character.isDigit(chr)) {
                                    state = ParserState.NUMBER;
                                } else {
                                    state = ParserState.NUMBER;
                                    found = true;
                                    reader.unread(data);
                                }
                            }
                        }
                    } else {
                        if (Character.isDigit(chr) || (chr >= 'a' && chr <= 'f') || (chr >= 'A' && chr <= 'F')) {
                            builder.append(chr);
                        } else {
                            found = true;
                            reader.unread(data);
                        }
                    }
                }
                break;
                case NUMBER: {
                    if (Character.isDigit(chr)) {
                        builder.append(chr);
                    } else {
                        if (chr == '.') {
                            builder.append(chr);
                            state = ParserState.FLOAT_NUMBER;
                        } else {
                            reader.unread(data);
                            found = true;
                        }
                    }
                }
                break;
                case VALUE_OR_FUNCTION: {
                    if (Character.isWhitespace(chr) || isDelimiterOrOperatorChar(chr)) {
                        reader.unread(data);
                        found = true;
                    } else {
                        builder.append(chr);
                    }
                }
                break;
                case SPECIAL_CHAR: {
                    switch (chr) {
                        case 'n':
                            builder.append('\n');
                            break;
                        case 't':
                            builder.append('\t');
                            break;
                        case 'b':
                            builder.append('\b');
                            break;
                        case 'f':
                            builder.append('\f');
                            break;
                        case 'r':
                            builder.append('\r');
                            break;
                        case '\\':
                            builder.append('\\');
                            break;
                        case '\"':
                            builder.append('\"');
                            break;
                        case '\'':
                            builder.append('\'');
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported special char detected \'\\" + chr + '\'');
                    }
                    state = ParserState.STRING;
                }
                break;
                case STRING: {
                    switch (chr) {
                        case '\"': {
                            found = true;
                        }
                        break;
                        case '\\': {
                            state = ParserState.SPECIAL_CHAR;
                        }
                        break;
                        default: {
                            builder.append(chr);
                        }
                    }
                }
                break;
                default:
                    throw new Error("Unsupported parser state [" + state.name() + ']');
            }
        }

        if (!found) {
            switch (state) {
                case SPECIAL_CHAR:
                case STRING:
                    throw new IllegalStateException("Unclosed string has been detected");
                default:
                    return null;
            }
        } else {
            ExpressionStackItem result = null;
            switch (state) {
                case FLOAT_NUMBER: {
                    result = Value.valueOf(Float.parseFloat(builder.toString()));
                }
                break;
                case HEX_NUMBER: {
                    final String text = builder.toString();
                    if ("0".equals(text)) {
                        result = Value.INT_ZERO;
                    } else {
                        final String str = PreprocessorUtils.extractTail("0x", text);
                        result = Value.valueOf(Long.parseLong(str, 16));
                    }
                }
                break;
                case NUMBER: {
                    result = Value.valueOf(Long.parseLong(builder.toString()));
                }
                break;
                case OPERATOR: {
                    final String operatorLC = builder.toString().toLowerCase();
                    for (final AbstractOperator operator : AbstractOperator.ALL_OPERATORS) {
                        if (operator.getKeyword().equals(operatorLC)) {
                            result = operator;
                            break;
                        }
                    }

                    if (result == null) {
                        throw new IllegalArgumentException("Unknown operator detected \'" + operatorLC + '\'');
                    }
                }
                break;
                case STRING: {
                    result = Value.valueOf(builder.toString());
                }
                break;
                case VALUE_OR_FUNCTION: {
                    final String str = builder.toString().toLowerCase();
                    if (str.charAt(0) == '$') {
                        if (context == null) {
                            throw new IllegalStateException("There is not a preprocessor context to define a user function [" + str + ']');
                        }

                        final PreprocessorExtension extension = context.getPreprocessorExtension();
                        if (extension == null) {
                            throw new IllegalStateException("There is not any defined preprocessor extension to get data about user functions [" + str + ']');
                        }

                        final String userFunctionName = PreprocessorUtils.extractTail("$", str);

                        // user defined
                        result = new FunctionDefinedByUser(userFunctionName, extension.getUserFunctionArity(userFunctionName), context);
                    } else {
                        if ("true".equals(str)) {
                            result = Value.BOOLEAN_TRUE;
                        } else if ("false".equals(str)) {
                            result = Value.BOOLEAN_FALSE;
                        } else {
                            final AbstractFunction function = AbstractFunction.findForName(str);
                            if (function == null) {
                                result = new Variable(str);
                            } else {
                                result = function;
                            }
                        }
                    }
                }
                break;
                default: {
                    throw new Error("Unsupported final parser state detected [" + state.name() + ']');
                }
            }
            return result;
        }
    }
}
