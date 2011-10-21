package com.igormaznitsa.jcpreprocessor.directives;

public abstract class DirectiveHandler {
    public abstract String getName();
    public abstract boolean hasSpaceOrEndAfter();
}
