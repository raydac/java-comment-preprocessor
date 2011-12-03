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
package com.igormaznitsa.jcp.maven;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.context.SpecialVariableProcessor;
import com.igormaznitsa.jcp.expression.Value;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.project.MavenProject;

/**
 * The class imports some properties from the maven which can be accessible from preprocessed sources as global variables
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class MavenPropertiesImporter implements SpecialVariableProcessor {

    private static final String[] IMPORTED_VARIABLES = {
        "project.name",
        "project.version",
        "project.url",
        "project.packaging",
        "project.modelVersion",
        "project.inceptionYear",
        "project.id",
        "project.groupId",
        "project.description",
        
        "project.artifact.id",
        "project.artifact.artifactId",
        "project.artifact.baseVersion",
        "project.artifact.dependencyConflictId",
        "project.artifact.downloadUrl",
        "project.artifact.groupId",
        "project.artifact.scope",
        "project.artifact.type",
        "project.artifact.version",
        
        "project.build.directory",
        "project.build.defaultGoal",
        "project.build.outputDirectory",
        "project.build.scriptSourceDirectory",
        "project.build.sourceDirectory",
        "project.build.testOutputDirectory",
        "project.build.testSourceDirectory",
        
        "project.organization.name",
        "project.organization.url"};
    
    private Map<String, Value> insideVarMap = new HashMap<String, Value>();
    private final MavenProject project;

    public MavenPropertiesImporter(final PreprocessorContext context, final MavenProject project) {
        this.project = project;
        for (final String paramName : IMPORTED_VARIABLES) {
            final String varName = "mvn." + paramName.toLowerCase();
            
            if (insideVarMap.containsKey(varName)) {
                throw new IllegalStateException("Duplicated imported value detected [" + paramName + ']');
            }

            try {
                final String value = getProperty(this.project,paramName);
                insideVarMap.put(varName, Value.valueOf(value));
                context.logInfo("Added MAVEN property " + varName + '=' + value);
            } catch (Exception ex) {
                context.logError("Exception during importing maven property '" + paramName + '\'');
            }
        }
        
        // add properties
        for(final String propertyName : this.project.getProperties().stringPropertyNames()){
            final String varName = "mvn.project.property."+propertyName.toLowerCase().replace(' ','_');
            final String value = this.project.getProperties().getProperty(propertyName);
            
            if (value == null){
                throw new IllegalStateException("Impossible state for property "+propertyName);
            }

            if (insideVarMap.containsKey(varName)) {
                throw new IllegalStateException("Property overrides a variable [" + varName + ']');
            }
            
            insideVarMap.put(varName, Value.valueOf(value));
            context.logInfo("Added MAVEN property " + varName + '=' + value);
        }
    }

    static String getProperty(final MavenProject project, final String name) throws Exception {
        final String[] splitted = name.split("\\.");

        Object root = null;

        if ("project".equals(splitted[0])) {
            root = project;
        }

        if (root == null) {
            throw new IllegalArgumentException("Unsupported root object detected [" + splitted[0] + ']');
        } else {
            for (int i = 1; i < splitted.length - 1; i++) {
                final Method getter = root.getClass().getMethod(normalizeGetter(splitted[i]));
                root = getter.invoke(root);
            }

            final Method finalStringGetter = root.getClass().getMethod(normalizeGetter(splitted[splitted.length - 1]));
            final Object result = finalStringGetter.invoke(root);
            return result == null ? "" : result.toString();
        }
    }

    static String normalizeGetter(final String str) {
        return "get" + Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    @Override
    public String[] getVariableNames() {
        return insideVarMap.keySet().toArray(new String[insideVarMap.size()]);
    }

    @Override
    public Value getVariable(final String varName, final PreprocessorContext context) {
        if (!insideVarMap.containsKey(varName)){
            throw new IllegalArgumentException("Unsupported property request detected ["+varName+']');
        }
        return insideVarMap.get(varName);
    }

    @Override
    public void setVariable(final String varName, final Value value, final PreprocessorContext context) {
        throw new UnsupportedOperationException("An attempt to change a maven property detected, those properties are accessible only for reading ["+varName+']');
    }
}
