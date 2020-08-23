/*
 * Copyright 2002-2019 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.igormaznitsa.jcp.expression;

import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.util.Objects;

/**
 * The class describes an expression value i.e. an atomic constant expression item like string or number
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 * @see ValueType
 */
public final class Value implements ExpressionItem {

  public static final Value BOOLEAN_TRUE = new Value(Boolean.TRUE);
  public static final Value BOOLEAN_FALSE = new Value(Boolean.FALSE);

  public static final Value INT_ZERO = new Value(0L);
  public static final Value INT_ONE = new Value(1L);
  public static final Value INT_TWO = new Value(2L);
  public static final Value INT_THREE = new Value(3L);
  public static final Value INT_FOUR = new Value(4L);
  public static final Value INT_FIVE = new Value(5L);

  private final Object value;
  private final ValueType type;

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


  public static Value recognizeRawString(final String str) {
    Objects.requireNonNull(str, "Parameter is null");

    if ("true".equals(str)) {
      return Value.BOOLEAN_TRUE;
    }

    if ("false".equals(str)) {
      return Value.BOOLEAN_FALSE;
    }

    try {
      return new Value(Long.parseLong(str));
    } catch (NumberFormatException ex) {
      // DO NOTHING
    }

    try {
      return new Value(Float.parseFloat(str));
    } catch (NumberFormatException ex) {
      // DO NOTHING
    }

    return new Value(str);
  }


  public static Value recognizeOf(final String str) {
    final ValueType type = recognizeType(str);

    final Value result;

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
          if (value.length() > 2 && value.charAt(0) == '0' &&
              (value.charAt(1) == 'x' || value.charAt(1) == 'X')) {
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


  public static ValueType recognizeType(final String value) {
    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) // Boolean
    {
      return ValueType.BOOLEAN;
    } else if (value.length() > 1 && value.charAt(0) == '\"' &&
        value.charAt(value.length() - 1) == '\"') // String value
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


  public ValueType getType() {
    return type;
  }


  public Object getValue() {
    return value;
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


  public String toStringDetail() {
    switch (type) {
      case BOOLEAN: {
        return "Boolean : " + value;
      }
      case INT: {
        return "Integer : " + value;
      }
      case UNKNOWN: {
        return "Unknown : -";
      }
      case FLOAT: {
        return "Float : " + value;
      }
      case STRING: {
        return "String : " + value;
      }
    }
    return "!!! ERROR , UNSUPPORTED TYPE [" + type + "]";
  }

  @Override

  public String toString() {
    switch (type) {
      case BOOLEAN:
        return asBoolean().toString();
      case INT:
        return asLong().toString();
      case UNKNOWN:
        return "<UNKNOWN>";
      case FLOAT:
        return asFloat().toString();
      case STRING:
        return asString();
      default:
        return "!!! ERROR , UNSUPPORTED TYPE [" + type + "]";
    }
  }

  @Override

  public ExpressionItemType getExpressionItemType() {
    return ExpressionItemType.VALUE;
  }

  @Override

  public ExpressionItemPriority getExpressionItemPriority() {
    return ExpressionItemPriority.VALUE;
  }

  @Override
  public boolean equals(final Object var) {
    if (this == var) {
      return true;
    }

    if (var instanceof Value) {
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
