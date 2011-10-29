package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;

public final class FunctionDefinedByUser extends AbstractFunction {

    private final String name;
    private final int argsNumber;
    private final PreprocessorContext configurator;

    public FunctionDefinedByUser(final String name, final int argsNumber, final PreprocessorContext cfg) {
        super();
        if (name == null) {
            throw new NullPointerException("Name is null");
        }

        if (argsNumber < 0) {
            throw new IllegalArgumentException("Argument number is less than zero");
        }

        if (cfg == null) {
            throw new NullPointerException("Configurator is null");
        }

        this.name = name;
        this.argsNumber = argsNumber;
        this.configurator = cfg;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getArity() {
        return argsNumber;
    }

    public void execute(final PreprocessorContext context, final Expression stack, final int indx) {
        final Value[] values = new Value[argsNumber];

        int index = indx;
        
        int counter = argsNumber;
        while (counter > 0) {
            try {
                if (stack.isEmpty()) {
                    throw new Exception();
                }
                final Object item = stack.getItemAtPosition(index - 1);
                index--;
                stack.removeItemAt(index);

                if (item instanceof Value) {
                    values[counter - 1] = (Value) item;
                } else {
                    throw new Exception();
                }
            } catch (Exception _ex) {
                throw new RuntimeException("You have wrong arguments number for \"" + name + "\" function, must be " + argsNumber);
            }

            counter--;
        }

        final Value value = configurator.getPreprocessorExtension().processUserFunction(name, values);
        if (value == null) {
            throw new RuntimeException("User defined function \"" + name + "\" has returned NULL");
        }
        stack.setItemAtPosition(index, value);

    }
}
