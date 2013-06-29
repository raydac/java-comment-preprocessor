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
package com.igormaznitsa.jcp.ant;

import com.igormaznitsa.jcp.JCPreprocessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.context.SpecialVariableProcessor;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.logger.PreprocessorLogger;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * The class implements an ANT task to allow calls for preprocessing from ANT build scripts.
 * Also it allows to out messages from preprocessor directives into the ANT log and read ANT properties as global variables (with the "ant." prefix)
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class PreprocessTask extends Task implements PreprocessorLogger, SpecialVariableProcessor {
    /**
     * Inside class describes a "cfgfile" item, it has the only attribute "file", the attribute must be defined
     * 
     * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
     */
    public static class CfgFile {
        private File file;
        
        public void setFile(final File file){
            this.file = file;
        }
        
        public File getFile(){
            return this.file;
        }
    }
    
    /**
     * Inside class describes a "global" item, it describes a global variable which will be added into the preprocessor context
     * It has attributes "name" and "value", be careful in the value attribute usage because you have to use "&quot;" instead of \" symbol inside string values
     * 
     * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
     */
    public static class Global {
        private String name;
        private String value;
        
        public void setName(final String name){
            this.name = name;
        }
        
        public String getName(){
            return this.name;
        }
        
        public void setValue(final String value){
            this.value = value;
        }
        
        public String getValue(){
            return this.value;
        }
    }
    
    private File sourceDirectory = null;
    private File destinationDirectory = null;

    private String inCharSet = null;
    private String outCharSet = null;
    private String excludedExtensions = null;
    private String processing = null;
    private boolean disableOut = false;
    private boolean verbose = false;
    private boolean clearDstFlag = false;
    private boolean removeComments = false;
    private boolean keepLines = false;
    
    private Map<String, Value> antVariables;
    private List<Global> globalVariables = new ArrayList<Global> ();
    private List<CfgFile> configFiles = new ArrayList<CfgFile>();
    
    /**
     * Set the "source" attribute, it allows to define the source directory to be preprocessed
     * @param src a directory to be used as the source one, must not be null
     */
    public void setSource(final File src) {
        this.sourceDirectory = src;
    }
    
    /**
     * Set the "destination" attribute, it allows to define the destination directory where the preprocessed files will be placed in
     * @param dst a directory to be used as the destination one, must not be null
     */
    public void setDestination(final File dst) {
        this.destinationDirectory = dst;
    }
    
    /**
     * Set the "inCharset" attribute, it allows to define the text encoding for the reading text files
     * @param charSet the character set to be used to decode read texts, must not be null
     */
    public void setInCharset(final String charSet) {
        this.inCharSet = charSet;
    }

    /**
     * Set the "outCharset" attribute, it allows to define the text encoding for the writing text files
     * @param charSet the character set to be used to encode written texts, must not be null
     */
    public void setOutCharset(final String charSet) {
        this.outCharSet = charSet;
    }

    /**
     * Set the "excluded" attribute, it defines the excluded file extensions which will be ignored by the preprocessor in its work (also those files will not be copied)
     * @param ext the list of ignored file extensions, must not be null
     */
    public void setExcluded(final String ext) {
        this.excludedExtensions = ext;
    }
    
    /**
     * Set the "processing" attribute, it defines the file extensions to be processed 
     * @param ext the list of file extensions which should be preprocessed, must not be null
     */
    public void setProcessing(final String ext) {
        this.processing = ext;
    }
    
    /**
     * Set the "clear" attribute, it is a boolean attribute allows to make the preprocessor to clear the destination directory before its work
     * @param flag true if the destination directory must be cleared before preprocessing, otherwise false
     */
    public void setClear(final boolean flag) {
        this.clearDstFlag = flag;
    }
    
    /**
     * Set the "removeComments" attribute, it is a boolean attribute allows to make the preprocessor to remove all Java-like comments from the result files
     * @param flag true if the result file must be cleared from comments, otherwise false
     */
    public void setRemoveComments(final boolean flag) {
        this.removeComments = flag;
    }
    
    /**
     * Set the "verbose" attribute, it is a boolean attribute allows to set the verbose level of preprocessor messages
     * @param flag true if the verbose level must be set, otherwise false
     */
    public void setVerbose(final boolean flag) {
        this.verbose = flag;
    }

    /**
     * Set the "keepLines" attribute, it is a boolean attribute to keep non-executing lines as commented ones in the output
     * @param flag true if preprocessor should keep the lines as commented ones, false otherwise
     */
    public void setKeepLines(final boolean flag){
      this.keepLines = flag;
    }
    
    /**
     * Set the "disableOut" attribute, it is a boolean attribute allows to disable any output operations into the destination directory
     * @param flag true if the output operations must be disabled, otherwise false
     */
    public void setDisableOut(final boolean flag) {
        this.disableOut = flag;
    }

    public Global createGlobal() {
        final Global result = new Global();
        globalVariables.add(result);
        return result;
    }

    public CfgFile createCfgFile() {
        final CfgFile result = new CfgFile();
        configFiles.add(result);
        return result;
    }
    
    private void fillCfgFiles(final PreprocessorContext context){
        for(final CfgFile f : configFiles){
            if (f.getFile() != null){
                context.addConfigFile(f.getFile());
            } else {
                throw new IllegalArgumentException("One of config files doesn't contain the file attribute");
            }
        }
    }
    
    private void fillGlobalVars(final PreprocessorContext context){
        for(final Global g : globalVariables){
            if (g.getName()!=null && g.getValue()!=null){
                context.setGlobalVariable(g.getName(), Value.recognizeRawString(g.getValue()));
            } else {
                throw new IllegalArgumentException("Wrong definition of a global variable, may be there is not a needed attribute");
            }
        }
    }
    
    PreprocessorContext generatePreprocessorContext() {
        fillAntVariables();
        
        final PreprocessorContext context = new PreprocessorContext();
        context.setPreprocessorLogger(this);
        context.registerSpecialVariableProcessor(this);
        
        if (destinationDirectory != null){
            context.setDestinationDirectory(destinationDirectory.getAbsolutePath());
        }
        
        if (sourceDirectory != null){
            context.setSourceDirectories(sourceDirectory.getAbsolutePath());
        } else {
            context.setSourceDirectories(getProject().getBaseDir().getAbsolutePath());
        }
        
        if (excludedExtensions!=null) {
            context.setExcludedFileExtensions(excludedExtensions);
        }
        
        if (processing!=null) {
            context.setProcessingFileExtensions(processing);
        }
        

        if (inCharSet!=null){
            context.setInCharacterEncoding(inCharSet);
        }
    
        if (outCharSet!=null){
            context.setOutCharacterEncoding(outCharSet);
        }
        
        context.setClearDestinationDirBefore(clearDstFlag);
        context.setFileOutputDisabled(disableOut);
        context.setRemoveComments(removeComments);
        context.setVerbose(verbose);
        context.setKeepLines(keepLines);
        
        fillCfgFiles(context);
        fillGlobalVars(context);
        
        return context;
    }
    
    final String extractMessageFromException(final Throwable exception){
        Throwable thr = exception;
        String result = exception.getMessage();
        
        while(result!=null){
            if (thr instanceof PreprocessorException || thr instanceof IllegalArgumentException || thr instanceof IllegalStateException) {
                result = thr.getMessage() != null ? thr.getMessage() : result;
                break;
            } 
            thr = thr.getCause();
            result = thr.getMessage() != null ? thr.getMessage() : result;
        }
        
        return result == null ? "" : result;
    }
    
    @Override
    public void execute() throws BuildException {
        PreprocessorContext context = null;
        JCPreprocessor preprocessor;
        
        try {
            context = generatePreprocessorContext();
        }catch(Exception unexpected){
            throw new BuildException(extractMessageFromException(unexpected), unexpected);
        }
        
        preprocessor = new JCPreprocessor(context);

        try {
            preprocessor.execute();
        } catch (Exception unexpected) {
            throw new BuildException(extractMessageFromException(unexpected), unexpected);
        }
    }

    @Override
    public void error(final String message) {
        log(message, Project.MSG_ERR);
    }

    @Override
    public void info(final String message) {
        log(message, Project.MSG_INFO);
    }

    @Override
    public void warning(final String message) {
        log(message, Project.MSG_WARN);
    }

    private void fillAntVariables() {
        final Project theProject = getProject();

        Map<String, Value> result;

        if (theProject == null) {
            result = Collections.emptyMap();
        } else {

            result = new HashMap<String, Value>();

            for (final Object key : getProject().getProperties().keySet()) {
                final String keyStr = key.toString();
                final String value = theProject.getProperty(keyStr);
                if (value != null) {
                    result.put("ant."+keyStr.toLowerCase(Locale.ENGLISH), Value.valueOf(value));
                }
            }
        }
        antVariables = result;
    }

    @Override
    public String[] getVariableNames() {
        String [] result;
        
        if (antVariables == null) {
            result = new String[0];
        } else {
            result = antVariables.keySet().toArray(new String[antVariables.size()]);
        }
        
        return result;
    }

    @Override
    public Value getVariable(final String varName, final PreprocessorContext context) {
        if (antVariables == null) {
            throw new IllegalStateException("Non-initialized ant property map detected");
        }
        final Value result = antVariables.get(varName);
        
        if (result == null){
            throw new IllegalArgumentException("Unsupported Ant property requested \'"+varName+'\'');
        }
        return result;
    }

    @Override
    public void setVariable(final String varName, final Value value, final PreprocessorContext context) {
        throw new UnsupportedOperationException("An Attempt to change an ANT property \'"+varName+"\'. All ANT properties allowed only to be read!");
    }
}
