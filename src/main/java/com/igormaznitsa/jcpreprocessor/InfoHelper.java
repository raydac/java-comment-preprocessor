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

import java.util.ArrayList;
import java.util.List;

public abstract class InfoHelper {
    
    public static final String getVersion() {
        return "v5.00";
    }
    
    public static final String getCopyright() {
        return "(C) 2003-2011 All Copyright by Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com)";
    }
    
    public static final String getProductName() {
        return "Java Comment Preprocessor";
    }
    
    public static List<String> getSupportedCommandList() {
        final List<String> result = new ArrayList<String>();
        
        final String DELIMITER  = "-------------------------------------------------";
        
        result.add("com.igormaznitsa.jcpreprocessor.JCPreprocessor [@file_path] [/F:filter_list] [/N] [/I:in_dir] [/O:out_dir] [/P:name=value]");
        result.add("@file_path      - to download variable list from the file");
        result.add("/N              - the output directory will not be cleared before (default it will be cleared) ");
        result.add("/I:in_dir       - the name of the input directory (default \".\\\"");
        result.add("/O:out_dir      - the name of the output directory (default \"..\\preprocessed\")");
        result.add("/P:name=value   - the name and the value of a parameter, ATTENTION: If you want set a string value, you must use symbol '$' instead of '\"\'(\"Hello\"=$Hello$)");
        result.add("/R              - remove all comments from prprocessed sources");
        result.add("/CA             - copy all files, processing only files in the extension list");
        result.add("/F:filter_list  - set the extensions for preprocessed files (default /F:java,txt,html,htm)");
        result.add("/EF:filter_list - set the extensions for excluded files (default /EF:)");
        result.add("");
        result.add("Preprocessor directives");
        result.add(DELIMITER);
        result.add("//#action EXPR0,EXPR1...EXPRn   To generate an action for an outside preprocessor action listener");
        result.add("//#global NAME=EXPR             Definition a global variable");
        result.add("//#local  NAME=EXPR             Definition a local variable");
        result.add("//#define NAME                  Definition a local logical variable as TRUE (you can use it instead of //#local NAME=true)");
        result.add("//#if BOOLEAN_EXPR              The beginning of a #if..#else..#endif block");
        result.add("//#ifdefined VARIABLE_NAME      The beginning of a #ifdefine..#else..#endif block, TRUE if the variable is exists else FALSE");
        result.add("//#else                         Change the flag for current #if construction");
        result.add("//#endif                        To end current #if construction");
        result.add("//#include STRING_EXPR          Include the file from the path what is gotten from the expression");
        result.add("//#excludeif BOOLEAN_EXPR       Exclude the file from the preprocessor output list if the expression is true");
        result.add("//#exit                         Abort current file preprocessing");
        result.add("//#exitif BOOLEAN_EXPR          Abort current file preprocessing if the expression is true");
        result.add("//#while BOOLEAN_EXPR           Start of //#while..//#end construction");
        result.add("//#break                        To break //#while..//#end construction");
        result.add("//#continue                     To continue //#while..//#end construction");
        result.add("//#end                          End of //#while..//#end construction");
        result.add("//#//                           Comment the next string with \"//\" in the output stream");
        result.add("/*-*/                           Give up the tail of the string after the command");
        result.add("/*$EXPR$*/                      Insert the string view of the expression into the output stream");
        result.add("//#-                            Turn off the flag of string outputting");
        result.add("//#+                            Turn on the flag of string outputting");
        result.add("//$                             Output the tail of the string into the output stream without comments");
        result.add("//$$                            Output the tail of the string into the output stream without comments and without processing of /*$..$*/");
        result.add("//#assert                       Output the tail of the string onto the console");
        result.add("//#prefix+                      All strings after the directive will be added in the beginning of generated file");
        result.add("//#prefix-                      Turn off the prefix mode");
        result.add("//#postfix+                     All strings after the directive will be added in the end of generated file");
        result.add("//#postfix-                     Turn off the postfix mode");
        result.add("//#outdir  STRING_EXPR          The destination dir for the file");
        result.add("//#outname  STRING_EXPR         The destination name for the file");
        result.add("//#flush                        To write current text buffer states into the destination file and to clear them.");
        result.add("//#_if BOOLEAN_EXPR             The beginning of a #_if..#_else..#_endif block (works during finding of global variables at sources)");
        result.add("//#_else                        Change the flag for current #_if construction");
        result.add("//#_endif                       To end current #_if construction");
        result.add(DELIMITER);
        result.add("Expression operators:");
        result.add("Arithmetic : *,/,+,%");
        result.add("Logical    : &&,||,!,^ (&&-AND,||-OR,!-NOT,^-XOR)");
        result.add("Comparation: ==,!=,<,>,>=,<=");
        result.add("Brakes     : (,)");
        result.add(DELIMITER);
        result.add("Functions:");
        result.add("FLOAT|INTEGER   abs(FLOAT|INTEGER)              Return the absolute value of an int or float value.");
        result.add("INTEGER         round(FLOAT|INTEGER)            Return the closest int to the argument.");
        result.add("STRING          str2web(STRING)                 Convert a string to a web compatible string.");
        result.add("INTEGER         strlen(STRING)                  Return the length of a string");
        result.add("INTEGER         str2int(STRING)                 Convert a string value to integer");

        result.add("INTEGER xml_open(STRING)                        Open an xml document for the name as the argument, return the ID of the document");
        result.add("INTEGER xml_getDocumentElement(INTEGER)         Return the document element for an opened xml document");
        result.add("INTEGER xml_getElementsForName(INTEGER,STRING)  Return an elemens list");
        result.add("INTEGER xml_elementsNumber(INTEGER)             Return number of elements in the list");
        result.add("INTEGER xml_elementAt(INTEGER)                  Return element for index in the list");
        result.add("STRING  xml_elementName(INTEGER)                Return the name of element");
        result.add("INTEGER xml_getAttribute(INTEGER,STRING)        Return attribute value of an element");
        result.add("INTEGER xml_getElementText(STRING)              Return text value of an element");

        result.add("VALUE   $user_name(EXPR0,EXPR1..EXPRn)          User defined function, it returns variable (undefined type)");
        result.add(DELIMITER);
        result.add("Data types:");
        result.add("BOOLEAN: true,false");
        result.add("INTEGER: 2374,0x56FE (signed 64 bit)");
        result.add("STRING : \"Hello World!\" (or $Hello World!$ for the command string)");
        result.add("FLOAT  : 0.745 (signed 32 bit)");
        
        return result;
    }
}
