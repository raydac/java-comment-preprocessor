package com.igormaznitsa.jcpreprocessor.references;

import com.igormaznitsa.jcpreprocessor.cfg.Configurator;
import com.igormaznitsa.jcpreprocessor.directives.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.removers.JavaCommentsRemover;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItem;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemType;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class FileReference {
    
    private final File sourceFile;
    private final boolean onlyForCopy;

    private boolean excluded;
    private String destinationDir;
    private String destinationName;
    
    public File getSourceFile() {
        return sourceFile;
    }
    
    public boolean isExcluded() {
        return excluded;
    }
    
    public boolean isOnlyForCopy() {
        return onlyForCopy;
    }
    
    public String getDestinationDir() {
        return destinationDir;
    }
    
    public String getDestinationName() {
        return destinationName;
    }
    
    public FileReference(final File srcFile, final String dstFileName, final boolean copingOnly) {
        onlyForCopy = copingOnly;
        excluded = false;
        sourceFile = srcFile;
        
        int dirSeparator = dstFileName.lastIndexOf('/');
        if (dirSeparator < 0) {
            dirSeparator = dstFileName.lastIndexOf('\\');
        }
        
        if (dirSeparator < 0) {
            destinationDir = "." + File.separatorChar;
            destinationName = dstFileName;
        } else {
            destinationDir = dstFileName.substring(0, dirSeparator);
            destinationName = dstFileName.substring(dirSeparator);
        }
    }
    
    public String getDestinationFilePath() {
        return destinationDir + File.separatorChar + destinationName;
    }
    
    @Override
    public String toString() {
        return sourceFile.getAbsolutePath();
    }
    
    private boolean isGlobalOperation(final String str) {
        return str.startsWith("//#_if") || str.startsWith("//#_else") || str.startsWith("//#_endif") || str.startsWith("//#global") || str.startsWith("//#exclude");
    }
    
    public void preprocess(final Configurator configurator) throws IOException {
        configurator.clearLocalVariables();
        
        ParameterContainer paramContainer = new ParameterContainer(getSourceFile());
        paramContainer.setStrings(PreprocessorUtils.readTextFileAndAddNullAtEnd(getSourceFile(), configurator.getCharacterEncoding()));
        
        final LinkedList<IncludeReference> includeReferenceStack = new LinkedList<IncludeReference>();
        File preprocessingFile = getSourceFile();
        
        final ByteArrayOutputStream normalDataBuffer = new ByteArrayOutputStream(64000);
        final ByteArrayOutputStream prefixDataBuffer = new ByteArrayOutputStream(1024);
        final ByteArrayOutputStream postfixDataBuffer = new ByteArrayOutputStream(1024);
        
        PrintStream normalTextOutStream = new PrintStream(normalDataBuffer,false, configurator.getCharacterEncoding());
        PrintStream prefixTextOutStream = new PrintStream(prefixDataBuffer, false, configurator.getCharacterEncoding());
        PrintStream postfixTextOutStream = new PrintStream(postfixDataBuffer, false, configurator.getCharacterEncoding());
        
        PrintStream currentTextOutStream = normalTextOutStream;
        
        final LinkedList<String> fileNameStack = new LinkedList<String>();
        fileNameStack.add(paramContainer.getCurrentFileCanonicalPath());
        
        final LinkedList<Integer> whileIndexesStack = new LinkedList<Integer>();
        
        try {
            while (true) {
                String nonTrimmedProcessingString = paramContainer.nextLine();                
                if (paramContainer.shouldEndPreprocessing()) {
                    nonTrimmedProcessingString = null;
                }
                
                if (nonTrimmedProcessingString == null) {
                    if (!includeReferenceStack.isEmpty()) {
                        IncludeReference p_inRef = includeReferenceStack.pop();
                        preprocessingFile = p_inRef.getFile();
                        paramContainer.setStrings(p_inRef.getStrings()).setCurrentStringIndex(p_inRef.getStringCounter()).setEndPreprocessing(false);
                        fileNameStack.pop();
                        paramContainer.setCurrentFileCanonicalPath(fileNameStack.getFirst());
                        continue;
                    } else {
                        break;
                    }
                }
                
                final String trimmedProcessingString = nonTrimmedProcessingString.trim();
                
                final int numberOfSpacesAtTheLineBeginning = nonTrimmedProcessingString.indexOf(trimmedProcessingString);
                
                boolean processingEnabled = paramContainer.isThereNoBreakCommand() && paramContainer.isIfEnabled() &&  paramContainer.isThereNoContinueCommand();
                
                String stringToBeProcessed;
                
                if (processingEnabled) {
                    stringToBeProcessed = PreprocessorUtils.processMacros(getSourceFile(), trimmedProcessingString, configurator);
                } else {
                    stringToBeProcessed = trimmedProcessingString;
                }
                
                if (isGlobalOperation(stringToBeProcessed)) {
                    continue;
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#local")) {
                    // Processing of a local variable definition
                    stringToBeProcessed = PreprocessorUtils.extractTrimmedTail("//#local", stringToBeProcessed);
                    processLocalDefinition(stringToBeProcessed, configurator);
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#define")) {
                    // Processing of a local definition
                    final String name = PreprocessorUtils.extractTrimmedTail("//#define",stringToBeProcessed);
                    final Value value = Value.BOOLEAN_TRUE;
                    configurator.setLocalVariable(name, value);
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#exitif")) {
                    // To end processing the file processing immediatly if the value is true
                    stringToBeProcessed = PreprocessorUtils.extractTrimmedTail("//#exitif", stringToBeProcessed);
                    final Value condition = Expression.eval(stringToBeProcessed);
                    if (condition == null || condition.getType() != ValueType.BOOLEAN) {
                        throw new IOException("You must use a boolean argument for an #endif operator");
                    }
                    if (((Boolean) condition.getValue()).booleanValue()) {
                        paramContainer.setEndPreprocessing(true);
                        continue;
                    }
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#exit")) {
                    // To end processing the file immediatly
                    paramContainer.setEndPreprocessing(true);
                    continue;
                } else if (stringToBeProcessed.startsWith("//#continue")) {
                    if (paramContainer.isWhileCounterZero()) {
                        throw new IOException("You have #continue without #when");
                    }
                    if (processingEnabled && paramContainer.getWhileCounter() == paramContainer.getActiveWhileCounter()) {
                        paramContainer.setThereIsNoContinueCommand(false);
                    }
                } else if (stringToBeProcessed.startsWith("//#break")) {
                    if (paramContainer.isWhileCounterZero()) {
                        throw new IOException("You have #break without #when");
                    }
                    
                    if (processingEnabled && paramContainer.getWhileCounter() == paramContainer.getActiveWhileCounter()) {
                        paramContainer.setThereIsNoBreakCommand(false);
                    }
                } else if (stringToBeProcessed.startsWith("//#while")) {
                    // To end processing the file immediatly
                    if (processingEnabled) {
                        stringToBeProcessed = stringToBeProcessed.substring(8).trim();
                        Value p_value = Expression.eval(stringToBeProcessed);
                        if (p_value == null || p_value.getType() != ValueType.BOOLEAN) {
                            throw new IOException("You don't have a boolean result in the #while instruction");
                        }
                        if (paramContainer.isWhileCounterZero()) {
                            paramContainer.setLastWhileFileName(paramContainer.getCurrentFileCanonicalPath());
                            paramContainer.setLastWhileStringNumber(paramContainer.getCurrentStringIndex());
                        }
                        paramContainer.increaseWhileCounter();
                        paramContainer.setActiveWhileCounter(paramContainer.getWhileCounter());
                        
                        if (((Boolean) p_value.getValue()).booleanValue()) {
                            paramContainer.setThereIsNoBreakCommand(true);
                        } else {
                            paramContainer.setThereIsNoBreakCommand(false);
                        }
                    } else {
                        paramContainer.increaseWhileCounter();
                    }
                    
                    whileIndexesStack.push(new Integer(paramContainer.getCurrentStringIndex() - 1));
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#prefix+")) {
                    currentTextOutStream = prefixTextOutStream;
                    continue;
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#prefix-")) {
                    currentTextOutStream = normalTextOutStream;
                    continue;
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#postfix+")) {
                    currentTextOutStream = postfixTextOutStream;
                    continue;
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#postfix-")) {
                    currentTextOutStream = normalTextOutStream;
                    continue;
                } else if (stringToBeProcessed.startsWith("//#ifdefined")) {
                    // Processing #ifdefine instruction
                    if (processingEnabled) {
                        stringToBeProcessed = stringToBeProcessed.substring(12).trim();
                        
                        if (stringToBeProcessed.isEmpty()) {
                            throw new IOException("You have not defined any variable in a //#ifdefined deirective");
                        }
                        
                        boolean lg_defined = configurator.findVariableForName(stringToBeProcessed)!=null;
                        
                        if (paramContainer.isIfCounterZero()) {
                            paramContainer.setLastIfFileName(paramContainer.getCurrentFileCanonicalPath());
                            paramContainer.setLastIfStringNumber(paramContainer.getCurrentStringIndex());
                        }
                        paramContainer.increaseIfCounter();
                        paramContainer.setActiveIfCounter(paramContainer.getIfCounter());
                        
                        if (lg_defined) {
                            paramContainer.setIfEnabled(true);
                        } else {
                            paramContainer.setIfEnabled(false);
                        }
                    } else {
                        paramContainer.increaseIfCounter();
                    }
                } else if (stringToBeProcessed.startsWith("//#if")) {
                    // Processing #if instruction
                    if (processingEnabled) {
                        stringToBeProcessed = stringToBeProcessed.substring(5).trim();
                        Value p_value = Expression.eval(stringToBeProcessed);
                        if (p_value == null || p_value.getType() != ValueType.BOOLEAN) {
                            throw new IOException("You don't have a boolean result in the #if instruction");
                        }
                        if (paramContainer.isIfCounterZero()) {
                            paramContainer.setLastIfFileName(paramContainer.getCurrentFileCanonicalPath());
                            paramContainer.setLastIfStringNumber(paramContainer.getCurrentStringIndex());
                        }
                        paramContainer.increaseIfCounter();
                        paramContainer.setActiveIfCounter(paramContainer.getIfCounter());
                        
                        if (((Boolean) p_value.getValue()).booleanValue()) {
                            paramContainer.setIfEnabled(true);
                        } else {
                            paramContainer.setIfEnabled(false);
                        }
                    } else {
                        paramContainer.increaseIfCounter();
                    }
                } else if (stringToBeProcessed.startsWith("//#else")) {
                    if (paramContainer.isIfCounterZero()) {
                        throw new IOException("You have got an #else instruction without #if");
                    }
                    
                    if (paramContainer.getIfCounter() == paramContainer.getActiveIfCounter()) {
                        paramContainer.setIfEnabled(!paramContainer.isIfEnabled());
                    }
                } else if (stringToBeProcessed.startsWith("//#outdir")) {
                    if (processingEnabled) {
                        try {
                            stringToBeProcessed = stringToBeProcessed.substring(9).trim();
                            Value p_value = Expression.eval(stringToBeProcessed);
                            
                            if (p_value == null || p_value.getType() != ValueType.STRING) {
                                throw new IOException("non string expression");
                            }
                            stringToBeProcessed = (String) p_value.getValue();
                            setDestinationDir(stringToBeProcessed);
                        } catch (IOException e) {
                            throw new IOException("You have the error in the #outdir instruction in the file " + getSourceFile().getAbsolutePath() + " at line: " + paramContainer.getCurrentStringIndex() + " [" + e.getMessage() + ']');
                        }
                    }
                } else if (stringToBeProcessed.startsWith("//#outname")) {
                    if (processingEnabled) {
                        try {
                            stringToBeProcessed = stringToBeProcessed.substring(10).trim();
                            Value p_value = Expression.eval(stringToBeProcessed);
                            
                            if (p_value == null || p_value.getType() != ValueType.STRING) {
                                throw new IOException("non string expression");
                            }
                            stringToBeProcessed = (String) p_value.getValue();
                            setDestinationName(stringToBeProcessed);
                        } catch (IOException e) {
                            throw new IOException("You have the error in the #outname instruction in the file " + getSourceFile().getAbsolutePath() + " line: " + paramContainer.getCurrentStringIndex() + " [" + e.getMessage() + ']');
                        }
                    }
                } else if (stringToBeProcessed.startsWith("//#flush")) {
                    if (processingEnabled) {
                        try {
                            normalTextOutStream.flush();
                            normalTextOutStream.close();
                            
                            postfixTextOutStream.flush();
                            postfixTextOutStream.close();
                            
                            prefixTextOutStream.flush();
                            prefixTextOutStream.close();
                            
                            
                            if (prefixDataBuffer.size() != 0 || postfixDataBuffer.size() != 0 || normalDataBuffer.size() != 0) {
                                final File outFile = new File(configurator.getDestinationDirectoryAsFile(), getDestinationFilePath());
                                if (!outFile.getParentFile().exists() && !outFile.getParentFile().mkdirs())
                                {
                                    throw new IOException("Can't create directories ["+outFile.getCanonicalPath()+']');
                                }
                                final FileOutputStream outFileStream = new FileOutputStream(outFile);
                                
                                if (prefixDataBuffer.size() != 0) {
                                    outFileStream.write(prefixDataBuffer.toByteArray());
                                    outFileStream.flush();
                                }
                                
                                outFileStream.write(normalDataBuffer.toByteArray());
                                outFileStream.flush();
                                
                                if (postfixDataBuffer.size() != 0) {
                                    outFileStream.write(postfixDataBuffer.toByteArray());
                                    outFileStream.flush();
                                }
                                
                                outFileStream.close();
                                
                                normalDataBuffer.reset();
                                prefixDataBuffer.reset();
                                postfixDataBuffer.reset();
                                
                                normalTextOutStream = new PrintStream(normalDataBuffer);
                                prefixTextOutStream = new PrintStream(prefixDataBuffer);
                                postfixTextOutStream = new PrintStream(postfixDataBuffer);
                                
                                currentTextOutStream = normalTextOutStream;
                            }
                        } catch (IOException e) {
                            throw new IOException("Exception during //#flush operator in the file " + getSourceFile().getCanonicalPath() + " at line: " + paramContainer.getCurrentStringIndex() + " [" + e.getMessage() + ']',e);
                        }
                    }
                } else if (stringToBeProcessed.startsWith("//#endif")) {
                    if (paramContainer.isIfCounterZero()) {
                        throw new IOException("You have got an #endif instruction without #if");
                    }
                    
                    if (paramContainer.getIfCounter() == paramContainer.getActiveIfCounter()) {
                        paramContainer.decreaseIfCounter();
                        paramContainer.decreaseActiveIfCounter();
                        paramContainer.setIfEnabled(true);
                    } else {
                        paramContainer.decreaseIfCounter();
                    }
                } else if (stringToBeProcessed.startsWith("//#end")) {
                    if (paramContainer.isWhileCounterZero()) {
                        throw new IOException("You have got an #end instruction without #while");
                    }
                    
                    int i_lastWhileIndex = ((Integer) whileIndexesStack.pop()).intValue();
                    
                    if (paramContainer.getWhileCounter() == paramContainer.getActiveWhileCounter()) {
                        paramContainer.decreaseWhileCounter();
                        paramContainer.decreaseActiveWhileCounter();
                        
                        if (paramContainer.isThereNoBreakCommand()) {
                            paramContainer.setCurrentStringIndex(i_lastWhileIndex);
                        }
                        
                        paramContainer.setThereIsNoContinueCommand(true);
                        paramContainer.setThereIsNoBreakCommand(true);
                    } else {
                        paramContainer.decreaseWhileCounter();
                    }
                } else if (processingEnabled && paramContainer.isOutEnabled() && stringToBeProcessed.startsWith("//#action")) {
                    // Вызов внешнего обработчика, если есть
                    if (configurator.getPreprocessorExtension() != null) {
                        stringToBeProcessed = stringToBeProcessed.substring(9).trim();
                        Expression p_stack = Expression.prepare(stringToBeProcessed);
                        p_stack.eval();
                        
                        Value[] ap_results = new Value[p_stack.size()];
                        for (int li = 0; li < p_stack.size(); li++) {
                            ExpressionStackItem p_obj = p_stack.getItemAtPosition(li);
                            if (p_obj.getStackItemType()!=ExpressionStackItemType.VALUE) {
                                throw new IOException("Error arguments list \'" + stringToBeProcessed + "\'");
                            }
                            ap_results[li] = (Value) p_obj;
                        }
                        
                        if (!configurator.getPreprocessorExtension().processAction(ap_results, getDestinationDir(), getDestinationName(), normalTextOutStream, prefixTextOutStream, postfixTextOutStream, configurator.getInfoPrintStream())) {
                            throw new IOException("There is an error during an action processing [" + stringToBeProcessed + "]");
                        }
                    }
                } else if (processingEnabled && paramContainer.isOutEnabled() && stringToBeProcessed.startsWith("//#include")) {
                    // include a file with the path to the place (with processing)
                    stringToBeProcessed = stringToBeProcessed.substring(10).trim();
                    Value p_value = Expression.eval(stringToBeProcessed);
                    
                    if (p_value == null || p_value.getType() != ValueType.STRING) {
                        throw new IOException("You don't have a string result in the #include instruction");
                    }
                    
                    IncludeReference p_inRef = new IncludeReference(preprocessingFile, paramContainer);
                    includeReferenceStack.push(p_inRef);
                    String s_fName = (String) p_value.getValue();
                    try {
                        File p_inclFile = null;
                        p_inclFile = new File(getSourceFile().getParent(), s_fName);
                        
                        preprocessingFile = p_inclFile;
                        paramContainer.setStrings(PreprocessorUtils.readTextFileAndAddNullAtEnd(p_inclFile, configurator.getCharacterEncoding()));
                    } catch (FileNotFoundException e) {
                        throw new IOException("You have got the bad file pointer in the #include instruction [" + s_fName + "]");
                    }
                    fileNameStack.push(paramContainer.getCurrentFileCanonicalPath());
                    paramContainer.setCurrentFileCanonicalPath(s_fName);
                    paramContainer.setCurrentStringIndex(0);
                } else if (stringToBeProcessed.startsWith("//$$") && processingEnabled && paramContainer.isOutEnabled()) {
                    // Output the tail of the string to the output stream without comments and macroses
                    stringToBeProcessed = stringToBeProcessed.substring(4);
                    currentTextOutStream.println(stringToBeProcessed);
                } else if (stringToBeProcessed.startsWith("//$") && processingEnabled && paramContainer.isOutEnabled()) {
                    // Output the tail of the string to the output stream without comments
                    stringToBeProcessed = stringToBeProcessed.substring(3);
                    currentTextOutStream.println(stringToBeProcessed);
                } else if (processingEnabled && paramContainer.isOutEnabled() && stringToBeProcessed.startsWith("//#assert")) {
                    // Out a message to the output stream
                    stringToBeProcessed = stringToBeProcessed.substring(9).trim();
                    configurator.info("-->: " + stringToBeProcessed);
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#+")) {
                    // Turn on outputing to the output stream
                    paramContainer.setOutEnabled(true);
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#-")) {
                    // Turn off outputing to the output stream
                    paramContainer.setOutEnabled(false);
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#//")) {
                    // To comment next string
                    paramContainer.setCommentNextLine(true);
                } else if (processingEnabled) {
                    // Just string :)
                    if (paramContainer.isOutEnabled()) {
                        if (!stringToBeProcessed.startsWith("//#")) {
                            int i_indx;
                            
                            i_indx = stringToBeProcessed.indexOf("/*-*/");
                            if (i_indx >= 0) {
                                stringToBeProcessed = stringToBeProcessed.substring(0, i_indx);
                            }
                            
                            if (paramContainer.shouldCommentNextLine()) {
                                currentTextOutStream.print("// ");
                                paramContainer.setCommentNextLine(false);
                            }
                            for (int li = 0; li < numberOfSpacesAtTheLineBeginning; li++) {
                                currentTextOutStream.print(" ");
                            }
                            currentTextOutStream.println(stringToBeProcessed);
                        } else {
                            throw new IOException("An unsupported preprocessor directive has been found " + stringToBeProcessed);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage() + " file: " + paramContainer.getCurrentFileCanonicalPath() + " str: " + paramContainer.getCurrentStringIndex());
        }
        
        if (!paramContainer.isIfCounterZero()) {
            throw new IOException("You have an unclosed #if construction [" + paramContainer.getCurrentFileCanonicalPath() + ':' + paramContainer.getLastIfStringNumber() + ']');
        }
        if (!paramContainer.isWhileCounterZero()) {
            throw new IOException("You have an unclosed #while construction [" + paramContainer.getCurrentFileCanonicalPath() + ':' + paramContainer.getLastWhileStringNumber() + ']');
        }
        
        
        normalTextOutStream.flush();
        normalTextOutStream.close();
        
        postfixTextOutStream.flush();
        postfixTextOutStream.close();
        
        prefixTextOutStream.flush();
        prefixTextOutStream.close();
        
        
        if (prefixDataBuffer.size() != 0 || postfixDataBuffer.size() != 0 || normalDataBuffer.size() != 0) {
            
            File p_outfile = new File(configurator.getDestinationDirectory(), getDestinationFilePath());
            p_outfile.getParentFile().mkdirs();
            FileOutputStream p_fos = new FileOutputStream(p_outfile);
            
            if (prefixDataBuffer.size() != 0) {
                p_fos.write(prefixDataBuffer.toByteArray());
                p_fos.flush();
            }
            
            p_fos.write(normalDataBuffer.toByteArray());
            p_fos.flush();
            
            if (postfixDataBuffer.size() != 0) {
                p_fos.write(postfixDataBuffer.toByteArray());
                p_fos.flush();
            }
            
            p_fos.close();
            p_fos = null;
            
            if (configurator.isRemovingComments()) {
                removeCommentsFromFile(p_outfile,configurator);
            }
        }
    }

    private final void removeCommentsFromFile(final File file, final Configurator cfg) throws IOException {
        int len = (int)file.length();
        int pos = 0;
        final byte [] memoryFile = new byte[len];
        FileInputStream inStream = new FileInputStream(file);
        try {
            while(len>0) {
                final int read = inStream.read(memoryFile, pos, len);
                if (read<0)
                    break;
                pos += read;
                len -= read;
            }
            
            if (len > 0){
                throw new IOException("Wrong read length");
            }
        }finally{
            try{
                inStream.close();
            }catch(IOException ex){}
        }
        
        if (!file.delete()){
            throw new IOException("Can't delete the file "+file.getAbsolutePath());
        }
        
        final Reader reader = new InputStreamReader(new ByteArrayInputStream(memoryFile),cfg.getCharacterEncoding());
        
        final FileWriter writer = new FileWriter(file,false);
        try {
            new JavaCommentsRemover(reader, writer).process();
            writer.flush();
        }finally{
            try {
            writer.close();
            }catch(IOException ex){}
        }
    }
    
   private void processLocalDefinition(String _str, Configurator cfg) throws IOException
    {
            final String [] splitted = PreprocessorUtils.splitForChar(_str,'=');
        
            if (splitted.length!=2) {
                throw new IOException("Wrong expression ["+_str+']');
            }

            Value p_value = Expression.eval(splitted[1].trim());

            if (p_value == null) throw new IOException("Error value");

            cfg.setLocalVariable(splitted[0].trim(), p_value);
    }
    
    private void setDestinationDir(final String stringToBeProcessed) {
        destinationDir = stringToBeProcessed;
    }

    private void setDestinationName(String stringToBeProcessed) {
        destinationName = stringToBeProcessed;
    }

    public void setExcluded(final boolean flag) {
        excluded = flag;
    }
}
