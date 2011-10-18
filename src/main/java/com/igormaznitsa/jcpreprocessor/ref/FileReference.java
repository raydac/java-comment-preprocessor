package com.igormaznitsa.jcpreprocessor.ref;

import com.igormaznitsa.jcpreprocessor.cfg.Configurator;
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
    
    public void preprocessFile(final Configurator configurator) throws IOException {
        configurator.clearLocalVariables();
        
        String lastIfFileName = null;
        String lastWhileFileName = null;
        int lastIfStringNumber = 0;
        int lastWhileStringNumber = 0;
        
        BufferedReader srcBufferedReader = PreprocessorUtils.makeFileReader(getSourceFile(),configurator.getCharacterEncoding());
        // We need read whole file into memory
        List<String> currentFileStringContainer = new ArrayList<String>(5000);
        try {
            while (true) {
                final String nextLine = srcBufferedReader.readLine();
                
                // we need have null at the end of the list
                currentFileStringContainer.add(nextLine);
                if (nextLine == null) {
                    break;
                }
            }
        } finally {
            srcBufferedReader.close();
            srcBufferedReader = null;
        }
        
        boolean flagOutputEnabled = true;
        boolean flagIfEnabled = true;
        boolean flagToCommentNextLine = false;
        boolean flagNoContinueCommand = true;
        boolean flagNoBreakCommand = true;
        
        int stringNumberCounter = 0;
        int ifConstructionCounter = 0;
        int whileConstructionCounter = 0;
        int activeWhileConstructionCounter = 0;
        int activeIfConstructionCounter = 0;
        String filePath = getSourceFile().getCanonicalPath();
        
        final LinkedList<IncludeReference> includeReferenceStack = new LinkedList<IncludeReference>();
        boolean flagEndPreprocessing = false;
        File preprocessingFile = getSourceFile();
        
        final ByteArrayOutputStream normalDataBuffer = new ByteArrayOutputStream(64000);
        final ByteArrayOutputStream prefixDataBuffer = new ByteArrayOutputStream(1024);
        final ByteArrayOutputStream postfixDataBuffer = new ByteArrayOutputStream(1024);
        
        PrintStream normalTextOutStream = new PrintStream(normalDataBuffer,false, configurator.getCharacterEncoding());
        PrintStream prefixTextOutStream = new PrintStream(prefixDataBuffer, false, configurator.getCharacterEncoding());
        PrintStream postfixTextOutStream = new PrintStream(postfixDataBuffer, false, configurator.getCharacterEncoding());
        
        PrintStream currentTextOutStream = normalTextOutStream;
        
        final LinkedList<String> fileNameStack = new LinkedList<String>();
        fileNameStack.add(filePath);
        
        final LinkedList<Integer> whileIndexesStack = new LinkedList<Integer>();
        
        try {
            while (true) {
                String nonTrimmedProcessingString = currentFileStringContainer.get(stringNumberCounter++);
                
                if (flagEndPreprocessing) {
                    nonTrimmedProcessingString = null;
                }
                
                if (nonTrimmedProcessingString == null) {
                    if (!includeReferenceStack.isEmpty()) {
                        IncludeReference p_inRef = includeReferenceStack.pop();
                        preprocessingFile = p_inRef.getFile();
                        currentFileStringContainer = p_inRef.getStrings();
                        stringNumberCounter = p_inRef.getStringCounter();
                        flagEndPreprocessing = false;
                        fileNameStack.pop();
                        filePath = fileNameStack.getFirst();
                        continue;
                    } else {
                        break;
                    }
                }
                
                final String trimmedProcessingString = nonTrimmedProcessingString.trim();
                
                final int numberOfSpacesAtTheLineBeginning = nonTrimmedProcessingString.indexOf(trimmedProcessingString);
                
                boolean processingEnabled = flagNoBreakCommand && flagIfEnabled && flagNoContinueCommand;
                
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
                    processLocalDefiniting(stringToBeProcessed, configurator);
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#define")) {
                    // Processing of a local definition
                    final String name = PreprocessorUtils.extractTrimmedTail("//#define",stringToBeProcessed);
                    final Value value = new Value("true");
                    configurator.setLocalVariable(name, value);
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#exitif")) {
                    // To end processing the file processing immediatly if the value is true
                    stringToBeProcessed = PreprocessorUtils.extractTrimmedTail("//#exitif", stringToBeProcessed);
                    final Value condition = Expression.eval(stringToBeProcessed);
                    if (condition == null || condition.getType() != ValueType.BOOLEAN) {
                        throw new IOException("You must use a boolean argument for an #endif operator");
                    }
                    if (((Boolean) condition.getValue()).booleanValue()) {
                        flagEndPreprocessing = true;
                        continue;
                    }
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#exit")) {
                    // To end processing the file immediatly
                    flagEndPreprocessing = true;
                    continue;
                } else if (stringToBeProcessed.startsWith("//#continue")) {
                    if (whileConstructionCounter == 0) {
                        throw new IOException("You have #continue without #when");
                    }
                    if (processingEnabled && whileConstructionCounter == activeWhileConstructionCounter) {
                        flagNoContinueCommand = false;
                    }
                } else if (stringToBeProcessed.startsWith("//#break")) {
                    if (whileConstructionCounter == 0) {
                        throw new IOException("You have #break without #when");
                    }
                    
                    if (processingEnabled && whileConstructionCounter == activeWhileConstructionCounter) {
                        flagNoBreakCommand = false;
                    }
                } else if (stringToBeProcessed.startsWith("//#while")) {
                    // To end processing the file immediatly
                    if (processingEnabled) {
                        stringToBeProcessed = stringToBeProcessed.substring(8).trim();
                        Value p_value = Expression.eval(stringToBeProcessed);
                        if (p_value == null || p_value.getType() != ValueType.BOOLEAN) {
                            throw new IOException("You don't have a boolean result in the #while instruction");
                        }
                        if (whileConstructionCounter == 0) {
                            lastWhileFileName = filePath;
                            lastWhileStringNumber = stringNumberCounter;
                        }
                        whileConstructionCounter++;
                        activeWhileConstructionCounter = whileConstructionCounter;
                        
                        if (((Boolean) p_value.getValue()).booleanValue()) {
                            flagNoBreakCommand = true;
                        } else {
                            flagNoBreakCommand = false;
                        }
                    } else {
                        whileConstructionCounter++;
                    }
                    
                    whileIndexesStack.push(new Integer(stringNumberCounter - 1));
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
                        
                        if (stringToBeProcessed.length() == 0) {
                            throw new IOException("You have not defined any variable in a //#ifdefined deirective");
                        }
                        
                        boolean lg_defined = configurator.findVariableForName(stringToBeProcessed)!=null;
                        
                        if (ifConstructionCounter == 0) {
                            lastIfFileName = filePath;
                            lastIfStringNumber = stringNumberCounter;
                        }
                        ifConstructionCounter++;
                        activeIfConstructionCounter = ifConstructionCounter;
                        
                        if (lg_defined) {
                            flagIfEnabled = true;
                        } else {
                            flagIfEnabled = false;
                        }
                    } else {
                        ifConstructionCounter++;
                    }
                } else if (stringToBeProcessed.startsWith("//#if")) {
                    // Processing #if instruction
                    if (processingEnabled) {
                        stringToBeProcessed = stringToBeProcessed.substring(5).trim();
                        Value p_value = Expression.eval(stringToBeProcessed);
                        if (p_value == null || p_value.getType() != ValueType.BOOLEAN) {
                            throw new IOException("You don't have a boolean result in the #if instruction");
                        }
                        if (ifConstructionCounter == 0) {
                            lastIfFileName = filePath;
                            lastIfStringNumber = stringNumberCounter;
                        }
                        ifConstructionCounter++;
                        activeIfConstructionCounter = ifConstructionCounter;
                        
                        if (((Boolean) p_value.getValue()).booleanValue()) {
                            flagIfEnabled = true;
                        } else {
                            flagIfEnabled = false;
                        }
                    } else {
                        ifConstructionCounter++;
                    }
                } else if (stringToBeProcessed.startsWith("//#else")) {
                    if (ifConstructionCounter == 0) {
                        throw new IOException("You have got an #else instruction without #if");
                    }
                    
                    if (ifConstructionCounter == activeIfConstructionCounter) {
                        flagIfEnabled = !flagIfEnabled;
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
                            throw new IOException("You have the error in the #outdir instruction in the file " + getSourceFile().getAbsolutePath() + " at line: " + stringNumberCounter + " [" + e.getMessage() + ']');
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
                            throw new IOException("You have the error in the #outname instruction in the file " + getSourceFile().getAbsolutePath() + " line: " + stringNumberCounter + " [" + e.getMessage() + ']');
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
                            throw new IOException("Exception during //#flush operator in the file " + getSourceFile().getCanonicalPath() + " at line: " + stringNumberCounter + " [" + e.getMessage() + ']',e);
                        }
                    }
                } else if (stringToBeProcessed.startsWith("//#endif")) {
                    if (ifConstructionCounter == 0) {
                        throw new IOException("You have got an #endif instruction without #if");
                    }
                    
                    if (ifConstructionCounter == activeIfConstructionCounter) {
                        ifConstructionCounter--;
                        activeIfConstructionCounter--;
                        flagIfEnabled = true;
                    } else {
                        ifConstructionCounter--;
                    }
                } else if (stringToBeProcessed.startsWith("//#end")) {
                    if (whileConstructionCounter == 0) {
                        throw new IOException("You have got an #end instruction without #while");
                    }
                    
                    int i_lastWhileIndex = ((Integer) whileIndexesStack.pop()).intValue();
                    
                    if (whileConstructionCounter == activeWhileConstructionCounter) {
                        whileConstructionCounter--;
                        activeWhileConstructionCounter--;
                        
                        if (flagNoBreakCommand) {
                            stringNumberCounter = i_lastWhileIndex;
                        }
                        
                        flagNoContinueCommand = true;
                        flagNoBreakCommand = true;
                    } else {
                        whileConstructionCounter--;
                    }
                } else if (processingEnabled && flagOutputEnabled && stringToBeProcessed.startsWith("//#action")) {
                    // Вызов внешнего обработчика, если есть
                    if (configurator.getPreprocessorExtension() != null) {
                        stringToBeProcessed = stringToBeProcessed.substring(9).trim();
                        Expression p_stack = Expression.parseExpression(stringToBeProcessed);
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
                } else if (processingEnabled && flagOutputEnabled && stringToBeProcessed.startsWith("//#include")) {
                    // include a file with the path to the place (with processing)
                    stringToBeProcessed = stringToBeProcessed.substring(10).trim();
                    Value p_value = Expression.eval(stringToBeProcessed);
                    
                    if (p_value == null || p_value.getType() != ValueType.STRING) {
                        throw new IOException("You don't have a string result in the #include instruction");
                    }
                    
                    IncludeReference p_inRef = new IncludeReference(preprocessingFile, filePath, currentFileStringContainer, stringNumberCounter);
                    includeReferenceStack.push(p_inRef);
                    String s_fName = (String) p_value.getValue();
                    try {
                        File p_inclFile = null;
                        p_inclFile = new File(getSourceFile().getParent(), s_fName);
                        
                        preprocessingFile = p_inclFile;
                        
                        srcBufferedReader = PreprocessorUtils.makeFileReader(p_inclFile,configurator.getCharacterEncoding());
                        currentFileStringContainer = new ArrayList(2000);
                        while (true) {
                            String s_s = srcBufferedReader.readLine();
                            currentFileStringContainer.add(s_s);
                            if (s_s == null) {
                                break;
                            }
                        }
                        srcBufferedReader.close();
                        srcBufferedReader = null;
                    } catch (FileNotFoundException e) {
                        throw new IOException("You have got the bad file pointer in the #include instruction [" + s_fName + "]");
                    }
                    fileNameStack.push(filePath);
                    filePath = s_fName;
                    stringNumberCounter = 0;
                } else if (stringToBeProcessed.startsWith("//$$") && processingEnabled && flagOutputEnabled) {
                    // Output the tail of the string to the output stream without comments and macroses
                    stringToBeProcessed = stringToBeProcessed.substring(4);
                    currentTextOutStream.println(stringToBeProcessed);
                } else if (stringToBeProcessed.startsWith("//$") && processingEnabled && flagOutputEnabled) {
                    // Output the tail of the string to the output stream without comments
                    stringToBeProcessed = stringToBeProcessed.substring(3);
                    currentTextOutStream.println(stringToBeProcessed);
                } else if (processingEnabled && flagOutputEnabled && stringToBeProcessed.startsWith("//#assert")) {
                    // Out a message to the output stream
                    stringToBeProcessed = stringToBeProcessed.substring(9).trim();
                    configurator.info("-->: " + stringToBeProcessed);
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#+")) {
                    // Turn on outputing to the output stream
                    flagOutputEnabled = true;
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#-")) {
                    // Turn off outputing to the output stream
                    flagOutputEnabled = false;
                } else if (processingEnabled && stringToBeProcessed.startsWith("//#//")) {
                    // To comment next string
                    flagToCommentNextLine = true;
                } else if (processingEnabled) {
                    // Just string :)
                    if (flagOutputEnabled) {
                        if (!stringToBeProcessed.startsWith("//#")) {
                            int i_indx;
                            
                            i_indx = stringToBeProcessed.indexOf("/*-*/");
                            if (i_indx >= 0) {
                                stringToBeProcessed = stringToBeProcessed.substring(0, i_indx);
                            }
                            
                            if (flagToCommentNextLine) {
                                currentTextOutStream.print("// ");
                                flagToCommentNextLine = false;
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
            throw new IOException(e.getMessage() + " file: " + filePath + " str: " + stringNumberCounter);
        }
        
        if (ifConstructionCounter > 0) {
            throw new IOException("You have an unclosed #if construction [" + lastIfFileName + ':' + lastIfStringNumber + ']');
        }
        if (whileConstructionCounter > 0) {
            throw new IOException("You have an unclosed #while construction [" + lastWhileFileName + ':' + lastWhileStringNumber + ']');
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
    
   private void processLocalDefiniting(String _str, Configurator cfg) throws IOException
    {
            final String [] splitted = _str.split("=");
        
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
