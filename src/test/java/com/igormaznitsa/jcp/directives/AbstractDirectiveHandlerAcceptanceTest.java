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
package com.igormaznitsa.jcp.directives;

import org.junit.BeforeClass;
import com.igormaznitsa.jcp.context.PreprocessingState.ExcludeIfInfo;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.extension.PreprocessorExtension;
import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.containers.TextFileDataContainer;
import com.igormaznitsa.jcp.logger.PreprocessorLogger;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import static org.junit.Assert.*;

public abstract class AbstractDirectiveHandlerAcceptanceTest {

  protected static File THIS_CLASS_FILE;

  @BeforeClass
  public static void beforeClass() throws Exception {
    THIS_CLASS_FILE = new File(AbstractDirectiveHandler.class.getResource(AbstractDirectiveHandlerAcceptanceTest.class.getSimpleName() + ".class").toURI());
  }

  @Test
  public abstract void testExecution() throws Exception;

  @Test
  public abstract void testKeyword() throws Exception;

  @Test
  public abstract void testExecutionCondition() throws Exception;

  @Test
  public abstract void testReference() throws Exception;

  @Test
  public abstract void testArgumentType() throws Exception;

  @Test
  public abstract void testPhase() throws Exception;

  protected void assertReference(final AbstractDirectiveHandler handler) {
    assertNotNull("Handler must not be null", handler);
    final String reference = handler.getReference();

    assertNotNull("Reference must not be null", reference);
    assertNotNull("Reference must not empty", reference.isEmpty());
    assertFalse("Reference must not be too short", reference.length() < 10);
  }

  private PreprocessorContext setGlobalVars(final PreprocessorContext context, final VariablePair... vars) {
    if (vars.length != 0) {
      for (final VariablePair p : vars) {
        context.setGlobalVariable(p.getName(), p.getValue());
      }
    }
    return context;
  }

  public void assertPreprocessorException(final String preprocessingText, final int exceptionStringIndex, final PreprocessorExtension extension, final VariablePair... globalVars) {
    try {
      final PreprocessorContext context = preprocessString(preprocessingText, null, extension, globalVars);
      fail("Must throw PreprocessorException");
    }
    catch (PreprocessorException expected) {
      assertEquals("Expected " + PreprocessorException.class.getCanonicalName(), exceptionStringIndex, expected.getStringIndex());
    }
    catch (Exception unExpected) {
      unExpected.printStackTrace();
      fail("Unexpected exception " + unExpected.getClass().getCanonicalName());
    }

  }

  public void assertGlobalPhaseException(final String preprocessingText, final int exceptionStringIndex, final PreprocessorExtension extension) {
    try {
      preprocessStringAtGlobalPhase(preprocessingText, null);
      fail("Must throw PreprocessorException");
    }
    catch (PreprocessorException expected) {
      assertEquals("Expected " + PreprocessorException.class.getCanonicalName(), exceptionStringIndex, expected.getStringIndex());
    }
    catch (Exception unExpected) {
      unExpected.printStackTrace();
      fail("Unexpected exception " + unExpected.getClass().getCanonicalName());
    }
  }

  private PreprocessorContext preprocessStringAtGlobalPhase(final String encoding, final List<ExcludeIfInfo> excludeInfoList) throws IOException {
    final List<String> parsedText = parseStringForLines(encoding);
    final PreprocessorContext context = new PreprocessorContext();
    context.setFileOutputDisabled(true);

    final FileInfoContainer reference = new FileInfoContainer(THIS_CLASS_FILE, "fake_name", false);
    final TextFileDataContainer textContainer = new TextFileDataContainer(reference.getSourceFile(), parsedText.toArray(new String[parsedText.size()]),false,0);
    final PreprocessingState state = context.produceNewPreprocessingState(reference, textContainer);

    final List<ExcludeIfInfo> result = reference.processGlobalDirectives(state, context);

    if (excludeInfoList != null) {
      excludeInfoList.addAll(result);
    }

    return context;
  }

  public PreprocessorContext executeGlobalPhase(final String fileName, final List<ExcludeIfInfo> excludeIf) throws Exception {
    final File file = new File(getClass().getResource(fileName).toURI());
    final PreprocessorContext context = new PreprocessorContext();
    context.setFileOutputDisabled(true);

    final FileInfoContainer reference = new FileInfoContainer(file, file.getName(), false);
    final List<ExcludeIfInfo> result = reference.processGlobalDirectives(null, context);

    if (excludeIf != null) {
      excludeIf.addAll(result);
    }
    return context;
  }

  private void readWholeDataFromReader(final BufferedReader reader, final List<String> accumulator) throws IOException {
    while (true) {
      final String line = reader.readLine();
      if (line == null) {
        break;
      }
      accumulator.add(line);
    }
  }

  private void assertEqualsStringLists(final List<String> etalon, final List<String> result) {
    final String[] etalonStrings = etalon.toArray(new String[etalon.size()]);
    final String[] resultStrings = result.toArray(new String[result.size()]);
    final int len = Math.max(etalonStrings.length, resultStrings.length);

    for (int i = 0; i < len; i++) {
      final String etalonStr = i < etalonStrings.length ? etalonStrings[i] : null;
      final String resultStr = i < resultStrings.length ? resultStrings[i] : null;

      if ((etalonStr != null && !etalonStr.equals(resultStr)) || (resultStr != null && !resultStr.equals(etalonStr))) {
        throw new LinesNotMatchException(etalonStrings.length, resultStrings.length, i, etalonStr, resultStr);
      }
    }
  }

  private PreprocessorContext insidePreprocessingAndMatching(final File srcfile, final List<String> preprocessingText, final List<String> result, final List<String> etalonList, final PreprocessorExtension extension, final PreprocessorLogger logger, final boolean keepLines, final VariablePair... globalVariables) throws Exception {
    PreprocessorUtils.assertNotNull("Preprocessing text is null", preprocessingText);
    PreprocessorUtils.assertNotNull("Result container is null", result);

    final PreprocessorContext context = new PreprocessorContext();
    if (logger != null) {
      context.setPreprocessorLogger(logger);
    }
    context.setFileOutputDisabled(true);
    context.setSourceDirectories(srcfile.getParent());
    context.setKeepLines(keepLines);
    context.setPreprocessorExtension(extension);

    setGlobalVars(context, globalVariables);

    final FileInfoContainer reference = new FileInfoContainer(srcfile, srcfile.getName(), false);
    final PreprocessingState state = context.produceNewPreprocessingState(reference, new TextFileDataContainer(reference.getSourceFile(), preprocessingText.toArray(new String[preprocessingText.size()]),false, 0));

    reference.preprocessFile(state, context);

    final ByteArrayOutputStream prefix = new ByteArrayOutputStream();
    final ByteArrayOutputStream normal = new ByteArrayOutputStream();
    final ByteArrayOutputStream postfix = new ByteArrayOutputStream();

    state.saveBuffersToStreams(prefix, normal, postfix);

    final BufferedReader prefixreader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(prefix.toByteArray()), "UTF8"));
    final BufferedReader normalreader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(normal.toByteArray()), "UTF8"));
    final BufferedReader postfixreader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(postfix.toByteArray()), "UTF8"));

    readWholeDataFromReader(prefixreader, result);
    readWholeDataFromReader(normalreader, result);
    readWholeDataFromReader(postfixreader, result);

    try {
      if (etalonList != null) {
        assertEqualsStringLists(etalonList, result);
      }
    }
    catch (Exception unexpected) {
      if (etalonList != null) {
        int index = 1;
        for (final String str : etalonList) {
          System.out.print(index++);
          System.out.print('\t');
          println(str, true);
        }
        System.out.println("---------------------");
        index = 1;
        for (final String str : result) {
          System.out.print(index++);
          System.out.print('\t');
          println(str, true);
        }
      }
      throw unexpected;
    }

    return context;

  }

  private void println(final String str, final boolean showWhitespaces) {
    for (final char chr : str.toCharArray()) {
      if (Character.isWhitespace(chr)) {
        System.out.print(showWhitespaces ? '.' : chr);
      }
      else {
        System.out.print(chr);
      }
    }
    System.out.println();
  }

  private List<String> parseStringForLines(final String text) throws IOException {
    if (text == null || text.isEmpty()) {
      return Collections.emptyList();
    }

    final BufferedReader reader = new BufferedReader(new StringReader(text), text.length() * 2);

    final List<String> preprocessingPart = new ArrayList<String>(100);

    try {
      while (true) {
        final String line = reader.readLine();
        if (line == null) {
          break;
        }

        preprocessingPart.add(line);
      }
    }
    finally {
      IOUtils.closeQuietly(reader);
    }

    return preprocessingPart;
  }

  private PreprocessorContext preprocessString(final String text, final List<String> preprocessedText, final PreprocessorExtension ext, final VariablePair... globalVars) throws Exception {
    final List<String> preprocessingPart = parseStringForLines(text);
    return insidePreprocessingAndMatching(THIS_CLASS_FILE, preprocessingPart, preprocessedText == null ? new ArrayList<String>() : preprocessedText, null, ext, null, false, globalVars);
  }

  public PreprocessorContext assertFilePreprocessing(final String testFileName, boolean keepLines, final PreprocessorExtension ext, final PreprocessorLogger logger, final VariablePair... globalVars) throws Exception {
    final File file = new File(getClass().getResource(testFileName).toURI());

    if (!file.exists() || !file.isFile()) {
      throw new FileNotFoundException("Can't find the test file " + testFileName);
    }

    final InputStream stream = new FileInputStream(file);

    final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF8"), 1024);

    final List<String> preprocessingPart = new ArrayList<String>(100);
    final List<String> etalonPart = new ArrayList<String>(100);

    boolean readFirestPart = true;
    try {
      while (true) {
        final String line = reader.readLine();
        if (line == null) {
          break;
        }

        if (line.startsWith("---START_ETALON---")) {
          if (readFirestPart) {
            readFirestPart = false;
            continue;
          }
          else {
            throw new IllegalStateException("Check etalon prefix for duplication");
          }
        }

        if (readFirestPart) {
          preprocessingPart.add(line);
        }
        else {
          etalonPart.add(line);
        }
      }
    }
    finally {
      IOUtils.closeQuietly(reader);
    }

    return insidePreprocessingAndMatching(file, preprocessingPart, new ArrayList<String>(), etalonPart, ext, logger, keepLines, globalVars);
  }
}
