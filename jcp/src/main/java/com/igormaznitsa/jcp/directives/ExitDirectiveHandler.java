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

package com.igormaznitsa.jcp.directives;

import com.igormaznitsa.jcp.containers.PreprocessingFlag;
import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;

/**
 * The class implements the //#exit directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class ExitDirectiveHandler extends AbstractDirectiveHandler {

  @Override

  public String getName() {
    return "exit";
  }

  @Override

  public String getReference() {
    return "immediately end current file preprocessing, return to the calling file if presented";
  }

  @Override

  public AfterDirectiveProcessingBehaviour execute(final String string,
                                                   final PreprocessorContext context) {
    final PreprocessingState state = context.getPreprocessingState();
    state.getPreprocessingFlags().add(PreprocessingFlag.END_PROCESSING);
    if (context.isVerbose()) {
      context.logForVerbose("Detected " + getFullName());
    }
    return AfterDirectiveProcessingBehaviour.READ_NEXT_LINE;
  }
}
