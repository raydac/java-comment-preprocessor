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
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.util.Locale;

/**
 * To set the input text character encoding
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class InCharsetHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/T:";

    @Override
    public String getKeyName() {
        return ARG_NAME;
    }

    @Override
    public String getDescription() {
        return "set the input encoding for text files (default is " + PreprocessorContext.DEFAULT_CHARSET+')';
    }

    @Override
    public boolean processCommandLineKey(final String key, final PreprocessorContext context) {

        boolean result = false;

        if (key != null) {
            if (key.toUpperCase(Locale.ENGLISH).startsWith(ARG_NAME)) {
                final String value = PreprocessorUtils.extractTrimmedTail(ARG_NAME, key);

                if (!value.isEmpty()) {
                    context.setInCharacterEncoding(value);
                    result = true;
                }
            }
        }
        return result;
    }
}
