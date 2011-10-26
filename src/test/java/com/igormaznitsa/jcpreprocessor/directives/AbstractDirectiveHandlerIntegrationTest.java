package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.JCPreprocessor;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.extension.PreprocessorExtension;
import com.igormaznitsa.jcpreprocessor.containers.FileInfoContainer;
import com.igormaznitsa.jcpreprocessor.containers.TextFileDataContainer;
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

public abstract class AbstractDirectiveHandlerIntegrationTest {

    @Test
    public abstract void testExecution() throws Exception;

    @Test
    public abstract void testKeyword() throws Exception;

    @Test
    public abstract void testHasExpression() throws Exception;

    @Test
    public abstract void testProcessOnlyIfCanBeProcessed() throws Exception;
    
    @Test
    public abstract void testReference() throws Exception;
    
    protected void assertReference(final AbstractDirectiveHandler handler) {
        assertNotNull("Handler must not be null",handler);
        final String reference = handler.getReference();
        
        assertNotNull("Reference must not be null",reference);
        assertNotNull("Reference must not empty",reference.isEmpty());
        assertFalse("Reference must not be too short",reference.length()<10);
    }
    
    public PreprocessorContext preprocessString(final String text, final List<String> preprocessedText, final PreprocessorExtension ext) throws Exception {
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
        context.setPreprocessorExtension(ext);
        
        final FileInfoContainer reference = new FileInfoContainer(new File("fake"), "fake_file", false);

        final TextFileDataContainer dataContainer = new TextFileDataContainer(new File("fake file"), preprocessingPart.toArray(new String[preprocessingPart.size()]), 0);
        final ParameterContainer param = new ParameterContainer(reference, dataContainer, "UTF8");

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

    public PreprocessorContext assertPreprocessing(final String testFileName, final PreprocessorExtension ext) throws Exception {
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

        context.setPreprocessorExtension(ext);
        
        final FileInfoContainer reference = new FileInfoContainer(new File("fake"), "fake_file", false);
        final ParameterContainer param = new ParameterContainer(reference, new TextFileDataContainer(new File("fake"), preprocessingPart.toArray(new String[preprocessingPart.size()]), 0), "UTF8");

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
