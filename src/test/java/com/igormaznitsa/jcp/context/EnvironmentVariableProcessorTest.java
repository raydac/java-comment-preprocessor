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
package com.igormaznitsa.jcp.context;

import com.igormaznitsa.jcp.expression.Value;
import org.junit.Test;
import static org.junit.Assert.*;

public class EnvironmentVariableProcessorTest {

  @Test
  public void testReadVariable() {
    final String javaVersion = System.getProperty("java.version");
    final String osName = System.getProperty("os.name");

    assertNotNull("Must not be null", javaVersion);
    assertNotNull("Must not be null", osName);

    final EnvironmentVariableProcessor test = new EnvironmentVariableProcessor();

    assertEquals("Must be equals", javaVersion, test.getVariable("env.java.version", null).asString());
    assertEquals("Must be equals", osName, test.getVariable("env.os.name", null).asString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReadUnknownVariable() {
    new EnvironmentVariableProcessor().getVariable("kjhaksjdhksajqwoiueoqiwue", null);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testWriteVariable() {
    new EnvironmentVariableProcessor().setVariable("kjhaksjdhksajqwoiueoqiwue", Value.BOOLEAN_FALSE, null);
  }
}
