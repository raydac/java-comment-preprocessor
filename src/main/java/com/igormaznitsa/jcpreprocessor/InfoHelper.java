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
package com.igormaznitsa.jcpreprocessor;

import com.igormaznitsa.jcpreprocessor.cmdline.CommandLineHandler;
import com.igormaznitsa.jcpreprocessor.directives.AbstractDirectiveHandler;
import com.igormaznitsa.jcpreprocessor.expression.functions.AbstractFunction;
import com.igormaznitsa.jcpreprocessor.expression.operators.AbstractOperator;
import java.util.ArrayList;
import java.util.List;

public abstract class InfoHelper {

    public static final String DELIMITER = "-------------------------------------------------";

    public static final String getVersion() {
        return "v5.00";
    }

    public static final String getCopyright() {
        return "(C) 2003-2011 All Copyright by Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com)";
    }

    public static final String getProductName() {
        return "Java Comment Preprocessor";
    }

    public static List<String> makeTextForHelpInfo() {
        final List<String> result = new ArrayList<String>();

        result.add("com.igormaznitsa.jcpreprocessor.JCPreprocessor [@file_path] command line directives");
        result.add("");

        result.add("Command line directives\n------------");
        result.add(makeColumns("@file_path","to download variable list from the file"));
        for (final CommandLineHandler handler : JCPreprocessor.COMMAND_LINE_PROCESSORS) {
            result.add(makeCommandLineKeyReference(handler));
        }
        result.add(DELIMITER);

        result.add("Directives\n------------");
        for (final AbstractDirectiveHandler handler : AbstractDirectiveHandler.DIRECTIVES) {
            result.add(makeDirectiveReference(handler));
        }
        result.add(DELIMITER);

        result.add("Operators\n------------");
        for (final AbstractOperator handler : AbstractOperator.ALL_OPERATORS) {
            result.add(makeOperatorReference(handler));
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
        return makeColumns(key, descr);
    }

    private static String makeDirectiveReference(final AbstractDirectiveHandler directive) {
        final String directiveName = directive.getFullName();
        final String descr = directive.getReference();
        return makeColumns(directiveName, descr);
    }

    private static String makeOperatorReference(final AbstractOperator operator) {
        final String operatorName = operator.getKeyword();
        final String descr = operator.getReference();
        return makeColumns(operatorName, descr);
    }

    private static String makeFunctionReference(final AbstractFunction func) {
        final String funcName = func.getName();
        final String descr = func.getReference();
        return makeColumns(funcName, descr);
    }

    private static String makeColumns(final String name, final String reference) {
        final int spaces = 14 - name.length();
        final StringBuilder result = new StringBuilder(name);
        for (int i = 0; i < spaces; i++) {
            result.append(' ');
        }
        result.append(reference);
        return result.toString();
    }
}
