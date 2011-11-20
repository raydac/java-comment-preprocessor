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

public class ClearDstDirectoryHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/C";
    
    public String getDescription() {
        return "the destination directory will be cleared before processing";
    }

    public boolean processArgument(final String argument, final PreprocessorContext configurator) {
        if (ARG_NAME.equalsIgnoreCase(argument)){
            configurator.setClearDestinationDirBefore(true);
            return true;
        } else {
            return false;
        }
    }

    public String getKeyName() {
       return ARG_NAME;
    }
    
}
