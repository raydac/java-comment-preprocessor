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
package com.igormaznitsa.jcp;

import com.igormaznitsa.jcp.cmdline.CommandLineHandler;
import com.igormaznitsa.jcp.directives.AbstractDirectiveHandler;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.jcp.expression.functions.AbstractFunction;
import com.igormaznitsa.jcp.expression.operators.AbstractOperator;
import java.util.*;

public final class InfoHelper {

  private InfoHelper() {
  }

  public static final String DELIMITER = "-------------------------------------------------";

  public static String getVersion() {
    return "v5.3.3";
  }

  public static String getCopyright() {
    return "2003-2014 Author: Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com)";
  }

  public static String getProductName() {
    return "Java Comment Preprocessor";
  }

  public static List<String> makeTextForHelpInfo() {
    final List<String> result = new ArrayList<String>();

    result.add(JCPreprocessor.class.getCanonicalName() + " [@file_path] [cli_directives]");
    result.add("");

    result.add("Command line directives\n------------");
    result.add("\n(!)Historically all directives are prefixed by '/' but since 5.5.3 both '-' and '--' prefixes allowed\n");
    result.add(makeColumns("@file_path", "to download variable list from the file", 14));
    for (final CommandLineHandler handler : JCPreprocessor.getCommandLineHandlers()) {
      result.add(makeCommandLineKeyReference(handler));
    }
    result.add(DELIMITER);

    result.add("Preprocessor directives\n------------");
    for (final AbstractDirectiveHandler handler : AbstractDirectiveHandler.DIRECTIVES) {
      result.add(makeDirectiveReference(handler));
    }
    result.add(DELIMITER);
    result.add("Special string directives\n------------");
    result.add(makeSpecialDirectiveReference("//$", "it processes macroses inside the string rest and plac the string result without the comment prefix into the output stream"));
    result.add(makeSpecialDirectiveReference("//$$", "it works like //$ but it doesn't process macroses inside the string"));
    result.add(makeSpecialDirectiveReference("/*-*/", "it gets rid of the string tail after the directive (the directive will be removed too)"));

    result.add("Operators\n------------");
    for (final AbstractOperator handler : AbstractOperator.ALL_OPERATORS) {
      result.add(makeOperatorReference(handler));
    }
    result.add(DELIMITER);
    result.add("Functions\n------------");
    for (final AbstractFunction handler : AbstractFunction.ALL_FUNCTIONS) {
      result.add(makeFunctionReference(handler));
    }
    result.add(DELIMITER);
    result.add("Data types\n------------");
    result.add("BOOLEAN: true,false");
    result.add("INTEGER: 2374,0x56FE (signed 64 bit)");
    result.add("STRING : \"Hello World!\" (or $Hello World!$ for the command string)");
    result.add("FLOAT  : 0.745 (signed 32 bit)");

    return result;
  }

  private static String makeCommandLineKeyReference(final CommandLineHandler handler) {
    final String key = handler.getKeyName();
    final String descr = handler.getDescription();
    return makeColumns(key, descr, 14);
  }

  private static String makeDirectiveReference(final AbstractDirectiveHandler directive) {
    final String directiveName = directive.getFullName();
    final String descr = directive.getReference();
    return makeColumns(directiveName, descr, 14);
  }

  private static String makeSpecialDirectiveReference(final String name, final String reference) {
    return makeColumns(name, reference, 14);
  }

  private static String makeOperatorReference(final AbstractOperator operator) {
    final String operatorName = operator.getKeyword();
    final String descr = operator.getReference();
    return makeColumns(operatorName, descr, 14);
  }

  private static String makeFunctionReference(final AbstractFunction func) {
    final String funcName = func.getName();
    final String descr = func.getReference();

    final StringBuilder variants = new StringBuilder("  [");
    final String result = func.getResultType().getSignature().toUpperCase(Locale.ENGLISH);

    int variantIndex = 0;
    for (ValueType[] signature : func.getAllowedArgumentTypes()) {
      if (variantIndex > 0) {
        variants.append(" | ");
      }
      variants.append(result).append(' ').append(funcName).append(" (");
      for (int i = 0; i < signature.length; i++) {
        if (i > 0) {
          variants.append(',');
        }
        variants.append(signature[i].getSignature().toUpperCase());
      }
      variants.append(')');
      variantIndex++;
    }
    variants.append(']');
    return makeColumns(funcName, descr, 24) + variants.toString();
  }

  private static String makeColumns(final String name, final String reference, final int firstColumnWidth) {
    final int spaces = firstColumnWidth - name.length();
    final StringBuilder result = new StringBuilder(name);
    for (int i = 0; i < spaces; i++) {
      result.append(' ');
    }
    result.append(reference);
    return result.toString();
  }
}
