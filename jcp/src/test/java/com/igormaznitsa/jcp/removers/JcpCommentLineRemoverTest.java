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

package com.igormaznitsa.jcp.removers;

import java.io.Reader;
import java.io.Writer;
import org.junit.Test;

public class JcpCommentLineRemoverTest extends AbstractCommentRemoverTest {

  public JcpCommentLineRemoverTest(boolean whiteSpaced) {
    super(whiteSpaced);
  }

  @Override
  protected AbstractCommentRemover makeCommentRemoverInstance(final Reader reader,
                                                              final Writer writer,
                                                              final boolean whiteSpaceAllowed) {
    return new JcpCommentLineRemover(reader, writer, whiteSpaceAllowed);
  }

  @Test
  public void testRemovingSingleStringComments() throws Exception {
    this.assertCommentRemove(
        "class main() {\n// hello world\nSystem.out.println(\"hello // world\");// a comment\n}",
        "class main() {\n// hello world\nSystem.out.println(\"hello // world\");// a comment\n}"
    );
  }

  @Test
  public void testMultilineStringComments() throws Exception {
    this.assertCommentRemove(
        "class main() {/**\ntest\n*/\n\n// hello world\nSystem.out.println(\"hello /*ooo*/ world\");/* a comment*/\n/*  aslajdhkajhdkqwiueyoqiweuoqwueoqwiue}",
        "class main() {/**\ntest\n*/\n\n// hello world\nSystem.out.println(\"hello /*ooo*/ world\");/* a comment*/\n/*  aslajdhkajhdkqwiueyoqiweuoqwueoqwiue}"
    );
  }

  @Test
  public void testMultipleStarsAtComments() throws Exception {
    this.assertCommentRemove(
        "class main() {/**\ntest\n**/\n\n// hello world\nSystem.out.println(/**** some *** comment** ***/\"hello /*ooo*/ world\"/**** some *** comment*/);/* a comment*/\n/*  aslajdhkajhdkqwiueyoqiweuoqwueoqwiue}",
        "class main() {/**\ntest\n**/\n\n// hello world\nSystem.out.println(/**** some *** comment** ***/\"hello /*ooo*/ world\"/**** some *** comment*/);/* a comment*/\n/*  aslajdhkajhdkqwiueyoqiweuoqwueoqwiue}"
    );
  }

  @Test
  public void testTabulation() throws Exception {
    this.assertCommentRemove(
        "\thello world();//test",
        "\thello world();//test"
    );
  }

  @Test
  public void testJcpDirectivesInComments() throws Exception {
    this.assertCommentRemove(
        "// hello world\n//#if DEBUG\nSystem.out.println(\"DEBUG\");\n//#else\nSystem.out.println(\"RELEASE\");\n//#endif\n// end",
        "// hello world\n\nSystem.out.println(\"DEBUG\");\n\nSystem.out.println(\"RELEASE\");\n\n// end"
    );
  }

  @Test
  public void testCommentedJcpDirective() throws Exception {
    this.assertCommentRemove(
        "/** some multiline //$  //# test\nwith jcp directives*/\n//$ some\n//$$$ some two\n// hello world\n////#if DEBUG\nSystem.out.println(\"DEBUG\");\n//#else\nSystem.out.println(\"RELEASE\");\n//#endif\n// end",
        "/** some multiline //$  //# test\nwith jcp directives*/\n\n\n// hello world\n////#if DEBUG\nSystem.out.println(\"DEBUG\");\n\nSystem.out.println(\"RELEASE\");\n\n// end"
    );

    if (this.whiteSpaced) {
      this.assertCommentRemove(
          "/** some multiline //$  //# test\nwith jcp directives*/\n//  $ some\n//    $$$ some two\n// hello world\n////  #if DEBUG\nSystem.out.println(\"DEBUG\");\n//   # else\nSystem.out.println(\"RELEASE\");\n//   #endif\n// end",
          "/** some multiline //$  //# test\nwith jcp directives*/\n\n\n// hello world\n////  #if DEBUG\nSystem.out.println(\"DEBUG\");\n\nSystem.out.println(\"RELEASE\");\n\n// end"
      );
    }
  }

  @Test
  public void testCommentedJcpDirectiveAndJcpMarkedLines() throws Exception {
    this.assertCommentRemove(
        "/\n//\n// JCP> test\n//JCP test\n//JCP! some jcp\n/** some multiline //$  //# test\nwith jcp directives*/\n//$ some\n//JCP> some line \n//$$$ some two\n// hello world\n////#if DEBUG\nSystem.out.println(\"DEBUG\");\n//#else\nSystem.out.println(\"RELEASE\");\n//#endif\n// end\n//JCP",
        "/\n//\n// JCP> test\n//JCP test\n\n/** some multiline //$  //# test\nwith jcp directives*/\n\n\n\n// hello world\n////#if DEBUG\nSystem.out.println(\"DEBUG\");\n\nSystem.out.println(\"RELEASE\");\n\n// end\n//JCP"
    );

    if (this.whiteSpaced) {
      this.assertCommentRemove(
          "/\n//\n// JCP> test\n//JCP test\n//JCP! some jcp\n/** some multiline //$  //# test\nwith jcp directives*/\n//  $ some\n//JCP> some line \n//    $$$ some two\n// hello world\n////  #if DEBUG\nSystem.out.println(\"DEBUG\");\n//   # else\nSystem.out.println(\"RELEASE\");\n//   #endif\n// end\n//JCP",
          "/\n//\n// JCP> test\n//JCP test\n\n/** some multiline //$  //# test\nwith jcp directives*/\n\n\n\n// hello world\n////  #if DEBUG\nSystem.out.println(\"DEBUG\");\n\nSystem.out.println(\"RELEASE\");\n\n// end\n//JCP"
      );
    }
  }

  @Test
  public void testLineCommentInTheEnd() throws Exception {
    this.assertCommentRemove(
        "\thello world();//test\n//   Hello",
        "\thello world();//test\n//   Hello"
    );
  }
}
