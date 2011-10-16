package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public final class FunctionXML_OPEN extends AbstractXMLFunction {

    @Override
    public String getName() {
        return "xml_open";
    }

    public void execute(File currentFile, Expression stack, int index) {
       if (!stack.isThereOneValueBefore(index)) throw new IllegalStateException("Operation XML_OPEN needs an operand");

        Value _val0 = (Value)stack.getItemAtPosition(index-1);
        index--;
        stack.removeElementAt(index);

        switch (_val0.getType())
        {
            case STRING:
                {
                    String s_result = (String) _val0.getValue();

                    DocumentBuilderFactory p_dbf = DocumentBuilderFactory.newInstance();
                    p_dbf.setIgnoringComments(true);

                    Document p_doc = null;
                    try
                    {
                        DocumentBuilder p_db = p_dbf.newDocumentBuilder();
                        p_doc = p_db.parse(new File(currentFile.getParentFile(),s_result));
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException("Inside function error ["+e.getMessage()+"]");
                    }

                    int i_index = addXmlDocument(p_doc);

                    stack.setItemAtPosition(index, new Value(new Long(i_index)));
                };break;
            default :
                throw new IllegalArgumentException("Function XML_OPEN processes only the STRING types");
        }

    }
        @Override
    public int getArity() {
        return 1;
    }
    

}
