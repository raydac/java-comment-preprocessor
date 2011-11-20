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
package com.igormaznitsa.jcpreprocessor.removers;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * A remover allows to cut off all Java like comments from a reader and write the result into a writer
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class JavaCommentsRemover {

    private final Reader srcReader;
    private final Writer dstWriter;

    public JavaCommentsRemover(final Reader src, final Writer dst) {
        if (src == null){
            throw new NullPointerException("The reader is null");
        }
        
        if (dst == null){
            throw new NullPointerException("The writer is null");
        }
        
        this.srcReader = src;
        this.dstWriter = dst;
    }

    void skipUntilNextString() throws IOException {
        while (true) {
            final int chr = srcReader.read();
            if (chr < 0) {
                return;
            }

            if (chr == '\n') {
                dstWriter.write(chr);
                return;
            }
        }
    }

    void skipUntilClosingComments() throws IOException {
        boolean starFound = false;

        while (true) {
            final int chr = srcReader.read();
            if (chr < 0) {
                return;
            }
            if (starFound) {
                if (chr == '/') {
                    return;
                } else {
                    starFound = false;
                }
            } else {
                if (chr == '*') {
                    starFound = true;
                }
            }
        }
    }

    public void process() throws IOException {
        final int STATE_NORMAL = 0;
        final int STATE_INSIDE_STRING = 1;
        final int STATE_NEXT_SPECIAL_CHAR = 2;
        final int STATE_FORWARD_SLASH = 3;

        int state = STATE_NORMAL;

        while (true) {
            final int chr = srcReader.read();
            if (chr < 0) {
                break;
            }

            switch (state) {
                case STATE_NORMAL: {
                    switch (chr) {
                        case '\"': {
                            dstWriter.write(chr);
                            state = STATE_INSIDE_STRING;
                        }
                        break;
                        case '/': {
                            state = STATE_FORWARD_SLASH;
                        }
                        break;
                        default: {
                            dstWriter.write(chr);
                        }
                        break;
                    }
                }
                break;
                case STATE_FORWARD_SLASH: {
                    switch (chr) {
                        case '*': {
                            skipUntilClosingComments();
                            state = STATE_NORMAL;
                        }
                        break;
                        case '/': {
                            skipUntilNextString();
                            state = STATE_NORMAL;
                        }
                        break;
                        default: {
                            dstWriter.write('/');
                            dstWriter.write(chr);
                            state = STATE_NORMAL;
                        }
                        break;
                    }
                }
                break;
                case STATE_INSIDE_STRING: {
                    switch (chr) {
                        case '\\': {
                            state = STATE_NEXT_SPECIAL_CHAR;
                        }
                        break;
                        case '\"': {
                            state = STATE_NORMAL;
                        }
                        break;
                    }
                    dstWriter.write(chr);
                }
                break;
                case STATE_NEXT_SPECIAL_CHAR: {
                    dstWriter.write(chr);
                    state = STATE_INSIDE_STRING;
                }
                break;
            }

        }
    }
}
