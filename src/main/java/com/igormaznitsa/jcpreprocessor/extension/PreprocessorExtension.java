package com.igormaznitsa.jcpreprocessor.extension;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.expression.Value;

public interface PreprocessorExtension
{
    /**
     * Processing of an action
     */
     public boolean processUserDirective(Value [] parameters, ParameterContainer container);

    /**
     * Processing of an user function
     */
    public Value processUserFunction(String name,Value [] arguments);

    /**
     * Check the function arity for a function name
     */
    public int getUserFunctionArity(String name);
}
