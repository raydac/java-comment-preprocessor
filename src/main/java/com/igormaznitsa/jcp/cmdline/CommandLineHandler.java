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
package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;

/**
 * The interface describes a command line key handler. It is not just a handler but it will be called for all met keys to recognize one to be processed.
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public interface CommandLineHandler {
    /**
     * Get the key name for the handler
     * @return the key name as a String, must not be null
     */
    String getKeyName();
    
    /**
     * Get the description of the key (it will be printed into the help text)
     * @return the description as a String
     */
    String getDescription();
    
    /**
     * Process a command line key
     * @param key the command line key to be processed, must not be null
     * @param context the preprocessor context, must not be null
     * @return true if the key has been recognized and processed else false 
     */
    boolean processCommandLineKey(String key, PreprocessorContext context);
}
