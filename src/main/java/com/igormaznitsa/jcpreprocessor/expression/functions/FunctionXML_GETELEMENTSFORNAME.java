package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class FunctionXML_GETELEMENTSFORNAME extends AbstractXMLFunction {

    @Override
    public String getName() {
        return "xml_getelementsforname";
    }

    public void execute(PreprocessorContext context, Expression _stack, int _index) {
        if (!_stack.areThereTwoValuesBefore(_index)) throw new IllegalStateException("Operation XML_GETELEMENTSFORNAME needs two operands");

        Value _val1 = (Value)_stack.getItemAtPosition(_index-1);
        _index--;
        _stack.removeItemAt(_index);

        Value _val0 = (Value)_stack.getItemAtPosition(_index-1);
        _index--;
        _stack.removeItemAt(_index);

        String s_tagName = "";
        Element p_element = null;

        switch (_val0.getType())
        {
            case INT:
                {
                    long l_index  = ((Long) _val0.getValue()).longValue();

                    p_element =  getXmlElementForIndex((int)l_index);

                };break;
            default :
                throw new IllegalArgumentException("Function XML_GETELEMENTSFORNAME needs INTEGER type as the first operand");
        }

        switch (_val1.getType())
        {
            case STRING:
                {
                    s_tagName  = ((String) _val1.getValue());

                };break;
            default :
                throw new IllegalArgumentException("Function XML_GETELEMENTSFORNAME needs STRING type as the second operand");
        }

        try
        {
            NodeList p_nodeList = p_element.getElementsByTagName(s_tagName);
            int i_listIndex = findXmlNodelistIndex(p_nodeList);

            _stack.setItemAtPosition(_index, Value.valueOf(Long.valueOf(i_listIndex)));
        } catch (NullPointerException e)
        {
            throw new RuntimeException("Strange error [s_tagName="+s_tagName+", p_element="+p_element+"]");
        }
    }
        @Override
    public int getArity() {
        return 2;
    }
    

}
