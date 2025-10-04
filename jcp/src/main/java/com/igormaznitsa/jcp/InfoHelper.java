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

package com.igormaznitsa.jcp;

import static com.igormaznitsa.jcp.context.JCPSpecialVariableProcessor.getReference;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import com.igormaznitsa.jcp.cmdline.CommandLineHandler;
import com.igormaznitsa.jcp.context.JCPSpecialVariableProcessor;
import com.igormaznitsa.jcp.directives.AbstractDirectiveHandler;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.jcp.expression.functions.AbstractFunction;
import com.igormaznitsa.jcp.expression.operators.AbstractOperator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public final class InfoHelper {

  public static final String DELIMITER = "-------------------------------------------------";
  public static final String SHORT_DELIMITER = "----------------------";

  public static final String VERSION;
  public static final String URL;
  public static final int YEAR;

  static {
    final String path = "/jcpversion.properties";
    try (final InputStream stream = InfoHelper.class.getResourceAsStream(path)) {
      final Properties props = new Properties();
      props.load(stream);
      VERSION = requireNonNull(props.getProperty("version"));
      URL = requireNonNull(props.getProperty("url"));
      YEAR = Integer.parseInt(requireNonNull(props.getProperty("year")).trim());
    } catch (IOException ex) {
      throw new IllegalStateException("Can't read resource: " + path, ex);
    }
  }

  private InfoHelper() {
  }

  public static String getVersion() {
    return "v" + VERSION;
  }


  public static String getCopyright() {
    return "Copyright (C) 2002-" + YEAR + " Igor A. Maznitsa (https://www.igormaznitsa.com)";
  }


  public static String getSite() {
    return "Project page: " + URL;
  }


  public static String getProductName() {
    return "Java Comment Preprocessor";
  }


  public static List<String> makeTextForHelpInfo() {
    final List<String> result = new ArrayList<>();

    result.add(JcpPreprocessor.class.getCanonicalName() + " [@cfgFile] [cliCommands]");
    result.add("");

    result.add("Command line");
    result.add(SHORT_DELIMITER);
    result.add("allowed '/','-' and '--' prefixes, '--' doesn't support multiple commands at once");
    result.add(makeColumns("@cfgFile", "file contains global definition list", 14));
    result.addAll(JcpPreprocessor.getCommandLineHandlers().stream()
        .map(InfoHelper::makeCommandLineKeyReference).collect(toList()));
    result.add(DELIMITER);

    result.add("Directives");
    result.add(SHORT_DELIMITER);
    for (final AbstractDirectiveHandler handler : AbstractDirectiveHandler.findAllDirectives()) {
      result.add(makeDirectiveReference(handler));
    }
    result.add(DELIMITER);
    result.add("Special directives");
    result.add(SHORT_DELIMITER);
    result
        .add(makeSpecialDirectiveReference("//$", "uncomment and process all following macroses"));
    result.add(makeSpecialDirectiveReference("//$$", "like //$ but macroses ignored"));
    result.add(makeSpecialDirectiveReference("/*-*/", "truncate line"));

    result.add("Operators");
    result.add(SHORT_DELIMITER);
    for (final AbstractOperator handler : AbstractOperator.getAllOperators()) {
      result.add(makeOperatorReference(handler));
    }
    result.add(DELIMITER);
    result.add("Functions");
    result.add(SHORT_DELIMITER);
    for (final AbstractFunction handler : AbstractFunction.findAllFunctions()) {
      result.add(makeFunctionReference(handler));
    }
    result.add(DELIMITER);
    result.add("Allowed types");
    result.add(SHORT_DELIMITER);
    result.add(" BOOL: true,false");
    result.add("  INT: 2374,0x56FE (signed 64 bit)");
    result.add("  STR: \"Hello World!\" (or $Hello World!$ if in CLI)");
    result.add("FLOAT: 0.745 (signed 32 bit)");
    result.add(DELIMITER);
    result.add("Special variables");
    result.add(SHORT_DELIMITER);
    result.addAll(
        getReference().stream().map(InfoHelper::makeSpecialVariableReference).collect(toList()));
    return result;
  }


  private static String makeSpecialVariableReference(
      final JCPSpecialVariableProcessor.NameReferencePair p) {
    final String name = p.getName();
    final String ref = p.getReference();
    return makeColumns(name, ref, 24);
  }


  private static String makeCommandLineKeyReference(final CommandLineHandler handler) {
    return makeColumns(handler.getKeyName(), handler.getDescription(), 14);
  }


  private static String makeDirectiveReference(final AbstractDirectiveHandler directive) {
    final StringBuilder activityPasses = new StringBuilder();
    int i = 0;
    if (directive.isGlobalPhaseAllowed()) {
      i++;
      activityPasses.append("1st");
    }
    if (directive.isPreprocessingPhaseAllowed()) {
      if (i > 0) {
        activityPasses.append(',');
      }
      activityPasses.append("2th");
      i++;
    }
    activityPasses.append(i > 1 ? "passes" : " pass");

    final String directiveName = directive.getFullName();
    final String descr =
        (directive.isDeprecated() ? "{DEPRECATED} " : "") + directive.getReference() + " (" +
            activityPasses + ')';
    return makeColumns(directiveName, descr, 16);
  }


  private static String makeSpecialDirectiveReference(final String name, final String reference) {
    return makeColumns(name, reference, 14);
  }


  private static String makeOperatorReference(final AbstractOperator operator) {
    return makeColumns(operator.getKeyword(), operator.getReference(), 14);
  }


  private static String makeFunctionReference(final AbstractFunction func) {
    final String funcName = func.getName();
    final String description = func.getReference();

    final StringBuilder variants = new StringBuilder("  [");
    final String result = func.getResultType().getSignature().toUpperCase(Locale.ROOT);

    int variantIndex = 0;
    for (final List<ValueType> signature : func.getAllowedArgumentTypes()) {
      if (variantIndex > 0) {
        variants.append(" | ");
      }
      variants.append(result).append(' ').append(funcName).append(" (");
      for (int i = 0; i < signature.size(); i++) {
        if (i > 0) {
          variants.append(',');
        }
        variants.append(signature.get(i).getSignature().toUpperCase(Locale.ROOT));
      }
      variants.append(')');
      variantIndex++;
    }
    variants.append(']');
    return makeColumns(funcName, description, 24) + variants;
  }


  private static String makeColumns(final String name, final String reference,
                                    final int firstColumnWidth) {
    final int spaces = firstColumnWidth - name.length();
    final StringBuilder result = new StringBuilder(name);
    result.append(" ".repeat(Math.max(0, spaces)));
    result.append(reference);
    return result.toString();
  }
}
