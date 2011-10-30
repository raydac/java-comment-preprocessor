package com.igormaznitsa.jcpreprocessor.expression;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.functions.AbstractFunction;
import com.igormaznitsa.jcpreprocessor.expression.functions.FunctionDefinedByUser;
import com.igormaznitsa.jcpreprocessor.expression.operators.AbstractOperator;
import com.igormaznitsa.jcpreprocessor.expression.operators.OperatorLEFTBRACKET;
import com.igormaznitsa.jcpreprocessor.expression.operators.OperatorRIGHTBRACKET;
import com.igormaznitsa.jcpreprocessor.extension.PreprocessorExtension;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Expression {

    private static final Map<String, AbstractOperator> SHORT_OPERATORS = new HashMap<String, AbstractOperator>();
    private static final Map<String, AbstractOperator> LONG_OPERATORS = new HashMap<String, AbstractOperator>();
    private static final Map<String, AbstractFunction> FUNCTIONS = new HashMap<String, AbstractFunction>();

    static {
        for (final AbstractOperator operator : AbstractOperator.ALL_OPERATORS) {
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
    private transient final List<ExpressionStackItem> INSIDE_STACK = new ArrayList<ExpressionStackItem>(5);

    private final PreprocessorContext context;
    
    private Expression(final PreprocessorContext context) {
        this.context = context;
    }

    public ExpressionStackItem getItemAtPosition(final int position) {
        return INSIDE_STACK.get(position);
    }

    public void push(final ExpressionStackItem item) {
        if (item == null) {
            throw new NullPointerException("Item is null");
        }
        INSIDE_STACK.add(item);
    }

    public ExpressionStackItem peek() {
        if (INSIDE_STACK.isEmpty()) {
            return null;
        } else {
            return INSIDE_STACK.get(INSIDE_STACK.size() - 1);
        }
    }

    public ExpressionStackItem pop(final ExpressionStackItem item) {
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

    public void swapItems(final int index1, final int index2) {
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
    private boolean sortFormulaStack() {
        for (int processingItemIndex = 0; processingItemIndex < INSIDE_STACK.size() - 1; processingItemIndex++) {
            final ExpressionStackItem stackItem = INSIDE_STACK.get(processingItemIndex);

            final int itemPriority = stackItem.getPriority();
            boolean isUnary = false;

            boolean toContinue = true;
            switch (stackItem.getStackItemType()) {
                case VALUE:
                case DELIMITER: {
                    isUnary = false;
                }
                break;
                case OPERATOR: {
                    isUnary = ((AbstractOperator) stackItem).isUnary();
                    toContinue = stackItem instanceof OperatorLEFTBRACKET || stackItem instanceof OperatorRIGHTBRACKET;
                }
                break;
                case FUNCTION: {
                    toContinue = false;
                    isUnary = true;
                }
                break;
                default: {
                    throw new RuntimeException("Unsupported type detected");
                }
            }
            if (toContinue) {
                continue;
            }

            int offsetOfIndexForCurrentProcessingItem = 0;
            int bracketCounter = 0;

            for (int i = processingItemIndex + 1; i < INSIDE_STACK.size(); i++) {
                final ExpressionStackItem secondStackItem = INSIDE_STACK.get(i);
                final int newObjPriority = secondStackItem.getPriority();

                if (secondStackItem instanceof OperatorLEFTBRACKET) {
                    bracketCounter++;
                } else if (secondStackItem instanceof OperatorRIGHTBRACKET) {
                    if (bracketCounter == 0) {
                        break;
                    } else {
                        bracketCounter--;
                    }
                }

                if (bracketCounter == 0) {
                    if (newObjPriority > itemPriority) {
                        swapItems(i, i - 1);
                        offsetOfIndexForCurrentProcessingItem = -1;
                        if (isUnary) {
                            break;
                        }
                    } else {
                        break;
                    }
                } else {
                    swapItems(i, i - 1);
                }
            }
            processingItemIndex += offsetOfIndexForCurrentProcessingItem;

            if (bracketCounter != 0) {
                throw new RuntimeException ("An Unclosed bracket has been detected");
            }
        }

        // remove delimiters and brackets
        return removeAllDelimitersAndBrackets();
    }

    private static String readNumberOrVariableFromString(final String inString, final int startPosition) {
        final int TYPE_NONE = 0;
        final int TYPE_INTEGER = 1;
        final int TYPE_STRING = 2;
        final int TYPE_VARIABLE = 3;
        final int TYPE_HEXINTEGER = 4;
        final int TYPE_FUNCTION = 5;

        int insideState = TYPE_NONE;

        final StringBuilder stringAccumulator = new StringBuilder(4);

        boolean flagSpecialChar = false;
        int currentPosition = startPosition;
        int stringLength = inString.length();
        int specialCharCounter = 0;

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
                            } else if (PreprocessorUtils.isCharAllowedInVariableOrFunctionName(readChar)) {
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
                    if (readChar == 'x' || readChar == 'X') {
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
                    if (flagSpecialChar) {
                        switch (readChar) {
                            case '\"': {
                                stringAccumulator.append('\"');
                            }
                            break;
                            case 'f': {
                                stringAccumulator.append('\f');
                            }
                            break;
                            case 'b': {
                                stringAccumulator.append('\b');
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
                                throw new RuntimeException("Unknown special char detected \'\\"+readChar+'\'');
                        }
                        specialCharCounter++;
                        flagSpecialChar = false;
                    } else if (readChar == '\"') {
                        stringAccumulator.append(readChar);
                        if (specialCharCounter != 0) {
                            while (specialCharCounter > 0) {
                                stringAccumulator.append(' ');
                                specialCharCounter--;
                            }
                        }
                        return stringAccumulator.toString();
                    } else if (readChar == '\\') {
                        flagSpecialChar = true;
                    } else {
                        stringAccumulator.append(readChar);
                    }
                }
                break;
                case TYPE_VARIABLE: {
                    if (PreprocessorUtils.isCharAllowedInVariableOrFunctionName(readChar)) {
                        stringAccumulator.append(readChar);
                    } else {
                        return stringAccumulator.toString();
                    }
                }
                break;
                case TYPE_FUNCTION: {
                    if (PreprocessorUtils.isCharAllowedInVariableOrFunctionName(readChar)) {
                        stringAccumulator.append(readChar);
                    } else {
                        return stringAccumulator.toString();
                    }
                }
                break;
                case TYPE_HEXINTEGER: {
                    if (PreprocessorUtils.isCharAllowedAtHexNumber(readChar)) {
                        stringAccumulator.append(readChar);
                    } else {
                        return stringAccumulator.toString();
                    }
                }
                break;
            }
        }
        if (insideState == TYPE_STRING) {
            throw new RuntimeException("Detected an unclosed string");
        }
        return stringAccumulator.toString();
    }

    public static Expression prepare(final String stringToBeParsed, final PreprocessorContext context, final PreprocessingState state) {
        if (stringToBeParsed == null) {
            throw new NullPointerException("String is null");
        }

        final PreprocessorExtension preprocessorExtension = context == null ? null : context.getPreprocessorExtension();

        final Expression expressionStack = new Expression(context);
        int position = 0;
        while (position < stringToBeParsed.length()) {
            String token = getOperationToken(stringToBeParsed, position);
            if (token != null) {
                position += token.length();
                token = token.trim();

                AbstractOperator operator = SHORT_OPERATORS.get(token);
                if (operator == null) {
                    operator = LONG_OPERATORS.get(token);
                }
                if (operator == null) {
                    throw new NullPointerException("Operator is null");
                }
                expressionStack.push(operator);
                continue;
            }

            token = readNumberOrVariableFromString(stringToBeParsed, position);
            if (token.length() != 0) {
                position += token.length();
                token = token.trim();

                final Delimiter delimiter = Delimiter.valueOf(token);
                final String tokenInLowerCase = token.toLowerCase();

                if (delimiter != null) {
                    expressionStack.push(delimiter);
                } else if (isFunctionName(tokenInLowerCase)) {
                    if (tokenInLowerCase.charAt(0) == '$') {
                        // user defined function
                        if (preprocessorExtension == null) {
                            throw new RuntimeException("A User function \'"+token+"\'has been detected but a processor is not defined");
                        }
                        final int i_args = preprocessorExtension.getUserFunctionArity(tokenInLowerCase);
                        if (i_args < 0) {
                            throw new RuntimeException("Unknown user defined function \'"+token+"\' has been detected");
                        }
                        expressionStack.push(new FunctionDefinedByUser(tokenInLowerCase, i_args, context));
                    } else {
                        // standard function
                        expressionStack.push(FUNCTIONS.get(tokenInLowerCase));
                    }
                } else {
                    if (context == null) {
                        throw new IllegalStateException("There is not a context to use variables");
                    }

                    final Value value = context.findVariableForName(token,state);

                    if (value != null) {
                        expressionStack.push(value);
                    } else {
                        try {
                            expressionStack.push(Value.recognizeOf(token));
                        } catch (Exception e) {
                            throw new RuntimeException("Unsupported value or function detected \'" + token + "\'",e);
                        }
                    }
                }
            }
        }

        return expressionStack;
    }

    //TODO it ignores DELIMITERS flag during calculation
    private Value calculate(final boolean delimitersPresented, final PreprocessorContext context) {
        int index = 0;
        while (INSIDE_STACK.size() != 1) {
            if (INSIDE_STACK.size() == index) {
                throw new RuntimeException("Internal expression error detected");
            }

            final ExpressionStackItem expressionItem = INSIDE_STACK.get(index);

            switch (expressionItem.getStackItemType()) {
                case VALUE: {
                    index++;
                }
                break;
                case FUNCTION: {
                    AbstractFunction function = (AbstractFunction) expressionItem;
                    function.execute(context,this, index);
                    index -= function.getArity();
                }
                break;
                case OPERATOR: {
                    ((AbstractOperator) expressionItem).execute(context,this, index);
                    index = 0;
                }
                break;
                default:
                    throw new IllegalArgumentException("Unsupported object on stack");
            }
        }
        if (!delimitersPresented && INSIDE_STACK.size() > 1) {
            throw new RuntimeException("An Operand without an operator has been detected");
        }
        return delimitersPresented ? (INSIDE_STACK.size() > 1 ? null : (Value) INSIDE_STACK.get(0)) : (Value) INSIDE_STACK.get(0);
    }

    public static Value eval(final String expression, final PreprocessorContext context, final PreprocessingState state) {
        final Expression parsedStack = prepare(expression,context,state);
        return parsedStack.eval();
    }

    public Value eval() {
        final boolean delimitersPresented = sortFormulaStack();
        return calculate(delimitersPresented, context);
    }

    private static String getOperationToken(final String inString, int pos) {

        // count of spaces 
        int spacesCounter = 0;
        while (pos < inString.length()) {
            if (inString.charAt(pos) == ' ') {
                spacesCounter++;
                pos++;
                continue;
            }
            break;
        }

        if (pos + 1 < inString.length()) {
            // Checking  for long operator
            final String substr = inString.substring(pos, pos + 2);
            if (LONG_OPERATORS.containsKey(substr)) {
                return PreprocessorUtils.generateStringWithPrecendingSpaces(spacesCounter, substr);
            }
        }

        final String s_str = String.valueOf(inString.charAt(pos));
        if (SHORT_OPERATORS.containsKey(s_str)) {
            return PreprocessorUtils.generateStringWithPrecendingSpaces(spacesCounter, s_str);
        }
        return null;
    }

    private static boolean isFunctionName(final String str) {
        if (str.length() > 0 && str.charAt(0) == '$') {
            return true;
        }
        return FUNCTIONS.containsKey(str);
    }
}
