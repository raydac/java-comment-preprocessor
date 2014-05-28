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
package com.igormaznitsa.jcp.removers;

import java.io.StringReader;
import java.io.StringWriter;
import org.junit.Test;
import static org.junit.Assert.*;

public class JavaCommentsRemoverTest {

  @Test
  public void testRemovingSingleStringComments() throws Exception {
    final String SRC = "class main() {\n// hello world\nSystem.out.println(\"hello // world\");// a comment\n}";
    final String DST = "class main() {\n\nSystem.out.println(\"hello // world\");\n}";

    final StringReader reader = new StringReader(SRC);
    final StringWriter writer = new StringWriter(256);

    new JavaCommentsRemover(reader, writer).process();

    assertEquals("Must be the same", DST, writer.toString());
  }

  @Test
  public void testMultilineStringComments() throws Exception {
    final String SRC = "class main() {/**\ntest\n*/\n\n// hello world\nSystem.out.println(\"hello /*ooo*/ world\");/* a comment*/\n/*  aslajdhkajhdkqwiueyoqiweuoqwueoqwiue}";
    final String DST = "class main() {\n\n\nSystem.out.println(\"hello /*ooo*/ world\");\n";

    final StringReader reader = new StringReader(SRC);
    final StringWriter writer = new StringWriter(256);

    new JavaCommentsRemover(reader, writer).process();

    assertEquals("Must be the same", DST, writer.toString());
  }

  @Test
  public void testMultipleStarsAtComments() throws Exception {
    final String SRC = "class main() {/**\ntest\n**/\n\n// hello world\nSystem.out.println(/**** some *** comment** ***/\"hello /*ooo*/ world\"/**** some *** comment*/);/* a comment*/\n/*  aslajdhkajhdkqwiueyoqiweuoqwueoqwiue}";
    final String DST = "class main() {\n\n\nSystem.out.println(\"hello /*ooo*/ world\");\n";
    final StringReader reader = new StringReader(SRC);
    final StringWriter writer = new StringWriter(256);

    new JavaCommentsRemover(reader, writer).process();

    assertEquals("Must be the same", DST, writer.toString());
  }

  @Test
  public void testTabulation() throws Exception {
    final String SRC = "\thello world();//test";
    final String DST = "\thello world();";

    final StringReader reader = new StringReader(SRC);
    final StringWriter writer = new StringWriter(256);

    new JavaCommentsRemover(reader, writer).process();

    assertEquals("Must be the same", DST, writer.toString());
  }
}
