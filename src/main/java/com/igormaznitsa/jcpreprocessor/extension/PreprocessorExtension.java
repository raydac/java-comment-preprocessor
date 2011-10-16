package com.igormaznitsa.jcpreprocessor.extension;

import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.PrintStream;
import java.io.IOException;

/**
 * The interface of an preprocessor action listener (//#action directive)
 * @author  Igor Maznitsa
 * @version 1.00
 */
public interface PreprocessorExtension
{
    /**
     * Processing of an action
     * @param _actionParameters the actions array
     * @param _outDir destination directory for the file
     * @param _outName  destination file name for the file
     * @param _mainOutputStream  the stream for printing of strings into the body of the file
     * @param _prefixOutputStream the stream for printing of strings into the prefix of the file
     * @param _postfixOutputStream the stream for printing of strings into the postfix of the file
     * @param _infoStream the stream for printing of information into the console
     * @return true if success and false if the preprocessing must be stopped
     * @throws IOException
     */
    public boolean processAction(Value [] _actionParameters,String _outDir,String _outName,PrintStream _mainOutputStream,PrintStream _prefixOutputStream,PrintStream _postfixOutputStream,PrintStream _infoStream);

    /**
     * Processing of an user function
     * @param _functionName the name of a function
     * @param _arguments the arguments array
     * @return result of calculations as Value
     * @throws IOException
     */
    public Value processUserFunction(String _functionName,Value [] _arguments);

    /**
     * Check number of arguments for an user function
     * @param _name the name of the function
     * @return 0 or more if function presents and known by listener and -1 if it is an unknown function
     * @throws IOException
     */
    public int getArgumentsNumberForUserFunction(String _name);
}
