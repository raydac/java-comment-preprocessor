package com.igormaznitsa.jcpreprocessor.expression;

import com.igormaznitsa.jcpreprocessor.JCPreprocessor;
import com.igormaznitsa.jcpreprocessor.cfg.Configurator;
import com.igormaznitsa.jcpreprocessor.expression.functions.AbstractFunction;
import com.igormaznitsa.jcpreprocessor.expression.functions.FunctionDefinedByUser;
import com.igormaznitsa.jcpreprocessor.expression.operators.AbstractOperator;
import com.igormaznitsa.jcpreprocessor.expression.operators.OperatorLEFTBRACKET;
import com.igormaznitsa.jcpreprocessor.expression.operators.OperatorRIGHTBRACKET;
import com.igormaznitsa.jcpreprocessor.extension.PreprocessorExtension;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Expression {

    private static final Map<String, AbstractOperator> OPERATORS = new HashMap<String, AbstractOperator>();
    private static final Map<String, AbstractOperator> SHORT_OPERATORS = new HashMap<String, AbstractOperator>();
    private static final Map<String, AbstractOperator> LONG_OPERATORS = new HashMap<String, AbstractOperator>();
    private static final Map<String, AbstractFunction> FUNCTIONS = new HashMap<String, AbstractFunction>();

    static {
        for (final AbstractOperator operator : AbstractOperator.ALL_OPERATORS) {
            OPERATORS.put(operator.getKeyword(), operator);
            if (operator.getKeyword().length() == 1) {
                SHORT_OPERATORS.put(operator.getKeyword(), operator);
            } else {
                LONG_OPERATORS.put(operator.getKeyword(), operator);
            }
        }

        for (final AbstractFunction function : AbstractFunction.ALL_FUNCTIONS) {
            FUNCTIONS.put(function.getName(), function);
        }
    }
    public static final int PRIORITY_VALUE = 6;
    private transient final List<ExpressionStackItem> INSIDE_STACK = new ArrayList<ExpressionStackItem>();

    private Expression() {
    }

    public ExpressionStackItem getItemAtPosition(final int position) {
        return INSIDE_STACK.get(position);
    }

    public void pushItemOnStack(final ExpressionStackItem item) {
        if (item == null) {
            throw new NullPointerException("Item is null");
        }
        INSIDE_STACK.add(item);
    }

    public ExpressionStackItem popItemFromStack(final ExpressionStackItem item) {
        return INSIDE_STACK.remove(INSIDE_STACK.size() - 1);
    }

    public int size() {
        return INSIDE_STACK.size();
    }

    public void removeItemAt(final int index) {
        INSIDE_STACK.remove(index);
    }

    public void setItemAtPosition(final int index, final ExpressionStackItem item) {
        INSIDE_STACK.set(index, item);
    }

    public boolean isEmpty() {
        return INSIDE_STACK.isEmpty();
    }

    public void swapElements(final int index1, final int index2) {
        final ExpressionStackItem temp = INSIDE_STACK.get(index1);
        INSIDE_STACK.set(index1, INSIDE_STACK.get(index2));
        INSIDE_STACK.set(index2, temp);
    }

    public boolean areThereTwoValuesBefore(final int checkingIndex) {
        boolean result = false;
        if (checkingIndex > 1) {
            result = INSIDE_STACK.get(checkingIndex - 1).getStackItemType() == ExpressionStackItemType.VALUE
                    && INSIDE_STACK.get(checkingIndex - 2).getStackItemType() == ExpressionStackItemType.VALUE;
        }

        return result;
    }

    public boolean isThereOneValueBefore(final int checkingPosition) {
        boolean result = false;
        if (checkingPosition > 0) {
            result = INSIDE_STACK.get(checkingPosition - 1).getStackItemType() == ExpressionStackItemType.VALUE;
        }

        return result;
    }

    private boolean removeAllDelimitersAndBrackets() {
        boolean delimiterMet = false;
        final Iterator<ExpressionStackItem> iterator = INSIDE_STACK.iterator();
        while (iterator.hasNext()) {
            final ExpressionStackItem item = iterator.next();
            final Class itemClass = item.getClass();
            if (itemClass == Delimiter.class) {
                delimiterMet = true;
                iterator.remove();
            } else if (itemClass == OperatorLEFTBRACKET.class || itemClass == OperatorRIGHTBRACKET.class) {
                iterator.remove();
            }
        }
        return delimiterMet;
    }

    //TODO Optimize it because it takes a lot of time!!!
    public static boolean sortFormulaStack(final Expression expressionStack) throws IOException {
        for (int li = 0; li < expressionStack.size() - 1; li++) {
            final ExpressionStackItem stackItem = expressionStack.getItemAtPosition(li);

            final int itemPriority = getPriorityForObject(stackItem);
            int bracketNumber = 0;
            boolean isUnary = false;

            if (stackItem instanceof Delimiter) {
                isUnary = false;
                continue;
            } else if (stackItem instanceof Value) {
                isUnary = false;
                continue;
            } else if (stackItem instanceof AbstractOperator) {
                isUnary = ((AbstractOperator) stackItem).isUnary();
                if (stackItem instanceof OperatorLEFTBRACKET || stackItem instanceof OperatorRIGHTBRACKET) {
                    continue;
                }
            } else if (stackItem instanceof AbstractFunction) {
                isUnary = true;
            }

            int i_lioff = 0;

            for (int lx = li + 1; lx < expressionStack.size(); lx++) {
                final ExpressionStackItem secondStackItem = expressionStack.getItemAtPosition(lx);
                final int newObjPriority = getPriorityForObject(secondStackItem);

                if (secondStackItem instanceof OperatorLEFTBRACKET) {
                    bracketNumber++;
                } else if (secondStackItem instanceof OperatorRIGHTBRACKET) {
                    if (bracketNumber == 0) {
                        break;
                    } else {
                        bracketNumber--;
                    }
                }

                if (bracketNumber == 0) {
                    if (newObjPriority > itemPriority) {
                        expressionStack.swapElements(lx, lx - 1);
                        i_lioff = -1;
                        if (isUnary) {
                            break;
                        }
                    } else {
                        break;
                    }
                } else {
                    expressionStack.swapElements(lx, lx - 1);
                }
            }
            li += i_lioff;

            if (bracketNumber != 0) {
                throw new IOException("There is not closed blacket");
            }
        }

        // remove delimiters and brackets
        return expressionStack.removeAllDelimitersAndBrackets();
    }

    private static String getNumberOrVariable(final String inString, final int startPosition) throws IOException {
        final int TYPE_NONE = 0;
        final int TYPE_INTEGER = 1;
        final int TYPE_STRING = 2;
        final int TYPE_VARIABLE = 3;
        final int TYPE_HEXINTEGER = 4;
        final int TYPE_FUNCTION = 5;

        int insideState = TYPE_NONE;

        final StringBuilder stringAccumulator = new StringBuilder(4);

        boolean lg_specchar = false;
        int currentPosition = startPosition;
        int stringLength = inString.length();
        int i_specStr = 0;

        while (currentPosition < stringLength) {
            final char readChar = inString.charAt(currentPosition++);

            switch (insideState) {
                case TYPE_NONE: {
                    switch (readChar) {
                        case ' ': {
                            stringAccumulator.append(readChar);
                        }
                        break;
                        case '\"': {
                            stringAccumulator.append(readChar);
                            insideState = TYPE_STRING;
                        }
                        break;
                        default: {
                            if ((readChar >= '0' && readChar <= '9') || readChar == '.') {
                                stringAccumulator.append(readChar);
                                insideState = TYPE_INTEGER;
                            } else if (readChar == '$') {
                                stringAccumulator.append(readChar);
                                insideState = TYPE_FUNCTION;
                            } else if ((readChar >= 'a' && readChar <= 'z') || (readChar >= 'A' && readChar <= 'Z') || readChar == '_') {
                                stringAccumulator.append(readChar);
                                insideState = TYPE_VARIABLE;
                            } else {
                                return String.valueOf(readChar);
                            }
                        }
                    }
                }
                break;
                case TYPE_INTEGER: {
                    if (readChar == 'x') {
                        stringAccumulator.append(readChar);
                        insideState = TYPE_HEXINTEGER;
                    } else if (readChar == '.' || (readChar >= '0' && readChar <= '9')) {
                        stringAccumulator.append(readChar);
                    } else {
                        return stringAccumulator.toString();
                    }
                }
                break;
                case TYPE_STRING: {
                    if (lg_specchar) {
                        switch (readChar) {
                            case '\"': {
                                stringAccumulator.append('\"');
                            }
                            break;
                            case 'n': {
                                stringAccumulator.append('\n');
                            }
                            break;
                            case 'r': {
                                stringAccumulator.append('\r');
                            }
                            break;
                            case 't': {
                                stringAccumulator.append('\t');
                            }
                            break;
                            case '\\': {
                                stringAccumulator.append('\\');
                            }
                            break;
                            default:
                                throw new IOException("Unknown special char");
                        }
                        i_specStr++;
                        lg_specchar = false;
                    } else if (readChar == '\"') {
                        stringAccumulator.append(readChar);
                        if (i_specStr != 0) {
                            while (i_specStr > 0) {
                                stringAccumulator.append(' ');
                                i_specStr--;
                            }
                        }
                        return stringAccumulator.toString();
                    } else if (readChar == '\\') {
                        lg_specchar = true;
                    } else {
                        stringAccumulator.append(readChar);
                    }
                }
                break;
                case TYPE_VARIABLE: {
                    if ((readChar >= 'a' && readChar <= 'z') || (readChar >= 'A' && readChar <= 'Z') || readChar == '_' || (readChar >= '0' && readChar <= '9')) {
                        stringAccumulator.append(readChar);
                    } else {
                        return stringAccumulator.toString();
                    }
                }
                break;
                case TYPE_FUNCTION: {
                    if ((readChar >= 'a' && readChar <= 'z') || (readChar >= 'A' && readChar <= 'Z') || readChar == '_' || (readChar >= '0' && readChar <= '9')) {
                        stringAccumulator.append(readChar);
                    } else {
                        return stringAccumulator.toString();
                    }
                }
                break;
                case TYPE_HEXINTEGER: {
                    if ((readChar >= 'a' && readChar <= 'f') || (readChar >= 'A' && readChar <= 'F') || (readChar >= '0' && readChar <= '9')) {
                        stringAccumulator.append(readChar);
                    } else {
                        return stringAccumulator.toString();
                    }
                }
                break;
            }
        }
        if (insideState == TYPE_STRING) {
            throw new IOException("You have not closed string value");
        }
        return stringAccumulator.toString();
    }

    private static boolean isFunction(final String str) {
        if (str.length() > 0 && str.charAt(0) == '$') {
            return true;
        }
        return FUNCTIONS.containsKey(str);
    }

    public static Expression parseStringExpression(final String _string, final Configurator cfg) throws IOException {
        final Expression p_stack = new Expression();
        int i_pos = 0;
        while (i_pos < _string.length()) {
            String s_ar = getOperationToken(_string, i_pos);
            if (s_ar != null) {
                i_pos += s_ar.length();
                s_ar = s_ar.trim();

                p_stack.pushItemOnStack(OPERATORS.get(s_ar));
                continue;
            }

            s_ar = getNumberOrVariable(_string, i_pos);
            if (s_ar.length() != 0) {
                i_pos += s_ar.length();
                s_ar = s_ar.trim();

                final Delimiter delimiter = Delimiter.valueOf(s_ar);
                final String s_arLc = s_ar.toLowerCase();

                if (delimiter != null) {
                    p_stack.pushItemOnStack(delimiter);
                } else if (isFunction(s_arLc)) {
                    if (s_arLc.charAt(0) == '$') {
                        // user defined function
                        if (cfg.getPreprocessorExtension() == null) {
                            throw new IOException("You have an user function \"" + s_ar + "\" but don't have defined an action listener");
                        }
                        final int i_args = cfg.getPreprocessorExtension().getArgumentsNumberForUserFunction(s_arLc);
                        if (i_args < 0) {
                            throw new IOException("Unknown user function \"" + s_ar + "\"");
                        }
                        p_stack.pushItemOnStack(new FunctionDefinedByUser(s_arLc, i_args, cfg));
                    } else {
                        // standard function
                        p_stack.pushItemOnStack(FUNCTIONS.get(s_arLc));
                    }
                } else {
                    final Value p_val = cfg.findVariableForName(s_ar);

                    if (p_val != null) {
                        p_stack.pushItemOnStack(p_val);
                    } else {
                        try {
                            p_stack.pushItemOnStack(new Value(s_ar));
                        } catch (Exception e) {
                            throw new IOException("Unsupported value or function \'" + s_ar + "\'");
                        }
                    }
                }
            }
        }

        return p_stack;
    }

    private static final int getPriorityForObject(ExpressionStackItem _obj) {
        switch (_obj.getStackItemType()) {
            case VALUE:
                return PRIORITY_VALUE;
            case OPERATOR:
                return ((AbstractOperator) _obj).getPriority();
            case FUNCTION:
                return ((AbstractFunction) _obj).getPriority();
            default:
                return -1;
        }
    }

    public static Value calculateFormulaStack(final Expression expressionStack, final boolean delimitersPresented, final PreprocessorExtension preprocessorExtension) throws IOException {
        int index = 0;
        while (expressionStack.size() != 1) {
            if (expressionStack.size() == index) {
                throw new IOException("Error formula");
            }

            ExpressionStackItem p_obj = expressionStack.getItemAtPosition(index);

            switch (p_obj.getStackItemType()) {
                case VALUE: {
                    index++;
                }
                break;
                case FUNCTION: {
                    AbstractFunction p_func = (AbstractFunction) p_obj;
                    p_func.execute(expressionStack, index);
                    index -= p_func.getArity();
                }
                break;
                case OPERATOR: {
                    ((AbstractOperator) p_obj).execute(expressionStack, index);
                    index = 0;
                }
                break;
                default:
                    throw new IllegalArgumentException("Unsupportes object on stack");
            }
        }
        if (!delimitersPresented && expressionStack.size() > 1) {
            throw new IOException("There is an operand without an operation");
        }
        return delimitersPresented ? expressionStack.size() > 1 ? null : (Value) expressionStack.getItemAtPosition(0) : (Value) expressionStack.getItemAtPosition(0);
    }

    public static final Value eval(final String expression) throws IOException {
        Configurator configurator = null;
        final JCPreprocessor preprocessorInstance = JCPreprocessor.getPreprocessorInstanceForThread();
        if (preprocessorInstance != null) {
            configurator = preprocessorInstance.getConfigurator();
        }

        final Expression parsedStack = parseStringExpression(expression, configurator);
        final boolean delimitersPresented = sortFormulaStack(parsedStack);
        return calculateFormulaStack(parsedStack, delimitersPresented, getPreprocessorExtension(configurator));
    }

    private static PreprocessorExtension getPreprocessorExtension(final Configurator cfg) {
        return cfg == null ? null : cfg.getPreprocessorExtension();
    }

    public static String getOperationToken(String _string, int _position) {
        int spacesCounter = 0;
        while (_position < _string.length()) {
            if (_string.charAt(_position) == ' ') {
                spacesCounter++;
                _position++;
                continue;
            }
            break;
        }

        if (_position + 1 < _string.length()) {
            // Checking  for long operations
            final String s_str = _string.substring(_position, _position + 2);
            if (LONG_OPERATORS.containsKey(s_str)) {
                return PreprocessorUtils.generateStringWithPrecendingSpaces(spacesCounter, s_str);
            }
        }


        final String s_str = String.valueOf(_string.charAt(_position));
        if (SHORT_OPERATORS.containsKey(s_str)) {
            return PreprocessorUtils.generateStringWithPrecendingSpaces(spacesCounter, s_str);
        }
        return null;
    }
}
