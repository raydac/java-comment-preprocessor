/* 
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.jcp.directives;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;

/**
 * The class implements //#msg directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class MsgDirectiveHandler extends AbstractDirectiveHandler {

  @Override
  public String getName() {
    return "msg";
  }

  @Override
  public DirectiveArgumentType getArgumentType() {
    return DirectiveArgumentType.TAIL;
  }

  @Override
  public String getReference() {
    return "string tail macroses will be replaced and message will be printed as info";
  }

  @Override
  public AfterDirectiveProcessingBehaviour execute(final String rawTail, final PreprocessorContext context) {
    final String normal = (!rawTail.isEmpty() && Character.isSpaceChar(rawTail.charAt(0))) ? rawTail.substring(1) : rawTail;
    final String message = PreprocessorUtils.processMacroses(normal, context);
    if (context.isVerbose()){
      context.logForVerbose(message);
    }else{
      context.logInfo(message);
    }
    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }
}
