/* 
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.jcp.expression;

import com.igormaznitsa.jcp.utils.PreprocessorUtils;

/**
 * The class describes an expression value i.e. an atomic constant expression
 * item like string or number
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 * @see ValueType
 */
public final class Value implements ExpressionItem {

  public static final Value BOOLEAN_TRUE = new Value(Boolean.TRUE);
  public static final Value BOOLEAN_FALSE = new Value(Boolean.FALSE);

  public static final Value INT_ZERO = new Value(Long.valueOf(0L));
  public static final Value INT_ONE = new Value(Long.valueOf(1L));
  public static final Value INT_TWO = new Value(Long.valueOf(2L));
  public static final Value INT_THREE = new Value(Long.valueOf(3L));
  public static final Value INT_FOUR = new Value(Long.valueOf(4L));
  public static final Value INT_FIVE = new Value(Long.valueOf(5L));

  private final Object value;
  private final ValueType type;

  public ValueType getType() {
    return type;
  }

  public Object getValue() {
    return value;
  }

  private Value(final String val) {
    value = val == null ? "null" : val;
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
    return val ? BOOLEAN_TRUE : BOOLEAN_FALSE;
  }

  public static Value valueOf(final Float val) {
    return new Value(val);
  }

  public static Value valueOf(final String val) {
    return new Value(val);
  }

  public Long asLong() {
    if (type != ValueType.INT) {
      throw new IllegalStateException("Value is not integer");
    }
    return (Long) value;
  }

  public Float asFloat() {
    if (type != ValueType.FLOAT) {
      throw new IllegalStateException("Value is not float");
    }
    return (Float) value;
  }

  public String asString() {
    if (type != ValueType.STRING) {
      throw new IllegalStateException("Value is not string");
    }
    return (String) value;
  }

  public Boolean asBoolean() {
    if (type != ValueType.BOOLEAN) {
      throw new IllegalStateException("Value is not boolean");
    }
    return (Boolean) value;
  }

  public static Value recognizeRawString(final String str) {
    PreprocessorUtils.assertNotNull("Parameter is null", str);

    if (str.equals("true")) {
      return Value.BOOLEAN_TRUE;
    }

    if (str.equals("false")) {
      return Value.BOOLEAN_FALSE;
    }

    try {
      return new Value(Long.parseLong(str));
    }
    catch (NumberFormatException ex) {
    }

    try {
      return new Value(Float.parseFloat(str));
    }
    catch (NumberFormatException ex) {
    }

    return new Value(str);
  }

  public static Value recognizeOf(final String str) {
    final ValueType type = recognizeType(str);

    Value result = null;

    switch (type) {
      case BOOLEAN: {
        result = "true".equalsIgnoreCase(str) ? BOOLEAN_TRUE : BOOLEAN_FALSE;
      }
      break;
      case INT: {
        result = new Value((Long) getValue(str, ValueType.INT));
      }
      break;
      case FLOAT: {
        result = new Value((Float) getValue(str, ValueType.FLOAT));
      }
      break;
      case STRING: {
        result = new Value((String) getValue(str, ValueType.STRING));
      }
      break;
      default: {
        throw new IllegalArgumentException("Illegal value [" + str + ']');
      }
    }

    return result;
  }

  public static Object getValue(final String value, final ValueType type) {
    try {
      switch (type) {
        case STRING: {
          return value.substring(1, value.length() - 1);
        }
        case BOOLEAN: {
          return value.equalsIgnoreCase("true") ? Boolean.TRUE : Boolean.FALSE;
        }
        case INT: {
          if (value.length() > 2 && value.charAt(0) == '0' && (value.charAt(1) == 'x' || value.charAt(1) == 'X')) {
            // HEX value
            return Long.valueOf(PreprocessorUtils.extractTail("0x", value), 16);
          }
          else {
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
    }
    catch (NumberFormatException e) {
      return null;
    }
  }

  public static ValueType recognizeType(final String value) {
    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) // Boolean
    {
      return ValueType.BOOLEAN;
    }
    else if (value.length() > 1 && value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"') // String value
    {
      return ValueType.STRING;
    }
    else {
      try {
        if (value.indexOf('.') >= 0) {
          // Float
          Float.parseFloat(value);
          return ValueType.FLOAT;
        }
        else {
          // Integer
          if (value.startsWith("0x")) {
            // HEX value
            Long.parseLong(PreprocessorUtils.extractTail("0x", value), 16);
          }
          else {
            // Decimal value
            Long.parseLong(value, 10);
          }
          return ValueType.INT;
        }
      }
      catch (NumberFormatException e) {
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

  @Override
  public String toString() {
    switch (type) {
      case BOOLEAN: {
        return asBoolean().toString();
      }
      case INT: {
        return asLong().toString();
      }
      case UNKNOWN: {
        return "<UNKNOWN>";
      }
      case FLOAT: {
        return asFloat().toString();
      }
      case STRING: {
        return asString();
      }
    }
    return "!!! ERROR , UNSUPPORTED TYPE [" + type + "]";
  }

  public ExpressionItemType getExpressionItemType() {
    return ExpressionItemType.VALUE;
  }

  public ExpressionItemPriority getExpressionItemPriority() {
    return ExpressionItemPriority.VALUE;
  }

  @Override
  public boolean equals(final Object var) {
    if (var == null) {
      return false;
    }

    if (this == var) {
      return true;
    }

    if (var.getClass() == Value.class) {
      final Value thatValue = (Value) var;

      return this.type == thatValue.type && this.value.equals(thatValue.value);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.value.hashCode();
  }

}
