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
package com.igormaznitsa.jcp.utils.antpathmatcher;

import org.junit.Test;
import static org.junit.Assert.*;

public class AntPathMatcherTest {
  
  @Test
  public void testMatching() {
    final AntPathMatcher matcher = new AntPathMatcher();
 
    assertTrue(matcher.match("", ""));
    assertFalse(matcher.match("", "a"));
    assertFalse(matcher.match("a", ""));
    assertTrue(matcher.match("?", "a"));
    assertFalse(matcher.match("*", ""));
    assertFalse(matcher.match("?", ""));
    
    assertTrue(matcher.match("**/test", "test"));
    assertTrue(matcher.match("**/test?", "test1"));
    assertTrue(matcher.match("**/test*", "test111"));
    assertTrue(matcher.match("**/test", "some/test"));
    assertTrue(matcher.match("**/test", "some/help/test"));
    assertTrue(matcher.match("some/**/test", "some/help/test"));
    assertTrue(matcher.match("**/some/help/test", "some/help/test"));
    assertTrue(matcher.match("/**/help/test", "/some/help/test"));
    assertTrue(matcher.match("**/help/test", "some/help/test"));
    assertTrue(matcher.match("**\\help\\test", "some\\help\\test"));

    assertFalse(matcher.match("**\\help\\test", "some\\help\\test1"));
    assertFalse(matcher.match("some", "some1"));
    assertFalse(matcher.match("some/help", "some/help/ddd"));
  }
  
}
