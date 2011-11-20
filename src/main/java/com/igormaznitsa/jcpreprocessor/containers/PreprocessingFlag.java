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
package com.igormaznitsa.jcpreprocessor.containers;

public enum PreprocessingFlag {

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
