package com.igormaznitsa.jcpreprocessor.directives;

import java.io.File;
import java.io.IOException;

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
    
    public ParameterContainer(final File file) throws IOException {
        init(file);
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
    }

}
