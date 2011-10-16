package com.igormaznitsa.jcpreprocessor.expression;

import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public final class Value implements ExpressionStackItem {

    private Object value;
    private ValueType type;

    public ValueType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object val) {
        value = val;
    }

    public Value(final String val) {
        type = recognizeType(val);
        value = getValue(val, type);
        if (value == null) {
            throw new RuntimeException();
        }
    }

    public Value(Object _value) {
        type = ValueType.UNKNOWN;
        if (_value instanceof String) {
            type = ValueType.STRING;
        } else if (_value instanceof Boolean) {
            type = ValueType.BOOLEAN;
        } else if (_value instanceof Float) {
            type = ValueType.FLOAT;
        } else if (_value instanceof Long) {
            type = ValueType.INT;
        }

        value = _value;
        if (type == ValueType.UNKNOWN) {
            throw new RuntimeException("Unsupported value type");
        }
    }

    public static final Object getValue(final String value, final ValueType type) {
        try {
            switch (type) {
                case STRING: {
                    return value.substring(1, value.length() - 1);
                }
                case BOOLEAN: {
                    return value.toLowerCase().equals("true") ? Boolean.TRUE : Boolean.FALSE;
                }
                case INT: {
                    long longValue = -1;
                    if (value.startsWith("0x")) {
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
        } else if (value.startsWith("\"") && value.endsWith("\"")) // String value
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
        return Expression.PRIORITY_VALUE;
    }

    
}
