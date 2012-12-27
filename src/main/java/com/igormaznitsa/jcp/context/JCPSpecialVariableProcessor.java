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
package com.igormaznitsa.jcp.context;

import com.igormaznitsa.jcp.InfoHelper;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;

/**
 * The class implements the special variable processor interface and allows to
 * get access to inside JCP variables Inside JCP variables have the "jcp."
 * prefix
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class JCPSpecialVariableProcessor implements SpecialVariableProcessor {

    public static final String VAR_DEST_DIR = "jcp.dst.dir";
    public static final String VAR_VERSION = "jcp.version";
    public static final String VAR_DEST_FILE_NAME = "jcp.dst.name";
    public static final String VAR_DEST_FULLPATH = "jcp.dst.path";
    public static final String VAR_SRC_FILE_NAME = "jcp.src.name";
    public static final String VAR_SRC_DIR = "jcp.src.dir";
    public static final String VAR_SRC_FULLPATH = "jcp.src.path";

    @Override
    public String[] getVariableNames() {
        return new String[]{
                    VAR_DEST_DIR,
                    VAR_DEST_FILE_NAME,
                    VAR_DEST_FULLPATH,
                    VAR_SRC_DIR,
                    VAR_SRC_FILE_NAME,
                    VAR_SRC_FULLPATH,
                    VAR_VERSION
                };
    }

    @Override
    public Value getVariable(final String varName, final PreprocessorContext context) {
        final PreprocessingState state = context == null ? null : context.getPreprocessingState();
        if (VAR_DEST_DIR.equals(varName)) {
            return Value.valueOf(state.getRootFileInfo().getDestinationDir());
        } else if (VAR_DEST_FILE_NAME.equals(varName)) {
            return Value.valueOf(state.getRootFileInfo().getDestinationName());
        } else if (VAR_DEST_FULLPATH.equals(varName)) {
            return Value.valueOf(state.getRootFileInfo().getDestinationFilePath());
        } else if (VAR_SRC_DIR.equals(varName)) {
            return Value.valueOf(state.getRootFileInfo().getSourceFile().getParent());
        } else if (VAR_SRC_FILE_NAME.equals(varName)) {
            return Value.valueOf(state.getRootFileInfo().getSourceFile().getName());
        } else if (VAR_SRC_FULLPATH.equals(varName)) {
            return Value.valueOf(PreprocessorUtils.getFilePath(state.getRootFileInfo().getSourceFile()));
        } else if (VAR_VERSION.equals(varName)) {
            return Value.valueOf(InfoHelper.getVersion());
        } else {
            throw new IllegalArgumentException("Attemption to get unsupported variable [" + varName + ']');
        }
    }

    @Override
    public void setVariable(final String varName, final Value value, final PreprocessorContext context) {
        final PreprocessingState state = context == null ? null : context.getPreprocessingState();
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
                || VAR_SRC_FILE_NAME.equals(varName)
                || VAR_SRC_FULLPATH.equals(varName)
                || VAR_VERSION.equals(varName)) {
            throw new UnsupportedOperationException("The variable \'" + varName + "\' can't be set directly");
        } else {
            throw new IllegalStateException("Attemption to change an unsupported variable [" + varName + ']');
        }
    }
}
