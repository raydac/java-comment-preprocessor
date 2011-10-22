package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.JCPreprocessor;
import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.references.FileReference;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public abstract class AbstractDirectiveHandlerTest {

    @Test
    public abstract void testExecution() throws Exception;

    @Test
    public abstract void testKeyword() throws Exception;

    @Test
    public abstract void testHasExpression() throws Exception;

    public PreprocessorContext preprocessString(final String text, final List<String> preprocessedText) throws Exception {
        if (text == null) {
            throw new NullPointerException("Text to be preprocessed is null");
        }

        if (text.isEmpty()) {
            throw new IllegalArgumentException("Text to be preprocessed is empty");
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
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                }
            }
        }
        final PreprocessorContext context = new PreprocessorContext();

        final FileReference reference = new FileReference(new File("fake"), "fake_file", false);
        final ParameterContainer param = new ParameterContainer(reference, new File("fake"), "UTF8");

        param.setStrings(preprocessingPart.toArray(new String[preprocessingPart.size()])).setCurrentStringIndex(0);

        reference.preprocess(param, context);

        final ByteArrayOutputStream prefix = new ByteArrayOutputStream();
        final ByteArrayOutputStream normal = new ByteArrayOutputStream();
        final ByteArrayOutputStream postfix = new ByteArrayOutputStream();

        param.saveBuffersToStreams(prefix, normal, postfix);

        final BufferedReader prefixreader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(prefix.toByteArray()), "UTF8"));
        final BufferedReader normalreader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(normal.toByteArray()), "UTF8"));
        final BufferedReader postfixreader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(postfix.toByteArray()), "UTF8"));

        while (true) {
            final String line = prefixreader.readLine();
            if (line == null) {
                break;
            }
            if (preprocessedText != null) {
                preprocessedText.add(line);
            }
        }

        while (true) {
            final String line = normalreader.readLine();
            if (line == null) {
                break;
            }
            if (preprocessedText != null) {
                preprocessedText.add(line);
            }
        }

        while (true) {
            final String line = postfixreader.readLine();
            if (line == null) {
                break;
            }
            if (preprocessedText != null) {
                preprocessedText.add(line);
            }
        }

        return context;
    }

    public PreprocessorContext assertPreprocessing(final String testFileName) throws Exception {
        final InputStream stream = getClass().getResourceAsStream(testFileName);
        if (stream == null) {
            throw new FileNotFoundException("Can't find test file " + testFileName);
        }

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
                    } else {
                        throw new IllegalStateException("Check etalon prefix for duplication");
                    }
                }

                if (readFirestPart) {
                    preprocessingPart.add(line);
                } else {
                    etalonPart.add(line);
                }
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                }
            }
        }
        final PreprocessorContext context = new PreprocessorContext();

        final FileReference reference = new FileReference(new File("fake"), "fake_file", false);
        final ParameterContainer param = new ParameterContainer(reference, new File("fake"), "UTF8");

        param.setStrings(preprocessingPart.toArray(new String[preprocessingPart.size()])).setCurrentStringIndex(0);

        reference.preprocess(param, context);

        final ByteArrayOutputStream prefix = new ByteArrayOutputStream();
        final ByteArrayOutputStream normal = new ByteArrayOutputStream();
        final ByteArrayOutputStream postfix = new ByteArrayOutputStream();

        param.saveBuffersToStreams(prefix, normal, postfix);

        final List<String> result = new ArrayList<String>(100);

        final BufferedReader prefixreader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(prefix.toByteArray()), "UTF8"));
        final BufferedReader normalreader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(normal.toByteArray()), "UTF8"));
        final BufferedReader postfixreader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(postfix.toByteArray()), "UTF8"));

        while (true) {
            final String line = prefixreader.readLine();
            if (line == null) {
                break;
            }
            result.add(line);
        }

        while (true) {
            final String line = normalreader.readLine();
            if (line == null) {
                break;
            }
            result.add(line);
        }

        while (true) {
            final String line = postfixreader.readLine();
            if (line == null) {
                break;
            }
            result.add(line);
        }

        int lineIndex = 0;
        while (true) {
            if (lineIndex >= etalonPart.size() || lineIndex >= result.size()) {
                break;
            }
            assertEquals("Lines must be equals [" + (lineIndex + 1) + ']', etalonPart.get(lineIndex), result.get(lineIndex));
            lineIndex++;
        }

        assertEquals("Must be equal in their size", etalonPart.size(), result.size());

        return context;
    }
}
