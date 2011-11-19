package com.igormaznitsa.jcpreprocessor.expression;

import com.igormaznitsa.jcpreprocessor.expression.functions.AbstractFunction;
import com.igormaznitsa.jcpreprocessor.expression.functions.FunctionABS;
import com.igormaznitsa.jcpreprocessor.expression.functions.xml.FunctionXML_GETATTRIBUTE;
import com.igormaznitsa.jcpreprocessor.expression.operators.AbstractOperator;
import com.igormaznitsa.jcpreprocessor.expression.operators.OperatorADD;
import com.igormaznitsa.jcpreprocessor.expression.operators.OperatorDIV;
import com.igormaznitsa.jcpreprocessor.expression.operators.OperatorEQU;
import com.igormaznitsa.jcpreprocessor.expression.operators.OperatorLESS;
import com.igormaznitsa.jcpreprocessor.expression.operators.OperatorMOD;
import com.igormaznitsa.jcpreprocessor.expression.operators.OperatorMUL;
import com.igormaznitsa.jcpreprocessor.expression.operators.OperatorSUB;
import java.io.PushbackReader;
import java.io.StringReader;
import org.junit.Test;
import static org.junit.Assert.*;

public class ExpressionParserTest {

    @Test
    public void testReplacingNegativeNumber() throws Exception {
        final ExpressionTree tree = new ExpressionTree();
        final OperatorSUB SUB = AbstractOperator.findForClass(OperatorSUB.class);
        
        tree.addItem(SUB);
        tree.addItem(Value.INT_ONE);
        tree.addItem(SUB);
        tree.addItem(Value.INT_TWO);
        
        tree.postProcess();
        
        final ExpressionTreeElement root = tree.getRoot();
        
        assertEquals("Root must be SUB", SUB, root.getItem());
        assertEquals("Left must be -1", Value.valueOf(Long.valueOf(-1L)), root.getElementAt(0).getItem());
        assertEquals("Right must be 2", Value.INT_TWO, root.getElementAt(1).getItem());
    }
    
    @Test
    public void testNextItem_zero() throws Exception {
        final PushbackReader reader = new PushbackReader(new StringReader("0"));
        assertEquals("Must be 0", Value.INT_ZERO, ExpressionParser.getInstance().nextItem(reader, null));
        assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
    }
    
    @Test
    public void testNextItem_negativeNumber() throws Exception {
        final PushbackReader reader = new PushbackReader(new StringReader("-1"));
        assertEquals("Must be SUB", AbstractOperator.findForClass(OperatorSUB.class), ExpressionParser.getInstance().nextItem(reader, null));
        assertEquals("Must be 1", Value.INT_ONE, ExpressionParser.getInstance().nextItem(reader, null));
        assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
    }
    
    @Test
    public void testNextItem_zeroLess() throws Exception {
        final PushbackReader reader = new PushbackReader(new StringReader("0<"));
        assertEquals("Must be 0", Value.INT_ZERO, ExpressionParser.getInstance().nextItem(reader, null));
        assertEquals("Must be LESS", AbstractOperator.findForClass(OperatorLESS.class), ExpressionParser.getInstance().nextItem(reader, null));
        assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
    }
    
    @Test
    public void testNextItem_oneValue() throws Exception {
        final PushbackReader reader = new PushbackReader(new StringReader("3"));
        assertEquals("Must be 3", Value.INT_THREE, ExpressionParser.getInstance().nextItem(reader, null));
        assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
    }
    
    @Test
    public void testNextItem_oneHexValue() throws Exception {
        final PushbackReader reader = new PushbackReader(new StringReader("0xfF"));
        assertEquals("Must be 255", Value.valueOf(Long.valueOf(255L)), ExpressionParser.getInstance().nextItem(reader, null));
        assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
    }
    
    @Test
    public void testNextItem_oneBooleanTrueValue() throws Exception {
        final PushbackReader reader = new PushbackReader(new StringReader("true"));
        assertEquals("Must be TRUE", Value.BOOLEAN_TRUE, ExpressionParser.getInstance().nextItem(reader, null));
        assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
    }
    
    @Test
    public void testNextItem_oneBooleanFalseValue() throws Exception {
        final PushbackReader reader = new PushbackReader(new StringReader("false"));
        assertEquals("Must be FALSE", Value.BOOLEAN_FALSE, ExpressionParser.getInstance().nextItem(reader, null));
        assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
    }
    
    @Test
    public void testNextItem_oneOperator() throws Exception {
        final PushbackReader reader = new PushbackReader(new StringReader("/"));
        assertEquals("Must be DIV", AbstractOperator.findForClass(OperatorDIV.class), ExpressionParser.getInstance().nextItem(reader, null));
        assertNull("Must be null", ExpressionParser.getInstance().nextItem(reader, null));
    }
    
    @Test
    public void testNextItem_complexExpression() throws Exception {
        
        final PushbackReader reader = new PushbackReader(new StringReader("xml_getattribute(1.3%abs(1+2)*3/4,\"hello\"==\"\nworld\t\")"));
        
        final ExpressionStackItem [] items = new ExpressionStackItem[]{
            AbstractFunction.findForClass(FunctionXML_GETATTRIBUTE.class),
            ExpressionParser.SpecialItem.BRACKET_OPENING,
            Value.valueOf(Float. valueOf(1.3f)),
            AbstractOperator.findForClass(OperatorMOD.class),
            AbstractFunction.findForClass(FunctionABS.class),
            ExpressionParser.SpecialItem.BRACKET_OPENING,
            Value.INT_ONE,
            AbstractOperator.findForClass(OperatorADD.class),
            Value.INT_TWO,
            ExpressionParser.SpecialItem.BRACKET_CLOSING,
            AbstractOperator.findForClass(OperatorMUL.class),
            Value.INT_THREE,
            AbstractOperator.findForClass(OperatorDIV.class),
            Value.INT_FOUR,
            ExpressionParser.SpecialItem.COMMA,
            Value.valueOf("hello"),
            AbstractOperator.findForClass(OperatorEQU.class),
            Value.valueOf("\nworld\t"),
            ExpressionParser.SpecialItem.BRACKET_CLOSING
        };
        
        int index = 0;
        for(final ExpressionStackItem item : items){
            assertEquals("Position "+index+" must be equals",item, ExpressionParser.getInstance().nextItem(reader, null));
            index++;
        }
        assertNull(ExpressionParser.getInstance().nextItem(reader, null));
    }
 
    @Test
    public void testParsing_oneValue() throws Exception {
        final ExpressionParser parser = ExpressionParser.getInstance();
        final ExpressionTree tree = parser.parse("3", null);
        
        final ExpressionTreeElement root = tree.getRoot();
        assertEquals("Root is 3", Value.INT_THREE, root.getItem());
    }

    @Test
    public void testParsing_negativeNumber() throws Exception {
        final ExpressionParser parser = ExpressionParser.getInstance();
        final ExpressionTree tree = parser.parse(Long.toString(Long.MIN_VALUE+1), null);
        
        final ExpressionTreeElement root = tree.getRoot();
        assertEquals("Root is Long.MIN_VALUE+1", Value.valueOf(Long.valueOf(Long.MIN_VALUE+1)), root.getItem());
    }
    
    @Test
    public void testParsing_easyExpression() throws Exception {
        final ExpressionParser parser = ExpressionParser.getInstance();
        final ExpressionTree tree = parser.parse("3*4/8", null);
        
        final ExpressionTreeElement root = tree.getRoot();
        assertEquals("Root is DIV", AbstractOperator.findForClass(OperatorDIV.class), root.getItem());
        assertEquals("Right is 8", Value.valueOf(Long.valueOf(8L)), root.getElementAt(1).getItem());

        final ExpressionTreeElement left = root.getElementAt(0);
        assertEquals("Left is MUL", AbstractOperator.findForClass(OperatorMUL.class), left.getItem());
        assertEquals("Left-left is 3", Value.INT_THREE, left.getElementAt(0).getItem());
        assertEquals("Left-right is 4", Value.INT_FOUR, left.getElementAt(1).getItem());
    }
    
    @Test
    public void testParsing_complexExpression() throws Exception {
        final ExpressionParser parser = ExpressionParser.getInstance();
        final ExpressionTree tree = parser.parse("(var1+1)*xml_getattribute(\"first\",\"hello\"+\"world\")", null);
    
        final ExpressionTreeElement root = tree.getRoot();
        
        assertEquals("Root must be MUL",AbstractOperator.findForClass(OperatorMUL.class),root.getItem());
        
        final ExpressionTreeElement left = root.getElementAt(0);
        final ExpressionTreeElement right = root.getElementAt(1);
        
        assertEquals("Left must be ADD",AbstractOperator.findForClass(OperatorADD.class),left.getItem());
        assertEquals("Right must be Function",AbstractFunction.findForClass(FunctionXML_GETATTRIBUTE.class),right.getItem());
    }

    @Test
    public void testParsing_emptyBrakes() throws Exception {
        final ExpressionParser parser = ExpressionParser.getInstance();
        final ExpressionTree tree = parser.parse("()", null);
        assertNull("Must be null",tree.getRoot());
    }
}
