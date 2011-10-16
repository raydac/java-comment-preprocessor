package com.igormaznitsa.jcpreprocessor.expression;

import com.igormaznitsa.jcpreprocessor.cfg.Configurator;
import com.igormaznitsa.jcpreprocessor.expression.functions.AbstractFunction;
import com.igormaznitsa.jcpreprocessor.expression.functions.FunctionDefinedByUser;
import com.igormaznitsa.jcpreprocessor.expression.operators.AbstractOperator;
import com.igormaznitsa.jcpreprocessor.expression.operators.OperatorLEFTBRACKET;
import com.igormaznitsa.jcpreprocessor.expression.operators.OperatorRIGHTBRACKET;
import com.igormaznitsa.jcpreprocessor.extension.PreprocessorExtension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Expression {

    private static final Map<String,AbstractOperator> OPERATORS = new HashMap<String,AbstractOperator>();
    private static final Map<String,AbstractOperator> SHORT_OPERATORS = new HashMap<String,AbstractOperator>();
    private static final Map<String,AbstractOperator> LONG_OPERATORS = new HashMap<String,AbstractOperator>();
    private static final Map<String,AbstractFunction> FUNCTIONS = new HashMap<String, AbstractFunction>();
    
    static {
        for(final AbstractOperator operator : AbstractOperator.ALL_OPERATORS){
            OPERATORS.put(operator.getKeyword(), operator);
            if (operator.getKeyword().length() == 1) {
                SHORT_OPERATORS.put(operator.getKeyword(), operator);
            } else {
                LONG_OPERATORS.put(operator.getKeyword(), operator);
            }
        }
        
        for(final AbstractFunction function : AbstractFunction.ALL_FUNCTIONS){
            FUNCTIONS.put(function.getName(), function);
        }
    }
    
    
    public static final int PRIORITY_FUNCTION = 5;
    public static final int PRIORITY_VALUE = 6;
    
    private transient final List<ExpressionStackItem> INSIDE_STACK = new ArrayList<ExpressionStackItem>();

    private Expression()
    {
    }
    
    public ExpressionStackItem getItemAtPosition(final int position) {
        return INSIDE_STACK.get(position);
    }

    public void pushItem(final ExpressionStackItem item) {
        if (item == null) {
            throw new NullPointerException("Item is null");
        }
        INSIDE_STACK.add(item);
    }

    public ExpressionStackItem popItem(final ExpressionStackItem item) {
        return INSIDE_STACK.remove(INSIDE_STACK.size() - 1);
    }

    public int size() {
        return INSIDE_STACK.size();
    }

    public void removeElementAt(final int index) {
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
            result =  INSIDE_STACK.get(checkingPosition - 1).getStackItemType() == ExpressionStackItemType.VALUE;
        } 
        
        return result;
    }
    
    public static final boolean sortFormulaStack(Expression _stack) throws IOException
    {
        boolean lg_result = false;

        for (int li = 0; li < _stack.size() - 1; li++)
        {
            ExpressionStackItem p_obj = _stack.getItemAtPosition(li);

            int i_prioritet = getPriorityForObject(p_obj);
            int i_bracketNumber = 0;
            boolean lg_unary = false;

            if (p_obj instanceof Delimiter)
            {
                lg_unary = false;
                continue;
            }
            else
            if (p_obj instanceof Value)
            {
                lg_unary = false;
                continue;
            }
            else if (p_obj instanceof AbstractOperator)
            {
                lg_unary = ((AbstractOperator)p_obj).isUnary();
                if (p_obj instanceof OperatorLEFTBRACKET || p_obj instanceof OperatorRIGHTBRACKET) continue;
            }
            else if (p_obj instanceof AbstractFunction)
            {
                lg_unary = true;
            }

            int i_lioff = 0;

            for (int lx = li + 1; lx < _stack.size(); lx++)
            {
                ExpressionStackItem p_nobj = _stack.getItemAtPosition(lx);
                int i_priorityOfNewObj = getPriorityForObject(p_nobj);

                if (p_nobj instanceof OperatorLEFTBRACKET)
                {
                    i_bracketNumber++;
                }
                else
                if (p_nobj instanceof OperatorRIGHTBRACKET)
                {
                    if (i_bracketNumber == 0)
                    {
                        break;
                    }
                    else
                        i_bracketNumber--;
                }

                if (i_bracketNumber == 0)
                {
                    if (i_priorityOfNewObj > i_prioritet)
                    {
                        _stack.swapElements(lx, lx - 1);
                        i_lioff = -1;
                        if (lg_unary) break;
                    }
                    else
                        break;
                }
                else
                    _stack.swapElements(lx, lx - 1);
            }
            li += i_lioff;

            if (i_bracketNumber != 0) throw new IOException("There is not closed blacket");
        }

        int li = 0;
        while (li < _stack.size())
        {
            Object p_obj = _stack.getItemAtPosition(li);
            if (p_obj instanceof Delimiter)
            {
                lg_result = true;
                _stack.removeElementAt(li);
            }
            else
            if (p_obj instanceof AbstractOperator)
            {
                if (p_obj instanceof OperatorLEFTBRACKET || p_obj instanceof OperatorRIGHTBRACKET)
                    _stack.removeElementAt(li);
                else
                    li++;
            }
            else
                li++;
        }

        return lg_result;
    }

    
    
    private static final String getNumberOrVariable(String _string, int _pos) throws IOException
    {
        final int TYPE_NONE = 0;
        final int TYPE_INTEGER = 1;
        final int TYPE_STRING = 2;
        final int TYPE_VARIABLE = 3;
        final int TYPE_HEXINTEGER = 4;
        final int TYPE_FUNCTION = 5;

        int i_type = TYPE_NONE;

        String s_ak = "";

        boolean lg_specchar = false;
        int i_stringLength = _string.length();
        int i_specStr = 0;

        while (_pos < i_stringLength)
        {
            char c_char = _string.charAt(_pos++);

            switch (i_type)
            {
                case TYPE_NONE:
                    {
                        switch (c_char)
                        {
                            case ' ':
                                s_ak += c_char;
                                break;
                            case '\"':
                                {
                                    s_ak += c_char;
                                    i_type = TYPE_STRING;
                                }
                                ;
                                break;
                            default :
                                {
                                    if ((c_char >= '0' && c_char <= '9') || c_char == '.')
                                    {
                                        s_ak += c_char;
                                        i_type = TYPE_INTEGER;
                                    }
                                    else if (c_char == '$')
                                    {
                                        s_ak += c_char;
                                        i_type = TYPE_FUNCTION;
                                    }
                                    else if ((c_char >= 'a' && c_char <= 'z') || (c_char >= 'A' && c_char <= 'Z') || c_char == '_')
                                    {
                                        s_ak += c_char;
                                        i_type = TYPE_VARIABLE;
                                    }
                                    else
                                        return ""+c_char;
                                }
                        }
                    }
                    ;
                    break;
                case TYPE_INTEGER:
                    {
                        if (c_char == 'x')
                        {
                            s_ak += c_char;
                            i_type = TYPE_HEXINTEGER;
                        }
                        else if (c_char == '.' || (c_char >= '0' && c_char <= '9'))
                        {
                            s_ak += c_char;
                        }
                        else
                            return s_ak;
                    }
                    ;
                    break;
                case TYPE_STRING:
                    {
                        if (lg_specchar)
                        {
                            switch (c_char)
                            {
                                case '\"':
                                    {
                                        s_ak += '\"';
                                    }
                                    ;
                                    break;
                                case 'n':
                                    {
                                        s_ak += '\n';
                                    }
                                    ;
                                    break;
                                case 'r':
                                    {
                                        s_ak += '\r';
                                    }
                                    ;
                                    break;
                                case '\\':
                                    {
                                        s_ak += '\\';
                                    }
                                    ;
                                    break;
                                default :
                                    throw new IOException("Unknown special char");
                            }
                            i_specStr++;
                            lg_specchar = false;
                        }
                        else if (c_char == '\"')
                        {
                            s_ak += c_char;
                            if (i_specStr != 0)
                            {
                                while (i_specStr > 0)
                                {
                                    s_ak += ' ';
                                    i_specStr--;
                                }
                            }
                            return s_ak;
                        }
                        else if (c_char == '\\')
                        {
                            lg_specchar = true;
                        }
                        else
                        {
                            s_ak += c_char;
                        }
                    }
                    ;
                    break;
                case TYPE_VARIABLE:
                    {
                        if ((c_char >= 'a' && c_char <= 'z') || (c_char >= 'A' && c_char <= 'Z') || c_char == '_' || (c_char >= '0' && c_char <= '9'))
                        {
                            s_ak += c_char;
                        }
                        else
                            return s_ak;
                    }
                    ;
                    break;
                case TYPE_FUNCTION:
                    {
                        if ((c_char >= 'a' && c_char <= 'z') || (c_char >= 'A' && c_char <= 'Z') || c_char == '_' || (c_char >= '0' && c_char <= '9'))
                        {
                            s_ak += c_char;
                        }
                        else
                            return s_ak;
                    }
                    ;
                    break;
                case TYPE_HEXINTEGER:
                    {
                        if ((c_char >= 'a' && c_char <= 'f') || (c_char >= 'A' && c_char <= 'F') || (c_char >= '0' && c_char <= '9'))
                        {
                            s_ak += c_char;
                        }
                        else
                            return s_ak;
                    }
                    ;
                    break;
            }
        }
        if (i_type == TYPE_STRING) throw new IOException("You have not closed string value");
        return s_ak;
    }

    private static boolean isFunction(final String str) {
        if (str.startsWith("$")) return true;
        return FUNCTIONS.containsKey(str);
    }
    
    public static Expression parseStringExpression(final String _string, final Configurator cfg) throws IOException {
       Expression p_stack = new Expression();
        int i_pos = 0;
        while (i_pos < _string.length())
        {
            String s_ar = getOperationToken(_string, i_pos);
            if (s_ar != null)
            {
                i_pos += s_ar.length();
                s_ar = s_ar.trim();

                p_stack.pushItem(OPERATORS.get(s_ar));
                continue;
            }

            s_ar = getNumberOrVariable(_string, i_pos);
            if (s_ar.length() != 0)
            {
                i_pos += s_ar.length();
                s_ar = s_ar.trim();

                final Delimiter delimiter = Delimiter.valueOf(s_ar);
                
                if (delimiter!=null)
                {
                    p_stack.pushItem(delimiter);
                }
                else
                if (isFunction(s_ar.toLowerCase()))
                {
                    final String normalizedName = s_ar.toLowerCase();
                    if (normalizedName.charAt(0) == '$') {
                        // user defined function
                        if (cfg.getPreprocessorExtension() == null) throw new IOException("You have an user function \""+s_ar+"\" but don't have defined an action listener");
                        int i_args = cfg.getPreprocessorExtension().getArgumentsNumberForUserFunction(normalizedName);
                        if (i_args<0) throw new IOException("Unknown user function \""+s_ar+"\"");
                        p_stack.pushItem(new FunctionDefinedByUser(normalizedName, i_args, cfg));
                    } else {
                        // standard function
                        p_stack.pushItem(FUNCTIONS.get(normalizedName));
                    }
                }
                else
                {
                    Value p_val = cfg.findVariableForName(s_ar);

                    if (p_val != null)
                    {
                        p_stack.pushItem(p_val);
                    }
                    else
                    {
                        try
                        {
                            p_stack.pushItem(new Value(s_ar));
                        }
                        catch (Exception e)
                        {
                            throw new IOException("Unsupported value or function \'" + s_ar + "\'");
                        }
                    }
                }
            }
        }

        return p_stack;
    }
    
    private static final int getPriorityForObject(ExpressionStackItem _obj)
    {
        switch(_obj.getStackItemType()){
            case VALUE : return PRIORITY_VALUE;
            case OPERATOR : return ((AbstractOperator) _obj).getPriority();
            case FUNCTION : return ((AbstractFunction)_obj).getPriority();
            default: return -1;
        }
    }


    public static final Value calculateFormulaStack(File processingFile,Expression _stack,boolean _delimetersPresented,PreprocessorExtension _actionListener) throws IOException
    {
        int i_indx = 0;
        while (_stack.size() != 1)
        {
            if (_stack.size()==i_indx)
                throw new IOException("Error formula");

           
            ExpressionStackItem p_obj = _stack.getItemAtPosition(i_indx);
           
            switch(p_obj.getStackItemType()){
                case VALUE : {
                    i_indx++;
                }break;
                case FUNCTION : {
                    AbstractFunction p_func = (AbstractFunction) p_obj;
                    p_func.execute(processingFile,_stack, i_indx);
                    i_indx -= p_func.getArity();
                }break;
                case OPERATOR : {
                     ((AbstractOperator)p_obj).execute(processingFile, _stack, i_indx);
                    i_indx = 0;                   
                }break;
                default: throw new IllegalArgumentException("Unsupportes object on stack");
            }
        }
        if (!_delimetersPresented && _stack.size() > 1) throw new IOException("There is an operand without an operation");
        return _delimetersPresented ? _stack.size()>1 ? null : (Value) _stack.getItemAtPosition(0) : (Value) _stack.getItemAtPosition(0);
    }

    public static final Value evaluateFormula(File processingFile, String _string, final Configurator cfg) throws IOException
    {
        Expression p_stack = parseStringExpression(_string, cfg);

//        p_stack.printFormulaStack();
//        System.out.println("-------------");
         boolean lg_delimeters = sortFormulaStack(p_stack);

//        p_stack.printFormulaStack();

        return calculateFormulaStack(processingFile, p_stack,lg_delimeters,cfg.getPreprocessorExtension());
    }

    public static final String getOperationToken(String _string, int _position)
    {
        String s_spaces = "";
        while (_position < _string.length())
        {
            if (_string.charAt(_position) == ' ')
            {
                s_spaces += ' ';
                _position++;
                continue;
            }
            break;
        }

        if (_position + 1 < _string.length())
        {
            // Checking  for long operations
            String s_str = _string.substring(_position, _position + 2);
            if (LONG_OPERATORS.containsKey(s_str)){
                return s_spaces + s_str;
            }
        }

        
        String s_str = "" + _string.charAt(_position);
        if (SHORT_OPERATORS.containsKey(s_str)){
            return s_spaces + s_str;
        }
        return null;
    }

    
    public static final Value evaluateFormula(File processingFile, Expression _formula,PreprocessorExtension _actionListener) throws IOException
    {
        boolean lg_delimeters = sortFormulaStack(_formula);

        return calculateFormulaStack(processingFile, _formula,lg_delimeters,_actionListener);
    }

    
}
