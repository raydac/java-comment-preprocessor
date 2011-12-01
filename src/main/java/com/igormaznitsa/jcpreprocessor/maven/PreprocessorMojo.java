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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
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
     * @parameter name="source"
     * @readonly
     */
    private String source;
    /**
     * Destination directory
     * 
     * @parameter name="destination" default-value="${project.build.directory}/generated-sources/preprocessed"
     * @readonly
     */
    private File destination;
    /**
     * Input text character encoding
     * 
     * @parameter name="inEncoding" default-value="${project.build.sourceEncoding}"
     * @readonly
     */
    private String inEncoding;
    /**
     * Output text character encoding
     * 
     * @parameter name="outEncoding" default-value="${project.build.sourceEncoding}"
     * @readonly
     */
    private String outEncoding;
    /**
     * List of excluded extensions from preprocessing
     * 
     * @parameter name="excluded"
     * @readonly
     */
    private String excluded;
    /**
     * List of extensions to be preprocessed
     * 
     * @parameter name="processing"
     * @readonly
     */
    private String processing;
    /**
     * Disable any output file operations
     * 
     * @parameter name="disableOut" default-value="false"
     * @readonly
     */
    private boolean disableOut;
    /**
     * Make verbose message output for inside processes
     * 
     * @parameter name="verbose" default-value="false"
     * @readonly
     */
    private boolean verbose;
    /**
     * Clear the destination directory before preprocessing if it is existing
     * 
     * @parameter name="clear" default-value="false"
     * @readonly
     */
    private boolean clearDestination;
    /**
     * Flag to disable overriding of the source root directory by preprocessed directory
     * 
     * @parameter name="keepSrcRoot" default-value="false" 
     */
    private boolean keepSrcRoot;
    /**
     * Remove all Java like commentaries from the resulting files
     * 
     * @parameter name="removeComments" default-value="false"
     * @readonly
     */
    private boolean removeComments;
    /**
     * Global variables
     * 
     * @parameter name="globalVars"
     * @readonly
     */
    private Properties globalVars;
    /**
     * Configuration files
     * 
     * @parameter name="cfgFiles"
     * @readonly
     */
    private File[] cfgFiles;
    /**
     * The variable contains the processed variable map
     */
    private Map<String, Value> _variableMap;

    public void setKeepSrcRoot(final boolean flag){
        this.keepSrcRoot = flag;
    }
    
    public boolean getKeepSrcRoot(){
        return this.keepSrcRoot;
    }
    
    public void setGlobalVars(final Properties vars) {
        this.globalVars = vars;
    }

    public Properties getGlobalVars() {
        return this.globalVars;
    }

    public void setCfgFiles(final File[] files) {
        this.cfgFiles = files;
    }

    public File[] getCfgFiles() {
        return this.cfgFiles;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public String getSource() {
        return this.source;
    }

    public void setDestination(final File destination) {
        this.destination = destination;
    }

    public File getDestination() {
        return this.destination;
    }

    public void setInEncoding(final String value) {
        this.inEncoding = value;
    }

    public String getInEncoding() {
        return this.inEncoding;
    }

    public void setOutEncoding(final String value) {
        this.outEncoding = value;
    }

    public String getOutEncoding() {
        return this.outEncoding;
    }

    public void setExcluded(final String excluded) {
        this.excluded = excluded;
    }

    public String getExcluded() {
        return this.excluded;
    }

    public void setProcessing(final String processing) {
        this.processing = processing;
    }

    public String getProcessing() {
        return this.processing;
    }

    public void setDisableOut(final boolean value) {
        this.disableOut = value;
    }

    public boolean getDisableOut() {
        return this.disableOut;
    }

    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    public boolean getVerbose() {
        return this.verbose;
    }

    public void setRemoveComments(final boolean value) {
        this.removeComments = value;
    }

    public boolean getRemoveComments() {
        return this.removeComments;
    }

    private void fillVariableMap() throws Exception {

        final Map<String, Value> result = new HashMap<String, Value>();

        if (project != null) {
            final Properties props = project.getProperties();

            final Enumeration keys = props.keys();
            while (keys.hasMoreElements()) {
                final String key = (String) keys.nextElement();
                final String keyStr = "mvn." + key.toLowerCase();
                final String value = props.getProperty(key.toString(), "value_undefined");
                result.put(key, Value.recognizeRawString(value));
            }

            result.put("mvn.basedir", Value.recognizeRawString(project.getBasedir().getCanonicalPath()));
            result.put("mvn.project.build.directory", Value.recognizeRawString(project.getBuild().getDirectory()));
            result.put("mvn.project.build.outputDirectory", Value.recognizeRawString(project.getBuild().getOutputDirectory()));
            result.put("mvn.project.name", Value.recognizeRawString(project.getName()));
            result.put("mvn.project.version", Value.recognizeRawString(project.getVersion()));
            result.put("mvn.project.build.finalname", Value.recognizeRawString(project.getBuild().getFinalName()));
        } else {
            warning("Project object is null");
        }

        _variableMap = result;
    }

    private String makeSourceRootList() {
        String result = null;
        if (source != null) {
            result = source;
        } else if (project != null) {
            final StringBuilder accum = new StringBuilder();
            for (final String srcRoot : project.getCompileSourceRoots()) {
                if (accum.length() > 0) {
                    accum.append(';');
                }
                accum.append(srcRoot);
            }
            result = accum.toString();
        }
        return result;
    }

    private void addPreprocessedAsSourceRoot(final PreprocessorContext context) throws IOException {
        if (project != null) {
            final String sourceDirectories = context.getSourceDirectory();
            final String[] splitted = sourceDirectories.split(";");
            
            final List<String> sourceRoots = project.getCompileSourceRoots();
            final List<String> sourceRootsAsCanonical = new ArrayList<String>();
            for(final String src : sourceRoots){
                sourceRootsAsCanonical.add(new File(src).getCanonicalPath());
            }
            
            for (final String str : splitted) {
                int index = sourceRoots.indexOf(str);
                if (index<0){
                    // check for canonical paths
                    final File source = new File(str);
                    final String canonicalPath = source.getCanonicalPath();
                    index = sourceRootsAsCanonical.indexOf(canonicalPath);
                }
                if (index>=0){
                    info("A Compile source root has been removed from the root list ["+sourceRoots.get(index)+']');
                    sourceRoots.remove(index);
                }
            }
            
            final String destinationDir = context.getDestinationDirectoryAsFile().getCanonicalPath();
            
            sourceRoots.add(destinationDir);
            info("The New compile source root has been added into the list ["+destinationDir+']');
        }
    }

    PreprocessorContext makePreprocessorContext() throws IOException {
        final PreprocessorContext context = new PreprocessorContext();

        context.setSourceDirectory(makeSourceRootList());

        context.setDestinationDirectory(destination.getCanonicalPath());

        if (inEncoding != null) {
            context.setInCharacterEncoding(inEncoding);
        }
        if (outEncoding != null) {
            context.setOutCharacterEncoding(outEncoding);
        }
        if (excluded != null) {
            context.setExcludedFileExtensions(excluded);
        }
        if (processing != null) {
            context.setProcessingFileExtensions(processing);
        }

        info("Preprocessing sources folder : " + context.getSourceDirectory());
        info("Preprocessing destination folder : " + context.getDestinationDirectory());

        context.setClearDestinationDirBefore(clearDestination);
        context.setRemoveComments(removeComments);
        context.setVerbose(verbose);
        context.setFileOutputDisabled(disableOut);

        // process cfg files
        if (cfgFiles != null && cfgFiles.length != 0) {
            for (final File file : cfgFiles) {
                if (file == null) {
                    throw new NullPointerException("A NULL in place of a config file detected");
                }

                context.addConfigFile(file);
            }
        }

        // process global vars
        if (globalVars != null && !globalVars.isEmpty()) {
            for (final String key : globalVars.stringPropertyNames()) {
                final String value = globalVars.getProperty(key);
                if (value == null) {
                    throw new NullPointerException("Can't find defined value for '" + key + "' global variable");
                }
                context.setGlobalVariable(key, Value.recognizeRawString(value));
            }
        }

        return context;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            fillVariableMap();
        } catch (Exception unexpected) {
            throw new MojoExecutionException("Exception during project properties reading", unexpected);
        }

        PreprocessorContext context = null;

        try {
            context = makePreprocessorContext();
            context.registerSpecialVariableProcessor(this);
            context.setPreprocessorLogger(this);
        } catch (Exception ex) {
            throw new MojoExecutionException("Exception during preprocessor context creation", ex);
        }

        try {
            final JCPreprocessor preprocessor = new JCPreprocessor(context);
            preprocessor.execute();
            if (!getKeepSrcRoot()){
                addPreprocessedAsSourceRoot(context);
            }
        } catch (Exception ex) {
            throw new MojoFailureException("Exception during preprocessing or preparation", ex);
        }

    }

    private void overrideSourceRoot() {
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
        return _variableMap.keySet().toArray(new String[_variableMap.size()]);
    }

    @Override
    public Value getVariable(final String varName, final PreprocessorContext context) {
        Value result = null;
        if (_variableMap != null) {
            result = _variableMap.get(varName);
            if (result == null) {
                throw new IllegalStateException("Detected request for a nonexsitiong variable [" + varName + ']');
            }
        }
        return result;
    }

    @Override
    public void setVariable(String varName, Value value, PreprocessorContext context) {
        throw new UnsupportedOperationException("Writiong operation disallowed for maven properties [" + varName + ']');
    }
}
