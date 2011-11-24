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
package com.igormaznitsa.jcpreprocessor.ant;

import com.igormaznitsa.jcpreprocessor.JCPreprocessor;
import com.igormaznitsa.jcpreprocessor.context.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.context.SpecialVariableProcessor;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.logger.PreprocessorLogger;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
    public class CfgFile {
        private File file;
        
        public void setFile(final File file){
            this.file = file;
        }
        
        public File getFile(){
            return this.file;
        }
    }
    
    public class Global {
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
    private String processingExtensions = null;
    private boolean disableOut = false;
    private boolean verbose = false;
    private boolean clearDestinationDirectory = false;
    private boolean removeComments = false;

    private Map<String, Value> antVariables;
    private List<Global> globalVariables = new ArrayList<Global> ();
    private List<CfgFile> configFiles = new ArrayList<CfgFile>();
    
    public void setSource(final File src) {
        sourceDirectory = src;
    }
    
    public void setDestination(final File dst) {
        destinationDirectory = dst;
    }
    
    public void setInCharset(final String charSet) {
        inCharSet = charSet;
    }

    public void setOutCharset(final String charSet) {
        outCharSet = charSet;
    }

    public void setExcluded(final String excluded) {
        excludedExtensions = excluded;
    }
    
    public void setProcessing(final String processing) {
        processingExtensions = processing;
    }
    
    public void setClearDestination(final boolean flag) {
        clearDestinationDirectory = flag;
    }
    
    public void setRemoveComments(final boolean flag) {
        removeComments = flag;
    }
    
    public void setVerbose(final boolean flag) {
        verbose = flag;
    }

    public void setDisableOut(final boolean flag) {
        disableOut = flag;
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
                context.addGlobalVarDefiningFile(f.getFile());
            } else {
                throw new IllegalArgumentException("One of config files doesn't contain the file attribute");
            }
        }
    }
    
    private void fillGlobalVars(final PreprocessorContext context){
        for(final Global g : globalVariables){
            if (g.getName()!=null && g.getValue()!=null){
                context.setGlobalVariable(g.getName(), Value.recognizeOf(g.getValue()));
            } else {
                throw new IllegalArgumentException("Wrong definition of a global variable, may be there is not a needed attribute");
            }
        }
    }
    
    private PreprocessorContext generatePreprocessorContext() {
        final PreprocessorContext context = new PreprocessorContext();
        context.setPreprocessorLogger(this);
        context.registerSpecialVariableProcessor(this);
        
        context.setClearDestinationDirBefore(clearDestinationDirectory);
        
        if (destinationDirectory != null){
            context.setDestinationDirectory(destinationDirectory.getAbsolutePath());
        }
        
        if (sourceDirectory != null){
            context.setSourceDirectory(sourceDirectory.getAbsolutePath());
        } else {
            context.setSourceDirectory(getProject().getBaseDir().getAbsolutePath());
        }
        
        if (excludedExtensions!=null) {
            context.setExcludedFileExtensions(excludedExtensions);
        }
        
        if (processingExtensions!=null) {
            context.setProcessingFileExtensions(processingExtensions);
        }
        
        context.setFileOutputDisabled(disableOut);

        if (inCharSet!=null){
            context.setInCharacterEncoding(inCharSet);
        }
    
        if (outCharSet!=null){
            context.setOutCharacterEncoding(outCharSet);
        }
        
        context.setRemoveComments(removeComments);
        context.setVerbose(verbose);
        
        fillCfgFiles(context);
        fillGlobalVars(context);
        
        return context;
    }
    
    @Override
    public void execute() throws BuildException {
        fillAntVariables();

        PreprocessorContext context = null;
        JCPreprocessor preprocessor = null;
        
        try {
            context = generatePreprocessorContext();
        }catch(Exception unexpected){
            throw new BuildException("Unexpected exception during the procecessing context forming", unexpected);
        }
        
        preprocessor = new JCPreprocessor(context);

        try {
            preprocessor.execute();
        } catch (Exception unexpected) {
            throw new BuildException("Unexpected exception during preprocessing", unexpected);
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
        final Project project = getProject();

        Map<String, Value> result = null;

        if (project == null) {
            result = Collections.EMPTY_MAP;
        } else {

            result = new HashMap<String, Value>();

            for (final Object key : getProject().getProperties().keySet()) {
                final String keyStr = key.toString();
                final String value = project.getProperty(keyStr);
                if (value != null) {
                    result.put("ant."+keyStr.toLowerCase(), Value.valueOf(value));
                }
            }
        }
        antVariables = result;
    }

    @Override
    public String[] getVariableNames() {
        String [] result = null;
        
        if (antVariables == null) {
            result = new String[0];
        } else {
            result = antVariables.keySet().toArray(new String[antVariables.size()]);
        }
        
        return result;
    }

    @Override
    public Value getVariable(final String varName, final PreprocessorContext context, final PreprocessingState state) {
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
        throw new UnsupportedOperationException("Attemption to change an Ant property \'"+varName+'\'');
    }
}
