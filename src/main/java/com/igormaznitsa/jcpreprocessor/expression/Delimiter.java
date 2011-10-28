package com.igormaznitsa.jcpreprocessor.expression;

import java.util.Collections;
import java.util.Map;

public final class Delimiter implements ExpressionStackItem {

    public static final Map<String, Delimiter> DELIMITER_MAP = Collections.singletonMap(",", new Delimiter(","));
    private final String delimiterText;

    private Delimiter(final String text) {
        if (text == null) {
            throw new NullPointerException("Text is null");
        }
        delimiterText = text;
    }

    public static Delimiter valueOf(final String value) {
        return DELIMITER_MAP.get(value);
    }

    @Override
    public int hashCode() {
        return delimiterText.hashCode();
    }
    
    @Override
    public boolean equals(final Object that) {
        if (that == null) {
            return false;
        }

        if (this == that) {
            return true;
        }

        if (that.getClass() == Delimiter.class) {
            return delimiterText.equals(((Delimiter) that).delimiterText);
        }
        return false;
    }

    @Override
    public String toString() {
        return "DELIMETER \'" + delimiterText + "\'";
    }

    public ExpressionStackItemType getStackItemType() {
        return ExpressionStackItemType.DELIMITER;
    }

    public int getPriority() {
        return -1;
    }
    
    
}
