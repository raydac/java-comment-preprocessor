package com.igormaznitsa.jcpreprocessor.expression;

import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public final class Value implements ExpressionStackItem {

    public static final Value BOOLEAN_TRUE = new Value(Boolean.TRUE);
    public static final Value BOOLEAN_FALSE = new Value(Boolean.FALSE);
    public static final Value INT_ZERO = new Value(Long.valueOf(0));
    
    private final Object value;
    private final ValueType type;

    public ValueType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    
    private Value(final String val) {
        value = val;
        type = ValueType.STRING;
    }

    private Value(final Long val) {
        value = val;
        type = ValueType.INT;
    }

    private Value(final Float val) {
        value = val;
        type = ValueType.FLOAT;
    }

    private Value(final Boolean val) {
        value = val;
        type = ValueType.BOOLEAN;
    }

    public static Value valueOf(final Long val) {
        return new Value(val);
    }
    
    public static Value valueOf(final Boolean val) {
        return val.booleanValue() ? BOOLEAN_TRUE : BOOLEAN_FALSE;
    }
    
    public static Value valueOf(final Float val){
        return new Value(val);
    }
    
    public static Value valueOf(final String val){
        return new Value(val);
    }
    
    public static Value recognizeOf(final String str) {
        final ValueType type = recognizeType(str);

        switch(type) {
            case BOOLEAN : {
                return "true".equalsIgnoreCase(str) ? BOOLEAN_TRUE : BOOLEAN_FALSE;
            }
            case INT : {
                return new Value((Long)getValue(str, ValueType.INT));
            }
            case FLOAT : {
                return new Value((Float)getValue(str, ValueType.FLOAT));
            }
            case STRING : {
                return new Value((String)getValue(str, ValueType.STRING));
            }
            default:{
                throw new RuntimeException("Unsupported object type");
            } 
        }
    }
    
    public static final Object getValue(final String value, final ValueType type) {
        try {
            switch (type) {
                case STRING: {
                    return value.substring(1, value.length() - 1);
                }
                case BOOLEAN: {
                    return value.equalsIgnoreCase("true") ? Boolean.TRUE : Boolean.FALSE;
                }
                case INT: {
                    long longValue = -1;
                    if (value.length()>2 && value.charAt(0) == '0' && (value.charAt(1)=='x' || value.charAt(1)=='X'))
                        {
                        // HEX value
                        return Long.valueOf(PreprocessorUtils.extractTail("0x", value), 16);
                    } else {
                        // Decimal value
                        return Long.valueOf(value);
                    }
                }
                case FLOAT: {
                    return Float.valueOf(value);
                }
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static final ValueType recognizeType(final String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) // Boolean
        {
            return ValueType.BOOLEAN;
        } else if (value.length()>1 && value.charAt(0)=='\"' && value.charAt(value.length()-1) == '\"') // String value
        {
            return ValueType.STRING;
        } else {
            try {
                if (value.indexOf('.') >= 0) {
                    // Float
                    Float.parseFloat(value);
                    return ValueType.FLOAT;
                } else {
                    // Integer
                    if (value.startsWith("0x")) {
                        // HEX value
                        Long.parseLong(PreprocessorUtils.extractTail("0x", value), 16);
                    } else {
                        // Decimal value
                        Long.parseLong(value, 10);
                    }
                    return ValueType.INT;
                }
            } catch (NumberFormatException e) {
                return ValueType.UNKNOWN;
            }
        }
    }

    public String toStringDetail() {
        switch (type) {
            case BOOLEAN: {
                return "Boolean : " + ((Boolean) value).booleanValue();
            }
            case INT: {
                return "Integer : " + ((Long) value).longValue();
            }
            case UNKNOWN: {
                return "Unknown : -";
            }
            case FLOAT: {
                return "Float : " + ((Float) value).floatValue();
            }
            case STRING: {
                return "String : " + (String) value;
            }
        }
        return "!!! ERROR , UNSUPPORTED TYPE [" + type + "]";
    }

    public String toString() {
        switch (type) {
            case BOOLEAN: {
                return "" + ((Boolean) value).booleanValue();
            }
            case INT: {
                return "" + ((Long) value).longValue();
            }
            case UNKNOWN: {
                return "";
            }
            case FLOAT: {
                return "" + ((Float) value).floatValue();
            }
            case STRING: {
                return "" + (String) value;
            }
        }
        return "!!! ERROR , UNSUPPORTED TYPE [" + type + "]";
    }

    public ExpressionStackItemType getStackItemType() {
        return ExpressionStackItemType.VALUE;
    }

    public int getPriority() {
        return 6;
    }

    
}
