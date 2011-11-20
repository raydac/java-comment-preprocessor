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
package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.util.HashMap;
import java.util.Map;

/**
 * The class implements the str2web function handler
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionSTR2WEB extends AbstractFunction {

    private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING}};
    
    private static final Map<Character, String> CHAR_MAP = new HashMap<Character, String>();
    
    static {
	CHAR_MAP.put(Character.valueOf((char)160),"&nbsp;");
	CHAR_MAP.put(Character.valueOf((char)169),"&copy;");
	CHAR_MAP.put(Character.valueOf((char)174),"&reg;");
	CHAR_MAP.put(Character.valueOf((char)178),"&sup2;");
	CHAR_MAP.put(Character.valueOf((char)179),"&sup3;");
	CHAR_MAP.put(Character.valueOf((char)179),"&sup3;");
	CHAR_MAP.put(Character.valueOf((char)34),"&quot;");
	CHAR_MAP.put(Character.valueOf((char)38),"&amp;");
	CHAR_MAP.put(Character.valueOf((char)60),"&lt;");
	CHAR_MAP.put(Character.valueOf((char)62),"&gt;");
	CHAR_MAP.put(Character.valueOf((char)8211),"&ndash;");
	CHAR_MAP.put(Character.valueOf((char)8212),"&mdash;");
	CHAR_MAP.put(Character.valueOf((char)8217),"&rsquo;");
	CHAR_MAP.put(Character.valueOf((char)8220),"&ldquo;");
	CHAR_MAP.put(Character.valueOf((char)8226),"&bull;");
	CHAR_MAP.put(Character.valueOf((char)8224),"&dagger;");
	CHAR_MAP.put(Character.valueOf((char)8225),"&Dagger;");
	CHAR_MAP.put(Character.valueOf((char)8242),"&prime;");
	CHAR_MAP.put(Character.valueOf((char)8243),"&Prime;");
	CHAR_MAP.put(Character.valueOf((char)8249),"&lsaquo;");
	CHAR_MAP.put(Character.valueOf((char)8250),"&rsaquo;");
	CHAR_MAP.put(Character.valueOf((char)8364),"&euro;");
	CHAR_MAP.put(Character.valueOf((char)8482),"&trade;");
	CHAR_MAP.put(Character.valueOf((char)732),"&tilde;");
	CHAR_MAP.put(Character.valueOf((char)710),"&circ;");
	CHAR_MAP.put(Character.valueOf((char)9824),"&spades;");
	CHAR_MAP.put(Character.valueOf((char)9827),"&clubs;");
	CHAR_MAP.put(Character.valueOf((char)9829),"&hearts;");
	CHAR_MAP.put(Character.valueOf((char)9830),"&diams;");
	CHAR_MAP.put(Character.valueOf((char)9674),"&loz;");
	CHAR_MAP.put(Character.valueOf((char)8592),"&larr;");
	CHAR_MAP.put(Character.valueOf((char)8594),"&rarr;");
	CHAR_MAP.put(Character.valueOf((char)8593),"&uarr;");
	CHAR_MAP.put(Character.valueOf((char)8595),"&darr;");
	CHAR_MAP.put(Character.valueOf((char)8596),"&harr;");
	CHAR_MAP.put(Character.valueOf((char)172),"&not;");
    }
    
    @Override
    public String getName() {
        return "str2web";
    }

    public Value executeStr(final PreprocessorContext context, final Value value) {
        final String str = value.asString();
        
        final StringBuilder buffer = new StringBuilder(str.length()<<1);
        
        for(final char chr : str.toCharArray()){
            final String converted = CHAR_MAP.get(Character.valueOf(chr));
            if (converted == null) {
                buffer.append(chr);
            } else {
                buffer.append(converted);
            }
        }
        
        return Value.valueOf(buffer.toString());
    }
    
    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public ValueType[][] getAllowedArgumentTypes() {
        return ARG_TYPES;
    }

    @Override
    public String getReference() {
        return "it escapes a string to make it compatibles with the html format";
    }

    @Override
    public ValueType getResultType() {
        return ValueType.STRING;
    }
}
