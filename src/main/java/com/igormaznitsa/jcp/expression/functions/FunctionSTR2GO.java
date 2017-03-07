/* 
 * Copyright 2017 Igor Maznitsa (http://www.igormaznitsa.com).
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

import javax.annotation.Nonnull;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;

import com.igormaznitsa.meta.annotation.MustNotContainNull;

/**
 * The class implements escape function handler to escape strings to be used in Go.
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionSTR2GO extends AbstractFunction {

  private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING, ValueType.BOOLEAN}};

  @Override
  @Nonnull
  public String getName() {
    return "str2go";
  }

  @Nonnull
  public Value executeStrBool(@Nonnull final PreprocessorContext context, @Nonnull final Value source, @Nonnull final Value splitAndQuoteLines) {
    if (splitAndQuoteLines.asBoolean()){
      final boolean endsWithNextLine = source.asString().endsWith("\n");
      final String [] splitted = source.asString().split("\\n");
      final StringBuilder result = new StringBuilder(source.asString().length()*2);
      final String nextLineChars = PreprocessorUtils.getNextLineCodes();
      
      int index = 0;
      for(final String s : splitted){
        final boolean last = ++index == splitted.length;
        if (result.length()>0){
          result.append(nextLineChars).append('+');
        }
        result.append('\"').append(escapeGo(s));
        if (last ){
          result.append(endsWithNextLine ? "\\n\"":"\"");
        }else{
          result.append("\\n\"");
        }
      }
      return Value.valueOf(result.toString());
    }else{
      return Value.valueOf(escapeGo(source.asString()));
    }
  }

  @Nonnull
  private static String toUnicode(final char c) {
    final StringBuilder result = new StringBuilder(4);
    final String hex = Integer.toHexString(c);
    
    for(int i=0;i<4-hex.length();i++){
      result.append('0');
    }
    
    result.append(hex);
    
    return result.toString();
  }
  
  @Nonnull
  private static String escapeGo(@Nonnull final String value) {
    final StringBuilder result = new StringBuilder();
    
    for(final char c : value.toCharArray()) {
      switch(c) {
        case '\u0007' : result.append("\\a");break;
        case '\u000b' : result.append("\\v");break;
        case '\b' : result.append("\\b");break;
        case '\f' : result.append("\\f");break;
        case '\n' : result.append("\\n");break;
        case '\r' : result.append("\\r");break;
        case '\t' : result.append("\\t");break;
        case '\\' : result.append("\\\\");break;
        case '\'' : result.append("\\\'");break;
        case '\"' : result.append("\\\"");break;
        case ' ' : result.append(" ");break;
        default: {
          if (Character.isISOControl(c) || Character.isWhitespace(c) || c > 0xFF){
            result.append("\\u").append(toUnicode(c));
          } else {
            result.append(c);
          }
        }break;
      }
    }
    
    return result.toString();
  }
  
  @Override
  public int getArity() {
    return 2;
  }

  @Override
  @Nonnull
  @MustNotContainNull
  public ValueType[][] getAllowedArgumentTypes() {
    return ARG_TYPES;
  }

  @Override
  @Nonnull
  public String getReference() {
    return "escapes a string to be compatible with  GoLang";
  }

  @Override
  @Nonnull
  public ValueType getResultType() {
    return ValueType.STRING;
  }

}
