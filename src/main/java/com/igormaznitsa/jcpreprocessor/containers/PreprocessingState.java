package com.igormaznitsa.jcpreprocessor.containers;

public enum PreprocessingState {

    /**
     * This flag shows that it is allowed to print texts into an output stream
     */
    TEXT_OUTPUT_DISABLED,
    /**
     * This flag shows that we must comment the next line (one time flag)
     */
    COMMENT_NEXT_LINE,
    /**
     * This flag shows that the current //#if construction in the passive state
     */
    IF_CONDITION_FALSE,
    /**
     * This flag shows that //#break has been met
     */
    BREAK_COMMAND,
    /**
     * This flag shows that preprocessing must be ended on the next string
     */
    END_PROCESSING
}
