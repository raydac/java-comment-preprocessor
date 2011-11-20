/*
 * Copyright 2011 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307  USA
 */
package com.igormaznitsa.jcpreprocessor.extension;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.expression.Value;

/**
 * The interface describes an extension which can be connected to a preprocessor and to be notified about some calls and actions
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public interface PreprocessorExtension
{
    /**
     * To process an action (it will be called if the preprocessor is met //#action directive)
     * @param parameters the parameters of the action directive, must not be null
     * @param state the preprocessing state of the preprocessor, must not be null
     * @return true if the action has been processed successfully or false, if it is false then exception will be thrown and preprocessing will be stopped
     */
    public boolean processAction(Value [] parameters, PreprocessingState state);

    /**
     * Call to process a user function (the function starts with $)
     * @param functionName the name of the function (without $ and in low case), must not be null
     * @param arguments the function arguments as an array, must not be null
     * @return a calculated value, it must not be null
     */
    public Value processUserFunction(String functionName,Value [] arguments);

    /**
     * When a preprocessor meets a user defined function (the function starts with $) then it will ask for its arity
     * @param functionName the function name without $ and in low case, must not be null
     * @return the function arity (the argument number), zero or a great value
     */
    public int getUserFunctionArity(String functionName);
}
