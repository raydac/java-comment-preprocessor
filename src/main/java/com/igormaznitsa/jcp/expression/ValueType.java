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
package com.igormaznitsa.jcp.expression;

/**
 * The enumeration contains all allowed types for expression values and their signatures
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public enum ValueType {
    ANY("Any"), STRING("Str"), BOOLEAN("Bool"), INT("Int"), FLOAT("Float"), UNKNOWN("Unknown");
    
    /**
     * The signature for the type it will be used in method calls
     */
    private final String signature;
    
    public String getSignature(){
        return this.signature;
    }
    
    private ValueType(final String signature) {
        this.signature = signature;
    }
    
    /**
     * To check that the type is compatible with another one
     * @param type the type to be checked, must not be null
     * @return true if the type is compatible else false
     */
    public boolean isCompatible(final ValueType type){
        if (this == type) {
            return true;
        }

        if (this == UNKNOWN || type == UNKNOWN) {
            return false;
        }
        
        if (this == ANY || type == ANY){
            return true;
        }
        
        return false;
    }
}
