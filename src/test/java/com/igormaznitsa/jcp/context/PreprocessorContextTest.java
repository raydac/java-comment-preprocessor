/*
 * Copyright 2017 Igor Maznitsa.
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
package com.igormaznitsa.jcp.context;

import org.junit.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static org.junit.Assert.*;
import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.extension.PreprocessorExtension;
import com.igormaznitsa.jcp.logger.PreprocessorLogger;

@SuppressWarnings("rawtypes")
public class PreprocessorContextTest {

  private static final Random RND = new Random(776655);

  private static Set<Field> extractDeclaredNonStaticNonFinalFields(final Class<?> klazz) throws Exception {
    final Set<Field> result = new HashSet<Field>();
    for (final Field f : PreprocessorContext.class.getDeclaredFields()) {
      if ((f.getModifiers() & (Modifier.STATIC | Modifier.FINAL)) != 0) {
        continue;
      }
      result.add(f);
    }
    return result;
  }

  private static Map<Field, Object> extractValues(final PreprocessorContext context) throws Exception {
    final Map<Field, Object> result = new HashMap<Field, Object>();
    for (final Field f : extractDeclaredNonStaticNonFinalFields(PreprocessorContext.class)) {
      f.setAccessible(true);
      result.put(f, f.get(context));
    }
    return result;
  }

  private static void assertObjectValue(final String fieldName, final Object value, final Object that) {
    if (value != that) {
      if (value == null || that == null) {
        assertSame(fieldName, value, that);
      } else if (List.class.isAssignableFrom(value.getClass())) {
        final List thisList = (List) value;
        final List thatList = (List) that;
        assertEquals(fieldName, thisList.size(), thatList.size());

        for (int i = 0; i < thisList.size(); i++) {
          assertObjectValue(fieldName, thisList.get(i), thatList.get(i));
        }

      } else if (Map.class.isAssignableFrom(value.getClass())) {
        final Map thisMap = (Map) value;
        final Map thatMap = (Map) that;
        assertEquals(fieldName, thisMap.size(), thatMap.size());

        for (final Object k : thisMap.keySet()) {
          final Object thisValue = thisMap.get(k);
          assertTrue(fieldName, thatMap.containsKey(k));
          assertObjectValue(fieldName, thisValue, thatMap.get(k));
        }

      } else if (Set.class.isAssignableFrom(value.getClass())) {
        final Set thisSet = (Set) value;
        final Set thatSet = (Set) that;
        assertEquals(fieldName, thisSet.size(), thatSet.size());

        for (final Object v : thisSet) {
          assertTrue(fieldName, thatSet.contains(v));
        }

      } else if (value.getClass().isArray()) {
        assertEquals(Array.getLength(value), Array.getLength(that));
        for (int i = 0; i < Array.getLength(value); i++) {
          assertObjectValue(fieldName, Array.get(value, i), Array.get(that, i));
        }
      } else {
        assertEquals(fieldName, value, that);
      }
    }
  }

  private static void assertMapFields(final String mapFieldName, final PreprocessorContext etalon, final PreprocessorContext that) throws Exception {
    Field field = null;
    for(final Field f : PreprocessorContext.class.getDeclaredFields()) {
      if (mapFieldName.equals(f.getName())) {
        field = f;
        field.setAccessible(true);
        break;
      }
    }
    
    assertNotNull("Can't find field "+mapFieldName,field);
    
    final Map thisMap = (Map)field.get(etalon);
    final Map thatMap = (Map)field.get(that);
    
    assertEquals("Map fields must have same size '"+mapFieldName+'\'', thisMap,thatMap);
    
    for(final Object k : thisMap.keySet()) {
      assertTrue(thatMap.containsKey(k));
      assertSame("Key '"+k+"' at map field '"+mapFieldName+"'", thisMap.get(k), thatMap.get(k));
    }
  }
  
  private static void assertPreprocessorContextMaps(final PreprocessorContext etalon, final PreprocessorContext that) throws Exception {
    int detected = 0;
    for(final Field f : PreprocessorContext.class.getDeclaredFields()) {
      if (Modifier.isFinal(f.getModifiers()) && Map.class.isAssignableFrom(f.getType())) {
        assertMapFields(f.getName(), etalon, that);
        detected++;
      }
    }
    assertEquals(4,detected);
  }
  
  private static void assertContextEquals(final Map<Field, Object> etalon, final Map<Field, Object> value) throws Exception {
    assertEquals("Must have same number of elements", etalon.size(), value.size());

    for (final Field f : etalon.keySet()) {
      assertObjectValue(f.getName(), etalon.get(f), value.get(f));
    }
  }

  private static String randomString() {
    final StringBuilder result = new StringBuilder();

    for (int i = 0; i < 32; i++) {
      result.append((char) ('a' + RND.nextInt(52)));
    }

    return result.toString();
  }

  private static void fillByRandomValues(final PreprocessorContext context) throws Exception {

    for (final Field f : extractDeclaredNonStaticNonFinalFields(PreprocessorContext.class)) {
      f.setAccessible(true);

      final Class type = f.getType();

      if (type.isArray()) {
        if (type.getComponentType() == String.class) {
          final String[] arr = new String[RND.nextInt(32) + 1];
          for (int i = 0; i < arr.length; i++) {
            arr[i] = randomString();
          }
          f.set(context, arr);
        } else if (type.getComponentType() == File.class) {
          final File[] arr = new File[RND.nextInt(32) + 1];
          for (int i = 0; i < arr.length; i++) {
            arr[i] = new File(randomString());
          }
          f.set(context, arr);
        } else {
          throw new Error("Unexpected array field type : " + type.getComponentType().getName());
        }
      } else if (type == Boolean.class || type == boolean.class) {
        f.set(context, RND.nextBoolean());
      } else if (type == Integer.class || type == int.class) {
        f.set(context, RND.nextInt(10000));
      } else if (type == String.class) {
        f.set(context, randomString());
      } else if (type == File.class) {
        f.set(context, new File(randomString()));
      } else if (Set.class.isAssignableFrom(type)) {
        final String[] arr = new String[RND.nextInt(32) + 1];
        for (int i = 0; i < arr.length; i++) {
          arr[i] = randomString();
        }
        try {
          f.set(context, new HashSet<String>(Arrays.asList(arr)));
        }
        catch (Exception ex) {
          ex.printStackTrace();
          fail("Can't set value to '" + f.getName() + '\'');
        }
      } else if (type == PreprocessingState.class) {
        f.set(context, new PreprocessingState(context, "UTF-8", "UTF-8"));
      } else if (type == PreprocessorLogger.class) {
        f.set(context, new PreprocessorLogger() {
          @Override
          public void error(String message) {
          }

          @Override
          public void info(String message) {
          }

          @Override
          public void debug(String message) {
          }

          @Override
          public void warning(String message) {
          }
        });
      } else if (type == PreprocessorExtension.class) {
        final PreprocessorExtension exx = new PreprocessorExtension() {
          @Override
          public boolean processAction(PreprocessorContext context, Value[] parameters) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
          }

          @Override
          public Value processUserFunction(String functionName, Value[] arguments) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
          }

          @Override
          public int getUserFunctionArity(String functionName) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
          }
        };

        f.set(context, exx);
      } else {
        throw new Error("Unexpected field type : " + type.getName());
      }
    }
    
    context.setLocalVariable("LocalHelloOne", Value.INT_ONE);
    context.setGlobalVariable("GlobalHelloOne", Value.INT_FIVE);
    context.setSharedResource("RESOURCE111", "Some string");
    context.registerSpecialVariableProcessor(new SpecialVariableProcessor() {
      @Override
      public String[] getVariableNames() {
        return new String[]{"uno::","tuo::"};
      }

      @Override
      public Value getVariable(String varName, PreprocessorContext context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }

      @Override
      public void setVariable(String varName, Value value, PreprocessorContext context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }
    });
  }

  @Test
  public void testConstuctorWithBaseContext_DefaultValues() throws Exception {
    final PreprocessorContext baseContext = new PreprocessorContext();

    final Map<Field, Object> baseContextValues = extractValues(baseContext);
    assertFalse(baseContextValues.isEmpty());

    final PreprocessorContext clonedContext = new PreprocessorContext(baseContext);
    final Map<Field, Object> clonedContextValues = extractValues(clonedContext);

    assertFalse(baseContext.isCloned());
    assertTrue(clonedContext.isCloned());
    assertContextEquals(baseContextValues, clonedContextValues);
    assertPreprocessorContextMaps(baseContext, clonedContext);
  }

  @Test
  public void testConstructorWithBaseContext_RandomValues() throws Exception {
    for (int i = 0; i < 100; i++) {
      final PreprocessorContext etalon = new PreprocessorContext();
      fillByRandomValues(etalon);
      final PreprocessorContext cloned = new PreprocessorContext(etalon);
      assertFalse(etalon.isCloned());
      assertTrue(cloned.isCloned());

      assertContextEquals(extractValues(etalon), extractValues(cloned));
      assertPreprocessorContextMaps(etalon, cloned);
    }
  }

}
