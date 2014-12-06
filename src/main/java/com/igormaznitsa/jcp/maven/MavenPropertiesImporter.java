/* 
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.jcp.maven;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.context.SpecialVariableProcessor;
import com.igormaznitsa.jcp.expression.Value;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;

/**
 * The class imports some properties from the maven which can be accessible from
 * preprocessed sources as global variables
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class MavenPropertiesImporter implements SpecialVariableProcessor {

  private static final String[] TO_IMPORT = {
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

  private final Map<String, Value> insideVarMap = new HashMap<String, Value>();
  private final MavenProject project;

  private void printInfoAboutVarIntoLog(final PreprocessorContext context, final String varName, final String value) {
    context.logInfo("Added MAVEN property " + varName + '=' + value);
  }

  private void addVariableIntoInsideMap(final PreprocessorContext context, final String name, final Value value) {
    if (insideVarMap.containsKey(name)) {
      throw context.makeException("Duplicated importing value detected [" + name + ']',null);
    }
    insideVarMap.put(name, value);
    printInfoAboutVarIntoLog(context, name, value.asString());
  }

  public MavenPropertiesImporter(final PreprocessorContext context, final MavenProject project) {
    this.project = project;
    for (final String paramName : TO_IMPORT) {
      final String varName = "mvn." + paramName.toLowerCase(Locale.ENGLISH);
      final String value = getProperty(this.project, paramName);
      addVariableIntoInsideMap(context, varName, Value.valueOf(value));
    }

    // add active profile ids
    final StringBuilder profileIds = new StringBuilder();
    for (final Profile profile : project.getActiveProfiles()) {
      if (profileIds.length() > 0) {
        profileIds.append(';');
      }
      profileIds.append(profile.getId());
    }
    addVariableIntoInsideMap(context, "mvn.project.activeprofiles", Value.valueOf(profileIds.toString()));

    // add properties
    for (final String propertyName : this.project.getProperties().stringPropertyNames()) {
      final String varName = "mvn.project.property." + propertyName.toLowerCase().replace(' ', '_');
      final String value = this.project.getProperties().getProperty(propertyName);
      addVariableIntoInsideMap(context, varName, Value.valueOf(value));
    }
  }

  static String getProperty(final MavenProject project, final String name) {
    final String[] splitted = name.split("\\.");

    Object root = null;

    if ("project".equals(splitted[0])) {
      root = project;
    }

    try {
      if (root == null) {
        throw new IllegalArgumentException("Unsupported root object detected [" + splitted[0] + ']');
      }
      else {
        for (int i = 1; i < splitted.length - 1; i++) {
          final Method getter = root.getClass().getMethod(normalizeGetter(splitted[i]));
          root = getter.invoke(root);
          if (root == null) {
            return "";
          }
        }

        final Method finalStringGetter = root.getClass().getMethod(normalizeGetter(splitted[splitted.length - 1]));
        final Object result = finalStringGetter.invoke(root);
        return result == null ? "" : result.toString();
      }
    }
    catch (NoSuchMethodException ex) {
      throw new RuntimeException("Can't find method", ex);
    }
    catch (IllegalAccessException ex) {
      throw new RuntimeException("Security exception", ex);
    }
    catch (InvocationTargetException ex) {
      throw new RuntimeException("Exception during invocation", ex.getCause());
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
    if (!insideVarMap.containsKey(varName)) {
      throw new IllegalArgumentException("Unsupported property request detected [" + varName + ']');
    }
    return insideVarMap.get(varName);
  }

  @Override
  public void setVariable(final String varName, final Value value, final PreprocessorContext context) {
    throw new UnsupportedOperationException("An attempt to change a maven property detected, those properties are accessible only for reading [" + varName + ']');
  }
}
