package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class FunctionXML_GETDOCUMENTELEMENT extends AbstractXMLFunction {

    @Override
    public String getName() {
        return "xml_getdocumentelement";
    }

    public void execute(PreprocessorContext context, Expression _stack, int _index) {
        if (!_stack.isThereOneValueBefore(_index)) throw new IllegalStateException("Operation XML_GETDOCUMENTELEMENT needs an operand");

        Value _val0 = (Value)_stack.getItemAtPosition(_index-1);
        _index--;
        _stack.removeItemAt(_index);

        switch (_val0.getType())
        {
            case INT:
                {
                    long l_documentIndex = ((Long) _val0.getValue()).longValue();
                    Document p_document = (Document) getXmlDocumentForIndex((int)l_documentIndex);
                    Element p_element = p_document.getDocumentElement();
                    int i_index  = findXmlElementIndex(p_element);
                    _stack.setItemAtPosition(_index, Value.valueOf(Long.valueOf(i_index)));
                };break;
            default :
                throw new IllegalArgumentException("Function XML_GETDOCUMENTELEMENT processes only the INTEGER types");
        }

    }
      @Override
    public int getArity() {
        return 1;
    }
    
  
}
