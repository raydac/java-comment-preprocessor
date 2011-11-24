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
package com.igormaznitsa.jcpreprocessor.context;

import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The class allows to get access to environment variables from preprocessor expression, the variables have the "env." prefix and all them are String type
 * All environment variables are allowed for reading and disallowing for writing
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class EnvironmentVariableProcessor implements SpecialVariableProcessor {

    private static final String PREFIX = "env.";
    private final Map<String, Value> environmentVars;

    public EnvironmentVariableProcessor() {
        final Map<String, Value> env = new HashMap<String, Value>();

        final Properties properties = System.getProperties();
        
        for(final String key : properties.stringPropertyNames()){
             env.put(PREFIX + key.toLowerCase().replace(' ', '_'), Value.valueOf(properties.getProperty(key)));
        }
   
        environmentVars = Collections.unmodifiableMap(env);
    }

    @Override
    public String[] getVariableNames() {
        return environmentVars.keySet().toArray(new String[environmentVars.size()]);
    }

    @Override
    public Value getVariable(final String varName, final PreprocessorContext context, final PreprocessingState state) {
        final Value result = environmentVars.get(varName);
        if (result == null) {
            throw new IllegalArgumentException("Request for an unknown environment variable \'"+varName+'\'');
        }
        return result;
    }

    @Override
    public void setVariable(final String varName, final Value value, final PreprocessorContext context) {
        throw new UnsupportedOperationException("Attemption to change an environment variable ["+varName+']');
    }
}
