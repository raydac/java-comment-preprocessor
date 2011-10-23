package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class FunctionXML_ELEMENTAT extends AbstractXMLFunction {

    @Override
    public String getName() {
        return "xml_elementat";
    }

    public void execute(PreprocessorContext context, Expression _stack, int _index) {
        if (!_stack.areThereTwoValuesBefore(_index)) throw new IllegalStateException("Operation XML_ELEMENTAT needs two operands");

        Value _val1 = (Value)_stack.getItemAtPosition(_index-1);
        _index--;
        _stack.removeItemAt(_index);

        Value _val0 = (Value)_stack.getItemAtPosition(_index-1);
        _index--;
        _stack.removeItemAt(_index);

        NodeList p_nodeList = null;
        long l_indexElement = 0;

        switch (_val0.getType())
        {
            case INT:
                {
                    long l_index  = ((Long) _val0.getValue()).longValue();

                    p_nodeList = getXmlNodeListIndex((int)l_index);

                };break;
            default :
                throw new IllegalArgumentException("Function XML_ELEMENTAT needs INTEGER type as the first operand");
        }

        switch (_val1.getType())
        {
            case INT:
                {
                    l_indexElement  = ((Long) _val1.getValue()).longValue();

                };break;
            default :
                throw new IllegalArgumentException("Function XML_ELEMENTAT needs INTEGER type as the second operand");
        }

        Element p_Element = (Element) p_nodeList.item((int)l_indexElement);

        System.out.println("EEEEE "+p_Element);
        
        long l_index = findXmlElementIndex(p_Element);

        _stack.setItemAtPosition(_index, Value.valueOf(Long.valueOf(l_index)));
       
    }
    @Override
    public int getArity() {
        return 2;
    }
    
    
}
