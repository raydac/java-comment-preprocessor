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
 * The enumeration contains possible argument types are being used by directives
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public enum DirectiveArgumentType {
    NONE(""),
    STRING("STRING"),
    TAIL("TAIL"),
    BOOLEAN("BOOLEAN"),
    VARNAME("VAR"),
    EXPRESSTION("EXPR"),
    MULTIEXPRESSION("EXPR1,EXPR2...EXPRn"),
    SET("VAR=EXPR"), 
    ONOFF("[+|-]");
    
    private final String str;
    
    private DirectiveArgumentType(final String str){
        this.str = str;
    }
    
    public String getAsText() {
        return this.str;
    }
}
