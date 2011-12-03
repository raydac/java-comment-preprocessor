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
 * The handler processes a help command from the command string
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class HelpHandler implements CommandLineHandler {

    private static final String[] ARG_NAMES = new String[]{"/H", "/?", "-H", "-?"};

    @Override
    public String getDescription() {
        return "to print information about allowed preprocessor commands";
    }

    @Override
    public boolean processCommandLineKey(final String key, final PreprocessorContext context) {
        boolean result = false;
        if (key != null && !key.isEmpty()) {
            
            final String argUpperCase = key.trim().toUpperCase();
            
            for (final String str : ARG_NAMES) {
                if (str.equals(argUpperCase)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public String getKeyName() {
        final StringBuilder result = new StringBuilder();
        for (int li = 0; li < ARG_NAMES.length; li++) {
            if (li > 0) {
                result.append(',');
            }
            result.append(ARG_NAMES[li]);
        }
        return result.toString();
    }
}
