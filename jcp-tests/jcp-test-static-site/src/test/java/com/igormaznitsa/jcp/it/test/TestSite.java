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

package com.igormaznitsa.jcp.it.test;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSite {

  private final File FOLDER_SITE = new File(System.getProperty("jcp.target.folder"));

  private int countFilesInDir(File dir) {
    int cnt = 0;
    if (dir.isDirectory()) {
      Iterator it = FileUtils.iterateFiles(dir, null, true);
      while (it.hasNext()) {
        File f = (File) it.next();
        if (f.isFile()) {
          cnt++;
        }
      }
    }
    return cnt;
  }

  @Test
  public void testGeneratedSite() {
    assertEquals(342, countFilesInDir(FOLDER_SITE));
  }

}
