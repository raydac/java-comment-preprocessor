package com.igormaznitsa.jcpreprocessor.directives;

import org.junit.BeforeClass;
import com.igormaznitsa.jcpreprocessor.context.PreprocessingState.ExcludeIfInfo;
import com.igormaznitsa.jcpreprocessor.exceptions.PreprocessorException;
import com.igormaznitsa.jcpreprocessor.context.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.extension.PreprocessorExtension;
import com.igormaznitsa.jcpreprocessor.containers.FileInfoContainer;
import com.igormaznitsa.jcpreprocessor.containers.TextFileDataContainer;
import com.igormaznitsa.jcpreprocessor.logger.PreprocessorLogger;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
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
import org.junit.Test;
import static org.junit.Assert.*;

public abstract class AbstractDirectiveHandlerIntegrationTest {

    protected static File THIS_CLASS_FILE;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        THIS_CLASS_FILE = new File(AbstractDirectiveHandler.class.getResource("AbstractDirectiveHandlerIntegrationTest.class").toURI());
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

    public void assertPreprocessorException(final String preprocessingText, final int exceptionStringIndex, final PreprocessorExtension extension) {
        try {
            final PreprocessorContext context = preprocessString(preprocessingText, null, extension);
            fail("Must throw PreprocessorException");
        } catch (PreprocessorException expected) {
            assertEquals("Expected " + PreprocessorException.class.getCanonicalName(), exceptionStringIndex, expected.getStringIndex());
        } catch (Exception unExpected) {
            unExpected.printStackTrace();
            fail("Unexpected exception " + unExpected.getClass().getCanonicalName());
        }

    }

    public void assertGlobalPhaseException(final String preprocessingText, final int exceptionStringIndex, final PreprocessorExtension extension) {
        try {
            preprocessStringAtGlobalPhase(preprocessingText,null);
            fail("Must throw PreprocessorException");
        }catch(PreprocessorException expected){
            assertEquals("Expected " + PreprocessorException.class.getCanonicalName(), exceptionStringIndex, expected.getStringIndex());
        } catch (Exception unExpected) {
            unExpected.printStackTrace();
            fail("Unexpected exception " + unExpected.getClass().getCanonicalName());
        }
    }

    private PreprocessorContext preprocessStringAtGlobalPhase(final String encoding, final List<ExcludeIfInfo> excludeInfoList) throws IOException,PreprocessorException {
        final List<String> parsedText = parseStringForLines(encoding);
        final PreprocessorContext context = new PreprocessorContext();
        context.setFileOutputDisabled(true);

        final FileInfoContainer reference = new FileInfoContainer(THIS_CLASS_FILE, "fake_name", false);
        final TextFileDataContainer textContainer = new TextFileDataContainer(reference.getSourceFile(), parsedText.toArray(new String[parsedText.size()]), 0);
        final PreprocessingState state = context.produceNewPreprocessingState(reference, textContainer);

        final List<ExcludeIfInfo> result = reference.processGlobalDirectives(state, context);
        
        if (excludeInfoList!=null){
            excludeInfoList.addAll(result);
        }
        
        return context;
    }
    
    public PreprocessorContext executeGlobalPhase(final String fileName, final List<ExcludeIfInfo> excludeIf) throws Exception {
        final File file = new File(getClass().getResource(fileName).toURI());
        final PreprocessorContext context = new PreprocessorContext();
        context.setFileOutputDisabled(true);
        
        final FileInfoContainer reference = new FileInfoContainer(file, file.getName(), false);
        final List<ExcludeIfInfo> result = reference.processGlobalDirectives(null,context);
        
        if (excludeIf!=null){
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

    private PreprocessorContext insidePreprocessingAndMatching(final File srcfile, final List<String> preprocessingText, final List<String> result, final List<String> etalonList, final PreprocessorExtension extension, final PreprocessorLogger logger) throws Exception {
        if (preprocessingText == null) {
            throw new NullPointerException("Preprocessing text is null");
        }

        if (result == null) {
            throw new NullPointerException("Result container is null");
        }

        final PreprocessorContext context = new PreprocessorContext();
        if (logger!=null){
            context.setPreprocessorLogger(logger);
        }
        context.setFileOutputDisabled(true);
        context.setSourceDirectory(srcfile.getParent());
        
        context.setPreprocessorExtension(extension);

        final FileInfoContainer reference = new FileInfoContainer(srcfile, srcfile.getName(), false);
        final PreprocessingState state = context.produceNewPreprocessingState(reference, new TextFileDataContainer(reference.getSourceFile(), preprocessingText.toArray(new String[preprocessingText.size()]), 0));

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
                if (etalonList.size()!=result.size()){
                    throw new RuntimeException("Result and etalon size are not equal ["+etalonList.size()+"!="+result.size()+']');
                }
   
                int lineIndex = 0;
                while (true) {
                    if (lineIndex >= etalonList.size() || lineIndex >= result.size()) {
                        break;
                    }
                    if (!etalonList.get(lineIndex).equals(result.get(lineIndex))){
                        throw new RuntimeException("Non-equal lines detected at string "+(lineIndex+1));
                    }
                    lineIndex++;
                }
            }
        } catch (Exception unexpected) {
            if (etalonList != null && result != null) {
                int index = 1;
                for (final String str : etalonList) {
                    System.out.print(index++);
                    System.out.print('\t');
                    System.out.println(str);
                }
                System.out.println("---------------------");
                index = 1;
                for (final String str : result) {
                    System.out.print(index++);
                    System.out.print('\t');
                    System.out.println(str);
                }
            }
            throw unexpected;
        }

        return context;

    }

    private List<String> parseStringForLines(final String text) throws IOException {
        if (text == null || text.isEmpty())
        {
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
        } finally {
            PreprocessorUtils.closeSilently(reader);
        }

        return preprocessingPart;
    }
    
    private PreprocessorContext preprocessString(final String text, final List<String> preprocessedText, final PreprocessorExtension ext) throws Exception {
        final List<String> preprocessingPart = parseStringForLines(text);
        return insidePreprocessingAndMatching(THIS_CLASS_FILE,preprocessingPart, preprocessedText == null ? new ArrayList<String>() : preprocessedText, null, ext, null);
    }

    public PreprocessorContext assertFilePreprocessing(final String testFileName, final PreprocessorExtension ext, final PreprocessorLogger logger) throws Exception {
        final File file = new File(getClass().getResource(testFileName).toURI());
        
        if (!file.exists() || !file.isFile()){
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

        return insidePreprocessingAndMatching(file, preprocessingPart, new ArrayList<String>(), etalonPart, ext, logger);
    }
}
