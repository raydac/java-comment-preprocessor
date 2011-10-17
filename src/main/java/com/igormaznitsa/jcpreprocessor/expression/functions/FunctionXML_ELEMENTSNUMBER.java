package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;
import org.w3c.dom.NodeList;

public final class FunctionXML_ELEMENTSNUMBER extends AbstractXMLFunction {

    @Override
    public String getName() {
        return "xml_elementsnumber";
    }

    public void execute(Expression _stack, int _index) {
        if (!_stack.isThereOneValueBefore(_index)) throw new IllegalStateException("Operation XML_ELEMENTSNUMBER needs an operand");

        Value _val0 = (Value)_stack.getItemAtPosition(_index-1);
        _index--;
        _stack.removeItemAt(_index);

        switch (_val0.getType())
        {
            case INT:
                {
                    long l_listIndex = ((Long) _val0.getValue()).longValue();

                    NodeList p_nodeList = getXmlNodeListIndex((int)l_listIndex);

                    _stack.setItemAtPosition(_index, new Value(new Long(p_nodeList.getLength())));
                };break;
            default :
                throw new IllegalArgumentException("Function XML_ELEMENTSNUMBER processes only the INTEGER types");
        }
    }
     @Override
    public int getArity() {
        return 1;
    }
    
   
}
