package com.igormaznitsa.jcpreprocessor.directives;

public enum AfterProcessingBehaviour {
    /**
     * Processed
     */
    PROCESSED,
    /**
     * Processed and need to read the next line immediately
     */
    READ_NEXT_LINE,
    /**
     * The directive has not been processed
     */
    NOT_PROCESSED
}
