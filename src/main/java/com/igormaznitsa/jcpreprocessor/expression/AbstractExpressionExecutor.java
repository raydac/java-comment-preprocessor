package com.igormaznitsa.jcpreprocessor.expression;

import java.io.File;

public interface AbstractExpressionExecutor {
    void execute(Expression stack, int index);
}
