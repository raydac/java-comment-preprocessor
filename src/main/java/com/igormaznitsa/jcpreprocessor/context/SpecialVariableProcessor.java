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
 */package com.igormaznitsa.jcpreprocessor.context;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.expression.Value;

/**
  * The interface describes a special variable processor which will be called for variables met by a preprocessor in expressions
  * 
  * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
  */
public interface SpecialVariableProcessor {
    /**
     * Get all variable names allowed by the processor as an array, all names must be in lower case
     * @return allowed variable names as a String array
     */
    String[] getVariableNames();

    /**
     * Get the value for the variable
     * @param varName the variable name, must not be null
     * @param context the preprocessor context, it can be null
     * @param state the preprocessor state, it can be null
     * @return the value, it must not return null because it will notified preprocessor that it supports the variable
     */
    Value getVariable(String varName, PreprocessorContext context, PreprocessingState state);

    /**
     * Set a value to the variable
     * @param varName the variable name, must not be null
     * @param value the value to be set to the variable, must not be null
     * @param context the preprocessor context, it can be null
     * @param state the preprocessor state, it can be null
     */
    void setVariable(String varName, Value value, PreprocessorContext context, PreprocessingState state);
    
}
