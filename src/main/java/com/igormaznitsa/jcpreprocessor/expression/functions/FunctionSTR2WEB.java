package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;

public final class FunctionSTR2WEB extends AbstractFunction {

    @Override
    public String getName() {
        return "str2web";
    }

    public void execute(Expression stack, int index) {
       if (!stack.isThereOneValueBefore(index)) throw new IllegalStateException("Operation STR2WEB needs an operand");

        Value p_val = (Value)stack.getItemAtPosition(index-1);
        stack.removeItemAt(index);

        switch (p_val.getType())
        {
            case STRING:
                {
                    String s_result = (String) p_val.getValue();

                    StringBuffer p_strBuffer = new StringBuffer(s_result.length()<<1);

                    int i_strLen = s_result.length();
                    for(int li=0;li<i_strLen;li++)
                    {
                        char ch_char = s_result.charAt(li);

                        switch(ch_char)
                        {
                                case '&' : p_strBuffer.append("&amp;");break;
                                case ' ' : p_strBuffer.append("&nbsp;");break;
                                case '<' : p_strBuffer.append("&lt;");break;
                                case '>' : p_strBuffer.append("&gt;");break;
                                case '\"': p_strBuffer.append("&quot;");break;
                                case '€': p_strBuffer.append("&euro;");break;
                                case '©': p_strBuffer.append("&copy;");break;
                                case '¤': p_strBuffer.append("&curren;");break;
                                case '«': p_strBuffer.append("&laquo;");break;
                                case '»': p_strBuffer.append("&raquo;");break;
                                case '®': p_strBuffer.append("&reg;");break;
                                case '§': p_strBuffer.append("&sect;");break;
                                case '™': p_strBuffer.append("&trade;");break;
                                default:
                                {
                                    p_strBuffer.append(ch_char);
                                }
                        }
                    }


                    p_val.setValue(p_strBuffer.toString());
                };break;
            default :
                throw new IllegalArgumentException("Function STR2WEB processes only the STRING type");
        }

    }
    
    @Override
    public int getArity() {
        return 1;
    }
    
}
