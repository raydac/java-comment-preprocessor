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

package com.igormaznitsa.jcp.expression.functions.xml;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.functions.AbstractFunctionTest;
import org.junit.Before;
import org.mockito.stubbing.Answer;

import java.io.File;

import static org.mockito.Mockito.*;

public abstract class AbstractFunctionXMLTest extends AbstractFunctionTest {

  protected PreprocessorContext SPY_CONTEXT;
  protected Value OPENED_DOCUMENT_ID;
  protected Value OPENED_DOCUMENT_ROOT;

  @Before
  public void initTest() throws Exception {
    SPY_CONTEXT = spy(new PreprocessorContext());
    final File thisRoot = new File(this.getClass().getResource("./").toURI());

    doAnswer((Answer<Object>) invocation -> {
      final String name = (String) invocation.getArguments()[0];
      return new File(thisRoot, name);
    }).when(SPY_CONTEXT).findFileInSourceFolders(any(String.class));

    OPENED_DOCUMENT_ID = new FunctionXML_OPEN().executeStr(SPY_CONTEXT, Value.valueOf("test.xml"));
    OPENED_DOCUMENT_ROOT = new FunctionXML_ROOT().executeStr(SPY_CONTEXT, OPENED_DOCUMENT_ID);
  }
}
