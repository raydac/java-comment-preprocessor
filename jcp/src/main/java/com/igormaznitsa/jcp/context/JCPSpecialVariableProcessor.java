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

package com.igormaznitsa.jcp.context;


import com.igormaznitsa.jcp.InfoHelper;
import com.igormaznitsa.jcp.containers.TextFileDataContainer;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import com.igormaznitsa.meta.annotation.MustNotContainNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The class implements the special variable processor interface and allows to get access to inside JCP variables Inside JCP variables have the "jcp." prefix
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class JCPSpecialVariableProcessor implements SpecialVariableProcessor {

  public static final String VAR_DEST_DIR = "jcp.dst.dir";
  public static final String VAR_VERSION = "jcp.version";
  public static final String VAR_DEST_FILE_NAME = "jcp.dst.name";
  public static final String VAR_DEST_FULLPATH = "jcp.dst.path";
  public static final String VAR_SRC_FILE_NAME = "jcp.src.name";
  public static final String VAR_SRC_FILE_NAME2 = "__filename__";
  public static final String VAR_SRC_DIR = "jcp.src.dir";
  public static final String VAR_SRC_DIR2 = "__filefolder__";
  public static final String VAR_SRC_FULLPATH = "jcp.src.path";
  public static final String VAR_SRC_FULLPATH2 = "__file__";
  public static final String VAR_LINE = "__line__";
  public static final String VAR_DATE = "__date__";
  public static final String VAR_TIME = "__time__";
  public static final String VAR_TIMESTAMP = "__timestamp__";

  final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");
  final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
  final SimpleDateFormat timestampFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");

  @Nonnull
  @MustNotContainNull
  public static List<NameReferencePair> getReference() {
    final List<NameReferencePair> result = new ArrayList<>();

    result.add(new NameReferencePair(VAR_VERSION, "The Preprocessor version"));
    result.add(new NameReferencePair(VAR_SRC_FULLPATH, "Full path to the current preprocessing file, read only"));
    result.add(new NameReferencePair(VAR_SRC_FULLPATH2, "The Synonym for '" + VAR_DEST_FULLPATH + "', read only"));
    result.add(new NameReferencePair(VAR_SRC_DIR, "The Current preprocessing file folder, read only"));
    result.add(new NameReferencePair(VAR_SRC_DIR2, "The Synonym for '" + VAR_SRC_DIR + "', read only"));
    result.add(new NameReferencePair(VAR_SRC_FILE_NAME, "The Current preprocessing file name, read only"));
    result.add(new NameReferencePair(VAR_SRC_FILE_NAME2, "The Synonym for '" + VAR_SRC_FILE_NAME + "', read only"));

    result.add(new NameReferencePair(VAR_LINE, "The Current preprocessing line number in the current source file, read only"));
    result.add(new NameReferencePair(VAR_DEST_FULLPATH, "The Full Destination File path for the preprocessing file, read only"));
    result.add(new NameReferencePair(VAR_DEST_DIR, "The Destination File path for the preprocessing file, read only"));
    result.add(new NameReferencePair(VAR_DEST_FILE_NAME, "The Destination File name for the preprocessing file, allowed for reading and writing"));

    result.add(new NameReferencePair(VAR_TIME, "The Current time"));
    result.add(new NameReferencePair(VAR_DATE, "The Current date"));
    result.add(new NameReferencePair(VAR_TIMESTAMP, "The Timestamp of the current source file"));

    return result;
  }

  @Override
  @Nonnull
  @MustNotContainNull
  public String[] getVariableNames() {
    return new String[] {
        VAR_DEST_DIR,
        VAR_DEST_FILE_NAME,
        VAR_DEST_FULLPATH,
        VAR_SRC_DIR,
        VAR_SRC_DIR2,
        VAR_SRC_FILE_NAME,
        VAR_SRC_FILE_NAME2,
        VAR_SRC_FULLPATH,
        VAR_SRC_FULLPATH2,
        VAR_VERSION,
        VAR_LINE,
        VAR_TIME,
        VAR_TIMESTAMP,
        VAR_DATE
    };
  }

  @Override
  @Nullable
  public Value getVariable(@Nonnull final String varName, @Nonnull final PreprocessorContext context) {
    final PreprocessingState state = context.getPreprocessingState();

    if (VAR_DEST_DIR.equals(varName)) {
      return Value.valueOf(state.getRootFileInfo().getDestinationDir());
    } else if (VAR_DEST_FILE_NAME.equals(varName)) {
      return Value.valueOf(state.getRootFileInfo().getDestinationName());
    } else if (VAR_DEST_FULLPATH.equals(varName)) {
      return Value.valueOf(state.getRootFileInfo().getDestinationFilePath());
    } else if (VAR_SRC_DIR.equals(varName) || VAR_SRC_DIR2.equals(varName)) {
      return Value.valueOf(state.getRootFileInfo().getSourceFile().getParent());
    } else if (VAR_SRC_FILE_NAME.equals(varName) || VAR_SRC_FILE_NAME2.equals(varName)) {
      return Value.valueOf(state.getRootFileInfo().getSourceFile().getName());
    } else if (VAR_SRC_FULLPATH.equals(varName) || VAR_SRC_FULLPATH2.equals(varName)) {
      return Value.valueOf(PreprocessorUtils.getFilePath(state.getRootFileInfo().getSourceFile()));
    } else if (VAR_VERSION.equals(varName)) {
      return Value.valueOf(InfoHelper.getVersion());
    } else if (VAR_TIME.equals(varName)) {
      return Value.valueOf(timeFormat.format(new Date()));
    } else if (VAR_DATE.equals(varName)) {
      return Value.valueOf(dateFormat.format(new Date()));
    } else if (VAR_TIMESTAMP.equals(varName)) {
      final TextFileDataContainer filedata = state.peekFile();
      final Value result;
      if (filedata == null) {
        result = Value.valueOf("<no file>");
      } else {
        result = Value.valueOf(timestampFormat.format(new Date(filedata.getFile().lastModified())));
      }
      return result;
    } else if (VAR_LINE.equals(varName)) {
      final TextFileDataContainer currentFile = state.peekFile();
      final long line;
      if (currentFile == null) {
        line = -1L;
      } else {
        line = currentFile.getLastReadStringIndex() + 1;
      }
      return Value.valueOf(line);
    } else {
      final String text = "Attempting to read unexpected special variable [" + varName + ']';
      throw context.makeException(text, null);
    }
  }

  @Override
  public void setVariable(@Nonnull final String varName, @Nonnull final Value value, @Nonnull final PreprocessorContext context) {
    final PreprocessingState state = context.getPreprocessingState();
    if (VAR_DEST_DIR.equals(varName)) {
      if (value.getType() != ValueType.STRING) {
        throw new IllegalArgumentException("Only STRING type allowed");
      }
      state.getRootFileInfo().setDestinationDir(value.asString());
    } else if (VAR_DEST_FILE_NAME.equals(varName)) {
      if (value.getType() != ValueType.STRING) {
        throw new IllegalArgumentException("Only STRING type allowed");
      }
      state.getRootFileInfo().setDestinationName(value.asString());
    } else if (VAR_DEST_FULLPATH.equals(varName)
        || VAR_SRC_DIR.equals(varName)
        || VAR_SRC_DIR2.equals(varName)
        || VAR_SRC_FILE_NAME.equals(varName)
        || VAR_SRC_FILE_NAME2.equals(varName)
        || VAR_SRC_FULLPATH.equals(varName)
        || VAR_SRC_FULLPATH2.equals(varName)
        || VAR_VERSION.equals(varName)
        || VAR_LINE.equals(varName)
        || VAR_TIME.equals(varName)
        || VAR_TIMESTAMP.equals(varName)
        || VAR_DATE.equals(varName)) {
      final String text = "The variable \'" + varName + "\' can't be set directly";
      throw context.makeException(text, null);
    } else {
      final String text = "Attempting to write unexpected special variable [" + varName + ']';
      throw context.makeException(text, null);
    }
  }

  public static final class NameReferencePair {

    private final String name;
    private final String reference;

    private NameReferencePair(@Nonnull final String name, @Nonnull final String reference) {
      this.name = name;
      this.reference = reference;
    }

    @Nonnull
    public String getName() {
      return this.name;
    }

    @Nonnull
    public String getReference() {
      return this.reference;
    }
  }
}
