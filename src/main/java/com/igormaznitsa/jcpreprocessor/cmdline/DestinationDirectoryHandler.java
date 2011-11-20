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
package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

/**
 * The handler for the output directory command line key
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class DestinationDirectoryHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/O:";

    public String getDescription() {
        return "set the destination directory (default is " + PreprocessorContext.DEFAULT_DEST_DIRECTORY+')';
    }

    public boolean processCommandLineKey(final String key, final PreprocessorContext context) {
        boolean result = false;

        if (key != null && !key.isEmpty()) {

            if (key.toUpperCase().startsWith(ARG_NAME)) {
                final String name = PreprocessorUtils.extractTrimmedTail(ARG_NAME, key);
                if (!name.isEmpty()) {
                    context.setDestinationDirectory(PreprocessorUtils.extractTail(ARG_NAME, key));
                    result = true;
                }
            }
        }
        return result;
    }

    public String getKeyName() {
        return ARG_NAME;
    }
}
