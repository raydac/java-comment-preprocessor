/*
 * Copyright 2002-2019 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.igormaznitsa.jcp.maven;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.context.SpecialVariableProcessor;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.meta.annotation.MustNotContainNull;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The class imports some properties from the maven which can be accessible from preprocessed sources as global variables
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class MavenPropertiesImporter implements SpecialVariableProcessor {

  private static final Pattern PATTERN_FOR_PROPERTY_WHICH_CAN_CONTAIN_PRIVATE_INFO = Pattern.compile("key|pass", Pattern.CASE_INSENSITIVE);

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

  public MavenPropertiesImporter(@Nonnull final PreprocessorContext context, @Nonnull final MavenProject project, final boolean logAddedProperties) {
    this.project = project;
    for (final String paramName : TO_IMPORT) {
      final String varName = "mvn." + paramName.toLowerCase(Locale.ENGLISH);
      final String value = getProperty(this.project, paramName);
      addVariableIntoInsideMap(context, varName, Value.valueOf(value), logAddedProperties);
    }

    // add active profile ids
    final StringBuilder profileIds = new StringBuilder();
    for (final Profile profile : project.getActiveProfiles()) {
      if (profileIds.length() > 0) {
        profileIds.append(';');
      }
      profileIds.append(profile.getId());
    }
    addVariableIntoInsideMap(context, "mvn.project.activeprofiles", Value.valueOf(profileIds.toString()), logAddedProperties);

    // add properties
    for (final String propertyName : this.project.getProperties().stringPropertyNames()) {
      final String varName = "mvn.project.property." + propertyName.toLowerCase(Locale.ENGLISH).replace(' ', '_');
      final String value = this.project.getProperties().getProperty(propertyName);
      addVariableIntoInsideMap(context, varName, Value.valueOf(value), logAddedProperties);
    }
  }

  @Nonnull
  static String getProperty(@Nonnull final MavenProject project, @Nonnull final String name) {
    final String[] splitted = name.split("\\.");

    Object root = null;

    if ("project".equals(splitted[0])) {
      root = project;
    }

    try {
      if (root == null) {
        throw new IllegalArgumentException("Unsupported root object detected [" + splitted[0] + ']');
      } else {
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
    } catch (NoSuchMethodException ex) {
      throw new RuntimeException("Can't find method", ex);
    } catch (IllegalAccessException ex) {
      throw new RuntimeException("Security exception", ex);
    } catch (InvocationTargetException ex) {
      throw new RuntimeException("Exception during invocation", ex.getCause());
    }
  }

  @Nonnull
  static String normalizeGetter(@Nonnull final String str) {
    return "get" + Character.toUpperCase(str.charAt(0)) + str.substring(1);
  }

  private void printInfoAboutVarIntoLog(@Nonnull final PreprocessorContext context, @Nonnull final String varName, @Nonnull final String value) {
    final boolean possibleContainsPrivateInfo = PATTERN_FOR_PROPERTY_WHICH_CAN_CONTAIN_PRIVATE_INFO.matcher(varName).find();
    final String textValue = possibleContainsPrivateInfo ? "***** [hidden because may contain private info]" : value;
    context.logInfo("Added MAVEN property " + varName + '=' + textValue);
  }

  private void addVariableIntoInsideMap(@Nonnull final PreprocessorContext context, @Nonnull final String name, @Nonnull final Value value, final boolean verbose) {
    if (insideVarMap.containsKey(name)) {
      throw context.makeException("Duplicated importing value detected [" + name + ']', null);
    }
    insideVarMap.put(name, value);
    if (verbose) {
      printInfoAboutVarIntoLog(context, name, value.asString());
    }
  }

  @Override
  @Nonnull
  @MustNotContainNull
  public String[] getVariableNames() {
    return insideVarMap.keySet().toArray(new String[insideVarMap.size()]);
  }

  @Override
  @Nullable
  public Value getVariable(@Nonnull final String varName, @Nonnull final PreprocessorContext context) {
    if (!insideVarMap.containsKey(varName)) {
      throw new IllegalArgumentException("Unsupported property request detected [" + varName + ']');
    }
    return insideVarMap.get(varName);
  }

  @Override
  public void setVariable(@Nonnull final String varName, @Nonnull final Value value, @Nonnull final PreprocessorContext context) {
    throw new UnsupportedOperationException("An attempt to change a maven property detected, those properties are accessible only for reading [" + varName + ']');
  }
}
