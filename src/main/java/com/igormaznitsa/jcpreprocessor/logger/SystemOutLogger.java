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
package com.igormaznitsa.jcpreprocessor.logger;

/**
 * An Easy logger which just output log messages into the system output streams
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class SystemOutLogger implements PreprocessorLogger {

    public SystemOutLogger(){
        
    }
    
    public void error(final String text) {
        if (text != null) {
            final String out = "[ERROR]--> " + text;
            System.err.println(out);
        }
    }

    public void info(final String text) {
        if (text != null) {
            final String out = "[INFO]--> " + text;
            System.out.println(out);
        }
    }

    public void warning(final String text) {
        if (text != null) {
            final String out = "[WARNING]--> " + text;
            System.out.println(out);
        }
    }
}
