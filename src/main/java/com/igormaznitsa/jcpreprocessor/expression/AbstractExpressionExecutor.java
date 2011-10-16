package com.igormaznitsa.jcpreprocessor.expression;

import java.io.File;

public interface AbstractExpressionExecutor {
    void execute(File currentFile, Expression stack, int index);
}
