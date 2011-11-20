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
package com.igormaznitsa.jcpreprocessor.directives;

/**
 * The enumeration contains flags after directive processing behavior
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public enum AfterDirectiveProcessingBehaviour {
    /**
     * Notify preprocessor that a directive has been processed successfully
     */
    PROCESSED,
    /**
     * Notify preprocessor that a directive has been processed and need to read the next line immediately
     */
    READ_NEXT_LINE,
    /**
     * Notify preprocessor that the directive has not been processed
     */
    NOT_PROCESSED
}
