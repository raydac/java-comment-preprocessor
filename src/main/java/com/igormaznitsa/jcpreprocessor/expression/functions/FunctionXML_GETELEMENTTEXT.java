package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public final class FunctionXML_GETELEMENTTEXT extends AbstractXMLFunction {

    @Override
    public String getName() {
        return "xml_getelementtext";
    }

    public void execute(Expression _stack, int _index) {
        if (!_stack.isThereOneValueBefore(_index)) throw new IllegalStateException("Operation XML_GETELEMENTTEXT needs an operand");

        Value _val0 = (Value)_stack.getItemAtPosition(_index-1);
        _index--;
        _stack.removeItemAt(_index);

        switch (_val0.getType())
        {
            case INT:
                {
                    long l_elementIndex = ((Long) _val0.getValue()).longValue();

                    Element p_element = getXmlElementForIndex((int)l_elementIndex);
                    NodeList p_childNodes = p_element.getChildNodes();

                    StringBuilder p_strBuffer = new StringBuilder();
                    for(int li=0;li<p_childNodes.getLength();li++)
                    {
                        Node p_node = p_childNodes.item(li);
                        if (p_node instanceof Text) p_strBuffer.append(((Text)p_node).getData());
                    }

                    _stack.setItemAtPosition(_index, Value.valueOf(p_strBuffer.toString()));
                };break;
            default :
                throw new IllegalArgumentException("Function XML_GETELEMENTTEXT processes only the INTEGER types");
        }
    }
      @Override
    public int getArity() {
        return 1 ;
    }
    
  
}
