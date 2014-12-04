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
package com.igormaznitsa.jcp.expression.functions;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * The class implements the str2web function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionSTR2WEB extends AbstractStrConverter {

  @Override
  public String getName() {
    return "str2web";
  }

  @Override
  public Value executeStr(final PreprocessorContext context, final Value value) {
    final String escaped = StringEscapeUtils.escapeHtml3(value.asString());
    
    final StringBuilder result = new StringBuilder(escaped.length()*2);
    for(int i=0;i<escaped.length();i++){
      final char ch = escaped.charAt(i);
      if (CharUtils.isAscii(ch)){
        result.append(ch);
      }else{
        result.append("&#").append(Integer.toString(Character.codePointAt(escaped, i))).append(';');
      }
    }
    
    return Value.valueOf(result.toString());
  }

  @Override
  public String getReference() {
    return "escape string for HTML3";
  }

  @Override
  public ValueType getResultType() {
    return ValueType.STRING;
  }
}
