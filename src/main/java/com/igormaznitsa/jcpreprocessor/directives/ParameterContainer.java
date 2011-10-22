package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.references.FileReference;
import com.igormaznitsa.jcpreprocessor.references.IncludeReference;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

public class ParameterContainer {
    private String [] strings;
    private boolean outEnabled;
    private boolean ifEnabled;
    private boolean toCommentNextLine;
    private boolean noContinueCommand;
    private boolean noBreakCommand;
    private boolean endPreprocessing;
    
    private String lastIfFileName;
    private String lastWhileFileName;
    private int lastIfStringNumber;
    private int lastWhileStringNumber;
        
    private int stringNumberCounter;
    private int ifConstructionCounter;
    private int whileConstructionCounter;
    private int activeWhileConstructionCounter;
    private int activeIfConstructionCounter;
    private String currentFileCanonicalPath;
    
    private ByteArrayOutputStream normalDataBuffer;
    private ByteArrayOutputStream prefixDataBuffer;
    private ByteArrayOutputStream postfixDataBuffer;
        
    
    
    private PrintStream prefixOutStream;
    private PrintStream postfixOutStream;
    private PrintStream normalOutStream;
    
    private PrintStream currentOutStream;
    private final FileReference fileReference;
    private final LinkedList<Integer> whileIndexes = new LinkedList<Integer>();
    private final LinkedList<IncludeReference> includeReferenceStack = new LinkedList<IncludeReference>();
    private final LinkedList<String> fileNameStack = new LinkedList<String>();

    private File currentProcessingFile;
    
    private final String encoding;
    
    public ParameterContainer(final FileReference reference, final File file, final String encoding) throws IOException {
        fileReference = reference;
        this.encoding = encoding;
        init(file);
    }
    
    public PrintStream print(final String str) {
        currentOutStream.print(str);
        return currentOutStream;
    }
    
    public PrintStream println(final String str) {
        currentOutStream.println(str);
        return currentOutStream;
    }
    
    public File getCurrentProcessingFile() {
        return currentProcessingFile;
    }
   
    public String popFileName() {
        return fileNameStack.pop();
    }
    
    public ParameterContainer pushFileName(final String name){
        fileNameStack.push(name);
        return this;
    }
    
    public ParameterContainer setCurrentProcessingFile(final File file) {
        currentProcessingFile = file;
        return this;
    }
    
    public FileReference getFileReference() {
        return fileReference;
    }
    
    public PrintStream getPrefixOutStream() {
        return prefixOutStream;
    }
    
    public PrintStream getPostfixOutStream() {
        return postfixOutStream;
    }
    
    public PrintStream getNormalOutStream() {
        return normalOutStream;
    }
    
    public ParameterContainer pushIncludeReference(final IncludeReference ref) {
        includeReferenceStack.push(ref);
        return this;
    }
    
    public IncludeReference popIncludeReference() {
        return includeReferenceStack.pop();
    }
    
    public boolean isIncludeReferenceEmpty() {
        return includeReferenceStack.isEmpty();
    }
    
    public ParameterContainer setCurrentOutStream(final PrintStream stream) {
        currentOutStream = stream;
        return this;
    }
    
    public int popWhileIndex() {
        return whileIndexes.pop();
    }
    
    public void pushWhileIndex(int index) {
        whileIndexes.push(index);
    }
    
    public ParameterContainer setStrings(final String [] stringSet) {
        strings = stringSet;
        return this;
    }
    
    public String nextLine() {
        if (strings == null || stringNumberCounter<0 || stringNumberCounter>=strings.length) {
            return null;
        }
        return strings[stringNumberCounter++];
    }
    
    public String [] getStrings(){
        return strings;
    }
    
    public ParameterContainer setOutEnabled(final boolean flag) {
        outEnabled = flag;
        return this;
    }
    
    public boolean isOutEnabled() {
        return outEnabled;
    }
    
    public ParameterContainer setIfEnabled(final boolean flag) {
        ifEnabled = flag;
        return this;
    }
    
    public boolean isIfEnabled() {
        return ifEnabled;
    }
    
    public ParameterContainer setCommentNextLine(final boolean flag) {
        toCommentNextLine = flag;
        return this;
    }
    
    public boolean shouldCommentNextLine() {
        return toCommentNextLine;
    }
    
    public ParameterContainer setThereIsNoContinueCommand(final boolean flag) {
        noContinueCommand = flag;
        return this;
    }
    
    public boolean isThereNoContinueCommand() {
        return noContinueCommand;
    }
    
    
    public ParameterContainer setThereIsNoBreakCommand(final boolean flag) {
        noBreakCommand = flag;
        return this;
    }
    
    public boolean isThereNoBreakCommand() {
        return noBreakCommand;
    }
    
    public ParameterContainer setCurrentStringIndex(final int index) {
        stringNumberCounter = index;
        return this;
    }
    
    public ParameterContainer increaseCurrentStringIndex() {
        stringNumberCounter ++;
        return this;
    }
    
    public int getCurrentStringIndex(){
        return stringNumberCounter;
    }
    
    public ParameterContainer increaseIfCounter() {
        ifConstructionCounter ++;
        return this;
    }
    
    public ParameterContainer decreaseIfCounter() {
        ifConstructionCounter--;
        return this;
    }
    
    public ParameterContainer setIfCounter(final int value) {
        ifConstructionCounter = value;
        return this;
    }
    
    public int getIfCounter() {
        return ifConstructionCounter;
    }
    
    public ParameterContainer increaseWhileCounter() {
        whileConstructionCounter ++;
        return this;
    }
    
    public ParameterContainer decreaseWhileCounter() {
        whileConstructionCounter--;
        return this;
    }
    
    public ParameterContainer setWhileCounter(final int value) {
        whileConstructionCounter = value;
        return this;
    }
    
    public int getWhileCounter() {
        return whileConstructionCounter;
    }
    
    public ParameterContainer increaseActiveIfCounter() {
        activeIfConstructionCounter ++;
        return this;
    }
    
    public ParameterContainer decreaseActiveIfCounter() {
        activeIfConstructionCounter --;
        return this;
    }
    
    public ParameterContainer setActiveIfCounter(final int value) {
        activeIfConstructionCounter = value;
        return this;
    }
    
    public int getActiveIfCounter() {
        return activeIfConstructionCounter;
    }
    
    public ParameterContainer increaseActiveWhileCounter() {
        activeWhileConstructionCounter ++;
        return this;
    }
    
    public ParameterContainer decreaseActiveWhileCounter() {
        activeWhileConstructionCounter--;
        return this;
    }
    
    public ParameterContainer setActiveWhileCounter(final int value) {
        activeWhileConstructionCounter = value;
        return this;
    }
    
    public int getActiveWhileCounter() {
        return activeWhileConstructionCounter;
    }
    
    public ParameterContainer setLastIfFileName(final String fileName) {
        lastIfFileName = fileName;
        return this;
    }
    
    public String getLastIfFileName() {
        return lastIfFileName;
    }
    
    
    public ParameterContainer setLastWhileFileName(final String fileName) {
        lastWhileFileName = fileName;
        return this;
    }
    
    public String getLastWhileFileName() {
        return lastWhileFileName;
    }
    
    public ParameterContainer setLastWhileStringNumber(final int number) {
        lastWhileStringNumber = number;
        return this;
    }
    
    public int getLastWhileStringNumber() {
        return lastWhileStringNumber;
    }
    
    
    public ParameterContainer setLastIfStringNumber(final int number) {
        lastIfStringNumber = number;
        return this;
    }
    
    public int getLastIfStringNumber() {
        return lastIfStringNumber;
    }
    
    public ParameterContainer setEndPreprocessing(final boolean flag) {
        endPreprocessing = flag;
        return this;
    }
    
    public boolean shouldEndPreprocessing() {
        return endPreprocessing;
    }
    
    public String getCurrentFileCanonicalPath() {
        return currentFileCanonicalPath;
    }

    public ParameterContainer setCurrentFileCanonicalPath(final String path) {
        currentFileCanonicalPath = path;
        return this;
    }
    
    public boolean isCurrentIfIndexEqualsActive() {
        return ifConstructionCounter == activeIfConstructionCounter;
    }
    
    public boolean isCurrentWhileEqualsActive() {
        return whileConstructionCounter == activeWhileConstructionCounter;
    }
    
    public boolean isIfCounterZero() {
        return ifConstructionCounter == 0;
    }
    
    public boolean isWhileCounterZero() {
        return whileConstructionCounter == 0;
    }

    public boolean isProcessingEnabled() {
        return isThereNoBreakCommand() && isIfEnabled() &&  isThereNoContinueCommand();        
    }
    
    public void init(final File file) throws IOException {
        endPreprocessing = false;
        outEnabled = true;
        ifEnabled = true;
        toCommentNextLine = false;
        noContinueCommand = true;
        noBreakCommand = true;
        stringNumberCounter = 0;
        ifConstructionCounter = 0;
        whileConstructionCounter = 0;
        activeIfConstructionCounter = 0;
        activeWhileConstructionCounter = 0;
        currentFileCanonicalPath = file.getCanonicalPath();
        lastIfFileName = null;
        lastIfStringNumber = 0;
        lastWhileFileName = null;
        lastWhileStringNumber = 0;

        reinitOutBuffers();
        
        currentOutStream = normalOutStream;
    }
    
    public void reinitOutBuffers() throws IOException {
        normalDataBuffer= new ByteArrayOutputStream(64*1024);
        prefixDataBuffer = new ByteArrayOutputStream(1024);
        postfixDataBuffer =  new ByteArrayOutputStream(1024);
        
        normalOutStream = new PrintStream(normalDataBuffer, false, encoding);
        prefixOutStream = new PrintStream(prefixDataBuffer, false, encoding);
        postfixOutStream = new PrintStream(postfixDataBuffer, false, encoding);
        
        currentOutStream = normalOutStream;
    }

    public void saveBuffersToStreams(final OutputStream prefix, final OutputStream normal, final OutputStream postfix) throws IOException {
        prefixOutStream.flush();
        postfixOutStream.flush();
        normalOutStream.flush();
        
        prefix.write(prefixDataBuffer.toByteArray());
        normal.write(normalDataBuffer.toByteArray());
        postfix.write(postfixDataBuffer.toByteArray());
    }
    
    public boolean saveBuffersToFile(File outFile) throws IOException {
        normalOutStream.flush();
        normalOutStream.close();
        
        postfixOutStream.flush();
        postfixOutStream.close();
        
        prefixOutStream.flush();
        prefixOutStream.close();
        
        
        if (prefixDataBuffer.size() != 0 || postfixDataBuffer.size() != 0 || normalDataBuffer.size() != 0) {
            
            outFile.getParentFile().mkdirs();
            FileOutputStream p_fos = new FileOutputStream(outFile);
            
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
            
            return true;
         }
        else {
            return false;
        }
        
        
    }
    
}
