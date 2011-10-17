package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.cfg.Configurator;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;

public final class FunctionDefinedByUser extends AbstractFunction {

    private final String name;
    private final int argsNumber;
    private final Configurator configurator;

    public FunctionDefinedByUser(final String name, final int argsNumber, final Configurator cfg) {
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

    public void execute(Expression stack, int index) {
        Value[] ap_values = new Value[argsNumber];

        int i_arg = argsNumber;
        while (i_arg > 0) {
            try {
                if (stack.isEmpty()) {
                    throw new Exception();
                }
                Object p_obj = stack.getItemAtPosition(index - 1);
                index--;
                stack.removeItemAt(index);

                if (p_obj instanceof Value) {
                    ap_values[i_arg - 1] = (Value) p_obj;
                } else {
                    throw new Exception();
                }
            } catch (Exception _ex) {
                throw new RuntimeException("You have wrong arguments number for \"" + name + "\" function, must be " + argsNumber);
            }

            i_arg--;
        }

        Value p_value = configurator.getPreprocessorExtension().processUserFunction(name, ap_values);
        if (p_value == null) {
            throw new RuntimeException("User defined function \"" + name + "\" has returned NULL");
        }
        stack.setItemAtPosition(index, p_value);

    }
}
