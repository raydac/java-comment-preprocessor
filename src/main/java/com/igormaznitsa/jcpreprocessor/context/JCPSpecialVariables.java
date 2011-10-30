package com.igormaznitsa.jcpreprocessor.context;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;

public class JCPSpecialVariables implements PreprocessorContext.SpecialVariableProcessor{
    public static final String VAR_DEST_DIR = "jcp.dst.dir";
    public static final String VAR_DEST_FILE_NAME = "jcp.dst.name";
    public static final String VAR_DEST_FULLPATH = "jcp.dst.path";
    public static final String VAR_SRC_FILE_NAME = "jcp.src.name";
    public static final String VAR_SRC_DIR = "jcp.src.dir";
    public static final String VAR_SRC_FULLPATH = "jcp.src.path";

    public String[] getVariableNames() {
        return new String[]{VAR_DEST_DIR, VAR_DEST_FILE_NAME, VAR_DEST_FULLPATH, VAR_SRC_DIR, VAR_SRC_FILE_NAME, VAR_SRC_FULLPATH};
    }

    public Value getVariable(final String varName, final PreprocessorContext context, final PreprocessingState state) {
        if (VAR_DEST_DIR.equals(varName)){
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
            return Value.valueOf(state.getRootFileInfo().getSourceFile().getAbsolutePath());
        } else 
            throw new IllegalStateException("Attemption to get unsupported variable ["+varName+']');
    }

    public void setVariable(final String varName, final Value value, final PreprocessorContext context, final PreprocessingState state) {
        if (VAR_DEST_DIR.equals(varName)){
            if (value.getType()!=ValueType.STRING) throw new IllegalArgumentException("Only STRING type allowed");
            state.getRootFileInfo().setDestinationDir(value.asString());
        } else if (VAR_DEST_FILE_NAME.equals(varName)) {
            if (value.getType()!=ValueType.STRING) throw new IllegalArgumentException("Only STRING type allowed");
            state.getRootFileInfo().setDestinationName(value.asString());
        } else if (VAR_DEST_FULLPATH.equals(varName) || VAR_SRC_DIR.equals(varName) || VAR_SRC_FILE_NAME.equals(varName) || VAR_SRC_FULLPATH.equals(varName)) {
           throw new RuntimeException("The variable \'"+varName+"\' can't be set");
       } else 
            throw new IllegalStateException("Attemption to set unsupported variable ["+varName+']');
    }
}
