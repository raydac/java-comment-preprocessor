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
package com.igormaznitsa.jcpreprocessor.maven;

import com.igormaznitsa.jcpreprocessor.JCPreprocessor;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.context.SpecialVariableProcessor;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.logger.PreprocessorLogger;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * It allow to make preprocessing of sources and text data in maven projects.
 * 
 * @goal preprocess
 * @phase generate-sources
 * @threadSafe
 * @requiresProject
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class PreprocessorMojo extends AbstractMojo implements PreprocessorLogger, SpecialVariableProcessor {

    /**
     * The project to be preprocessed.
     *
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    /**
     * Source directory
     * 
     * @parameter name="source" default-value="${project.build.sourceDirectory}"
     * @readonly
     */
    private File _source;
    /**
     * Destination directory
     * 
     * @parameter name="destination" default-value="${project.build.outputDirectory}"
     * @readonly
     */
    private File _destination;
    /**
     * Input text character encoding
     * 
     * @parameter name="inencoding" default-value="${project.build.sourceEncoding}"
     * @readonly
     */
    private String _inencoding;
    /**
     * Output text character encoding
     * 
     * @parameter name="outencoding" default-value="${project.build.sourceEncoding}"
     * @readonly
     */
    private String _outencoding;
    /**
     * List of excluded extensions from preprocessing
     * 
     * @parameter name="excluded"
     * @readonly
     */
    private String _excluded;
    /**
     * List of extensions to be preprocessed
     * 
     * @parameter name="processing"
     * @readonly
     */
    private String _processing;
    /**
     * Disable any output file operations
     * 
     * @parameter name="disableout" default-value="false"
     * @readonly
     */
    private boolean _disableout;
    /**
     * Make verbose message output for inside processes
     * 
     * @parameter name="verbose" default-value="false"
     * @readonly
     */
    private boolean _verbose;
    /**
     * Clear the destination directory before preprocessing if it is existing
     * 
     * @parameter name="clear" default-value="false"
     * @readonly
     */
    private boolean _clearDestination;
    /**
     * Remove all Java like commentaries from the resulting files
     * 
     * @parameter name="removecomments" default-value="false"
     * @readonly
     */
    private boolean _removecomments;
    
    /**
     * Global variables
     * 
     * @parameter name="globalvars"
     * @readonly
     */
    private Properties _globalvars;
    
    /**
     * Configuration files
     * 
     * @parameter name="cfgfiles"
     * @readonly
     */
    private File [] _cfgfiles;
    
    /**
     * The variable contains the processed variable map
     */
    private Map<String, Value> _variableMap;

    public void setGlobalvars(final Properties vars){
        this._globalvars = vars;
    }
    
    public Properties getGlobalvars(){
        return this._globalvars;
    }
    
    public void setCfgfiles(final File [] files){
        this._cfgfiles = files;
    }
    
    public File [] getCfgfiles(){
        return this._cfgfiles;
    }
    
    public void setSource(final File source) {
        this._source = source;
    }

    public File getSource() {
        return this._source;
    }

    public void setDestination(final File destination) {
        this._destination = destination;
    }

    public File getDestination() {
        return this._destination;
    }

    public void setInencoding(final String incharset) {
        this._inencoding = incharset;
    }

    public String getInencoding() {
        return this._inencoding;
    }

    public void setOutencoding(final String outcharset) {
        this._outencoding = outcharset;
    }

    public String getOutencoding() {
        return this._outencoding;
    }

    public void setExcluded(final String excluded) {
        this._excluded = excluded;
    }

    public String getExcluded() {
        return this._excluded;
    }

    public void setProcessing(final String processing) {
        this._processing = processing;
    }

    public String getProcessing() {
        return this._processing;
    }

    public void setDisableout(final boolean disableout) {
        this._disableout = disableout;
    }

    public boolean getDisableout() {
        return this._disableout;
    }

    public void setVerbose(final boolean verbose) {
        this._verbose = verbose;
    }

    public boolean getVerbose() {
        return this._verbose;
    }

    public void setRemovecomments(final boolean value) {
        this._removecomments = value;
    }

    public boolean getRemovecomments() {
        return this._removecomments;
    }

    private void fillVariableMap() {
        final Map<String, Value> result = new HashMap<String, Value>();

        if (project != null) {
            final Properties props = project.getProperties();
            for(final String key : props.stringPropertyNames()){
                final String keyStr = "mvn."+key.toLowerCase();
                final String value = props.getProperty(key);
                if (value!=null){
                    result.put(key, Value.recognizeOf(key));
                }
            }
        }

        _variableMap = result;
    }

    PreprocessorContext makePreprocessorContext() throws IOException {
        final PreprocessorContext context = new PreprocessorContext();
        context.setSourceDirectory(_source.getCanonicalPath());
        context.setDestinationDirectory(_destination.getCanonicalPath());
        
        if (_inencoding!=null)
        context.setInCharacterEncoding(_inencoding);
        if (_outencoding!=null)
        context.setOutCharacterEncoding(_outencoding);
        if (_excluded!=null)
            context.setExcludedFileExtensions(_excluded);
        if (_processing!=null)
            context.setProcessingFileExtensions(_processing);
        
        context.setClearDestinationDirBefore(_clearDestination);
        context.setRemoveComments(_removecomments);
        context.setVerbose(_verbose);
        context.setFileOutputDisabled(_disableout);
        
        // process cfg files
        if (_cfgfiles!=null && _cfgfiles.length!=0){
            for(final File file : _cfgfiles){
                if (file == null){
                    throw new NullPointerException("A NULL in place of a config file detected");
                }
                
                context.addConfigFile(file);
            }
        }
        
        // process global vars
        if (!_globalvars.isEmpty()){
            for(final String key : _globalvars.stringPropertyNames()){
                final String value = _globalvars.getProperty(key);
                if (value == null){
                    throw new NullPointerException("Can't find defined value for '"+key+"' global variable");
                }
                context.setGlobalVariable(key, Value.recognizeOf(value));
            }
        }
        
        return context;
    }
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        fillVariableMap();
       
        PreprocessorContext context = null;
        
        try {
            context = makePreprocessorContext();
            context.registerSpecialVariableProcessor(this);
            context.setPreprocessorLogger(this);
        }catch(Exception ex){
            throw new MojoExecutionException("Exception during preprocessor context creation",ex);
        }
        
        try {
            final JCPreprocessor preprocessor = new JCPreprocessor(context);
            preprocessor.execute();
        }catch(Exception ex){
            throw new MojoFailureException("Exception during preprocessing or preparation", ex);
        }
    }

    @Override
    public void error(final String message) {
        getLog().error(message);
    }

    @Override
    public void info(String message) {
        getLog().info(message);
    }

    @Override
    public void warning(String message) {
        getLog().warn(message);
    }

    @Override
    public String[] getVariableNames() {
        return null;
    }

    @Override
    public Value getVariable(final String varName, final PreprocessorContext context) {
        Value result = null;
        if (_variableMap != null) {
            result = _variableMap.get(varName);
            if (result == null){
                throw new IllegalStateException("Detected request for a nonexsitiong variable ["+varName+']');
            }
        }
        return result;
    }

    @Override
    public void setVariable(String varName, Value value, PreprocessorContext context) {
        throw new UnsupportedOperationException("Writiong operation disallowed for maven properties ["+varName+']');
    }
}
