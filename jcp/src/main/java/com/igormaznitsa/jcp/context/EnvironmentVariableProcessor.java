/*
 * Copyright 2002-2019 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.igormaznitsa.jcp.context;

import com.igormaznitsa.jcp.expression.Value;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * The class allows to get access to environment variables from preprocessor
 * expression, the variables have the "env." prefix and all them are String type
 * All environment variables are allowed for reading and disallowing for writing
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class EnvironmentVariableProcessor implements SpecialVariableProcessor {

  private static final String PREFIX = "env.";
  private final Map<String, Value> environmentVars;

  public EnvironmentVariableProcessor() {
    final Map<String, Value> env = new HashMap<>();

    final Properties properties = System.getProperties();

    for (final String key : properties.stringPropertyNames()) {
      env.put(PREFIX + key.toLowerCase(Locale.ENGLISH).replace(' ', '_'),
          Value.valueOf(properties.getProperty(key)));
    }

    environmentVars = Collections.unmodifiableMap(env);
  }

  @Override


  public String[] getVariableNames() {
    return environmentVars.keySet().toArray(new String[0]);
  }

  @Override
  public Value getVariable(final String varName, final PreprocessorContext context) {
    final Value result = environmentVars.get(varName);
    if (result == null) {
      throw context.makeException("Can't find in environment: " + varName, null);
    }
    return result;
  }

  @Override
  public void setVariable(final String varName, final Value value,
                          final PreprocessorContext context) {
    throw context.makeException("Illegal change of environment record '" + varName +
        "'. Environment records accessible only for reading!", null);
  }
}
