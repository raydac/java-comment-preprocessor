package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;

public final class FunctionDefinedByUser extends AbstractFunction {

    private final String name;
    private final int argsNumber;
    private final PreprocessorContext configurator;
    private final ValueType [][] argTypes;
    
    public FunctionDefinedByUser(final String name, final int argsNumber, final PreprocessorContext context) {
        super();
        if (name == null) {
            throw new NullPointerException("Name is null");
        }

        if (argsNumber < 0) {
            throw new IllegalArgumentException("Argument number is less than zero");
        }

        if (context == null) {
            throw new NullPointerException("Context is null");
        }

        this.name = name;
        this.argsNumber = argsNumber;
        this.configurator = context;
        
        final ValueType [] types = new ValueType[argsNumber];
        
        for(int li=0;li<argsNumber;li++){
            types[li] = ValueType.ANY;
        }
        this.argTypes = new ValueType[][]{types};
    }

    @Override
    public String getName() {
        return name;
    }

    public int getArity() {
        return argsNumber;
    }

    public Value execute(final PreprocessorContext context, final Value [] values) {
        return context.getPreprocessorExtension().processUserFunction(name, values);
    }
    
    @Override
    public ValueType[][] getAllowedArgumentTypes() {
        return argTypes;
    }

    @Override
    public String getReference() {
        return "it's a user defined function";
    }

    @Override
    public ValueType getResultType() {
        return ValueType.ANY;
    }
    
}
