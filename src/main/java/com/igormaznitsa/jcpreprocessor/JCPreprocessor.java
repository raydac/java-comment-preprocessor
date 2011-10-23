package com.igormaznitsa.jcpreprocessor;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.cmdline.CommandLineHandler;
import com.igormaznitsa.jcpreprocessor.cmdline.CharsetHandler;
import com.igormaznitsa.jcpreprocessor.cmdline.ClearDstDirectoryHandler;
import com.igormaznitsa.jcpreprocessor.cmdline.DestinationDirectoryHandler;
import com.igormaznitsa.jcpreprocessor.cmdline.ExcludedFileExtensionsHandler;
import com.igormaznitsa.jcpreprocessor.cmdline.HelpHandler;
import com.igormaznitsa.jcpreprocessor.cmdline.FileExtensionsHandler;
import com.igormaznitsa.jcpreprocessor.cmdline.RemoveCommentsHandler;
import com.igormaznitsa.jcpreprocessor.cmdline.SourceDirectoryHandler;
import com.igormaznitsa.jcpreprocessor.cmdline.VerboseHandler;
import com.igormaznitsa.jcpreprocessor.exceptions.PreprocessorException;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.references.FileReference;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class JCPreprocessor {

    private final PreprocessorContext context;
    private static final CommandLineHandler[] COMMAND_LINE_PROCESSORS = new CommandLineHandler[]{
        new HelpHandler(),
        new VerboseHandler(),
        new CharsetHandler(),
        new ClearDstDirectoryHandler(),
        new SourceDirectoryHandler(),
        new DestinationDirectoryHandler(),
        new FileExtensionsHandler(),
        new ExcludedFileExtensionsHandler(),
        new RemoveCommentsHandler(),
        new VerboseHandler()
    };

    public PreprocessorContext getContext() {
        return context;
    }

    public JCPreprocessor(final PreprocessorContext context) {
        if (context == null) {
            throw new NullPointerException("Configurator is null");
        }
        this.context = context;
    }

    public void execute() throws PreprocessorException, IOException {
        final File[] srcDirs = context.getParsedSourceDirectoryAsFiles();

        final Collection<FileReference> filesToBePreprocessed = findAllFilesToBePreprocessed(srcDirs);

        fillGlobalVariables(filesToBePreprocessed);
        processExcludeIf(filesToBePreprocessed);

        createDestinationDirectory();

        for (final FileReference fileRef : filesToBePreprocessed) {
            if (fileRef.isExcluded()) {
                continue;
            } else if (fileRef.isOnlyForCopy()) {
                PreprocessorUtils.copyFile(fileRef.getSourceFile(), context.makeDestinationFile(fileRef.getDestinationFilePath()));
                continue;
            } else {
                fileRef.preprocess(context);
            }
        }

    }

    private final void createDestinationDirectory() throws IOException {
        final File destination = context.getDestinationDirectoryAsFile();

        final boolean destinationExistsAndDirectory = destination.exists() && destination.isDirectory();

        if (context.doesClearDestinationDirBefore()) {
            if (destinationExistsAndDirectory) {
                if (!PreprocessorUtils.clearDirectory(destination)) {
                    throw new IOException("I can't clear the destination directory [" + destination.getAbsolutePath() + ']');
                }
            }
        }

        if (!destinationExistsAndDirectory) {
            if (!destination.mkdirs()) {
                throw new IOException("I can't make the destination directory [" + destination.getAbsolutePath() + ']');
            }
        }
    }

    private final void fillGlobalVariables(Collection<FileReference> files) throws PreprocessorException, IOException {
        int i_stringLine = 0;
        String s_fileName = null;

        Iterator p_iter = files.iterator();
        int i_ifcounter = 0;
        int i_activeif = 0;

        String s_strLastIfFileName = null;
        String s_LastIfTrimmedString = null;
        int i_lastIfStringNumber = 0;

        while (p_iter.hasNext()) {
            FileReference p_fr = (FileReference) p_iter.next();

            final File processingFile = p_fr.getSourceFile();

            s_fileName = p_fr.getSourceFile().getAbsolutePath();
            if (p_fr.isOnlyForCopy()) {
                continue;
            }
            BufferedReader p_bufreader = PreprocessorUtils.makeFileReader(p_fr.getSourceFile(), context.getCharacterEncoding(), -1);
            boolean lg_ifenabled = true;
            i_stringLine = 0;

            while (true) {
                final String readString = p_bufreader.readLine();
                if (readString == null) {
                    break;
                }
                i_stringLine++;

                final String trimmedReadString = readString.trim();

                if (trimmedReadString.startsWith("//#_if")) {
                    // Processing #ifg instruction
                    if (lg_ifenabled) {
                        final Value p_value = Expression.eval(PreprocessorUtils.extractTrimmedTail("//#_if", trimmedReadString), context);

                        if (p_value == null || p_value.getType() != ValueType.BOOLEAN) {
                            throw new PreprocessorException("There must be a BOOLEAN expression result to be used by a //#_if directive", processingFile, processingFile, trimmedReadString, i_stringLine, null);
                        }

                        if (i_ifcounter == 0) {
                            s_strLastIfFileName = s_fileName;
                            i_lastIfStringNumber = i_stringLine;
                            s_LastIfTrimmedString = trimmedReadString;
                        }
                        i_ifcounter++;
                        i_activeif = i_ifcounter;

                        if (((Boolean) p_value.getValue()).booleanValue()) {
                            lg_ifenabled = true;
                        } else {
                            lg_ifenabled = false;
                        }
                    } else {
                        i_ifcounter++;
                    }
                } else if (trimmedReadString.startsWith("//#_else")) {
                    if (i_ifcounter == 0) {
                        throw new PreprocessorException("Found //#_else without //#_if", processingFile, processingFile, trimmedReadString, i_stringLine, null);
                    }

                    if (i_ifcounter == i_activeif) {
                        lg_ifenabled = !lg_ifenabled;
                    }
                } else if (trimmedReadString.startsWith("//#_endif")) {
                    if (i_ifcounter == 0) {
                        throw new PreprocessorException("Found //#_endif without //#_if", processingFile, processingFile, trimmedReadString, i_stringLine, null);
                    }

                    if (i_ifcounter == i_activeif) {
                        i_ifcounter--;
                        i_activeif--;
                        lg_ifenabled = true;
                    } else {
                        i_ifcounter--;
                    }
                } else if (trimmedReadString.startsWith("//#global")) {
                    if (!lg_ifenabled) {
                        continue;
                    }
                    try {
                        final String trimmedTailString = PreprocessorUtils.extractTrimmedTail("//#global", trimmedReadString);
                        int i_equ = trimmedTailString.indexOf('=');
                        if (i_equ < 0) {
                            throw new IOException();
                        }
                        String s_name = trimmedTailString.substring(0, i_equ).trim();
                        String s_eval = trimmedTailString.substring(i_equ + 1).trim();

                        if (context.containsGlobalVariable(s_name)) {
                            throw new IOException("You have duplicated the global variable " + s_name);
                        }

                        Value p_value = Expression.eval(s_eval, context);
                        if (p_value == null) {
                            throw new IOException("Error value");
                        }
                        context.setGlobalVariable(s_name, p_value);

                        context.info("\'" + s_name + "\' = \'" + p_value + "\'");
                    } catch (IOException e) {
                        throw new PreprocessorException("Exception during a global variable definition", processingFile, processingFile, trimmedReadString, i_stringLine, null);
                    }
                }
            }
            p_bufreader.close();

            if (i_ifcounter > 0) {
                throw new PreprocessorException("Unclosed //#_if directive detected", processingFile, processingFile, s_LastIfTrimmedString, i_lastIfStringNumber, null);
            }
        }
    }

    private void processExcludeIf(final Collection<FileReference> files) throws IOException {
        int i_stringLine = 0;
        String s_fileName = null;

        try {
            Iterator p_iter = files.iterator();
            int i_ifcounter = 0;
            int i_activeif = 0;

            String s_strLastIfFileName = null;
            int i_lastIfStringNumber = 0;

            while (p_iter.hasNext()) {
                FileReference p_fr = (FileReference) p_iter.next();
                s_fileName = p_fr.getSourceFile().getCanonicalPath();
                if (p_fr.isOnlyForCopy()) {
                    continue;
                }
                BufferedReader p_bufreader = PreprocessorUtils.makeFileReader(p_fr.getSourceFile(), context.getCharacterEncoding(), -1);
                boolean lg_ifenabled = true;
                i_stringLine = 0;

                while (true) {
                    String s_str = p_bufreader.readLine();
                    if (s_str == null) {
                        break;
                    }
                    i_stringLine++;

                    s_str = s_str.trim();

                    if (s_str.startsWith("//#_if")) {
                        // Processing #_if instruction
                        if (lg_ifenabled) {
                            s_str = s_str.substring(6).trim();
                            Value p_value = Expression.eval(s_str, context);
                            if (p_value == null || p_value.getType() != ValueType.BOOLEAN) {
                                throw new IOException("You don't have a boolean result in the #_if instruction");
                            }
                            if (i_ifcounter == 0) {
                                s_strLastIfFileName = s_fileName;
                                i_lastIfStringNumber = i_stringLine;
                            }
                            i_ifcounter++;
                            i_activeif = i_ifcounter;

                            if (((Boolean) p_value.getValue()).booleanValue()) {
                                lg_ifenabled = true;
                            } else {
                                lg_ifenabled = false;
                            }
                        } else {
                            i_ifcounter++;
                        }
                    } else if (s_str.startsWith("//#_else")) {
                        if (i_ifcounter == 0) {
                            throw new IOException("You have got an #_else instruction without #_if");
                        }

                        if (i_ifcounter == i_activeif) {
                            lg_ifenabled = !lg_ifenabled;
                        }
                    } else if (s_str.startsWith("//#_endif")) {
                        if (i_ifcounter == 0) {
                            throw new IOException("You have got an #_endif instruction without #_if");
                        }

                        if (i_ifcounter == i_activeif) {
                            i_ifcounter--;
                            i_activeif--;
                            lg_ifenabled = true;
                        } else {
                            i_ifcounter--;
                        }
                    } else if (s_str.startsWith("//#excludeif")) {
                        if (lg_ifenabled) {
                            try {
                                s_str = s_str.substring(12).trim();
                                Value p_value = Expression.eval(s_str, context);

                                if (p_value == null || p_value.getType() != ValueType.BOOLEAN) {
                                    throw new IOException("non boolean expression");
                                }
                                if (((Boolean) p_value.getValue()).booleanValue()) {
                                    p_fr.setExcluded(true);
                                }
                            } catch (IOException e) {
                                throw new IOException("You have the error in the #excludeif instruction in the file " + p_fr.getSourceFile().getCanonicalPath() + " line: " + i_stringLine + " [" + e.getMessage() + "]");
                            }
                        }
                    }
                }
                p_bufreader.close();

                if (i_ifcounter > 0) {
                    throw new IOException("You have an unclosed #ifg construction [" + s_strLastIfFileName + ":" + i_lastIfStringNumber + "]");
                }
            }
        } catch (Exception _ex) {
            throw new IOException(s_fileName + ":" + i_stringLine + " " + _ex.getMessage());
        }
    }

    private Collection<FileReference> findAllFilesToBePreprocessed(final File[] srcDirs) throws IOException {
        final Collection<FileReference> result = new ArrayList<FileReference>();

        for (final File dir : srcDirs) {
            final String canonicalPathForSrcDirectory = dir.getCanonicalPath();
            final Set<File> allFoundFiles = findAllFiles(dir);


            for (final File file : allFoundFiles) {
                final String extension = PreprocessorUtils.getFileExtension(file);

                if (context.isFileExcludedFromProcess(file)) {
                    // ignore excluded file
                    continue;
                }

                final String filePath = file.getCanonicalPath();
                final String relativePath = filePath.substring(canonicalPathForSrcDirectory.length());

                final FileReference reference = new FileReference(file, relativePath, !context.isFileAllowedToBeProcessed(file));
                result.add(reference);
            }

        }

        return result;
    }

    private Set<File> findAllFiles(final File dir) {
        final Set<File> result = new HashSet<File>();
        final File[] allowedFiles = dir.listFiles();
        for (final File file : allowedFiles) {
            if (file.isDirectory()) {
                result.addAll(findAllFiles(file));
            } else {
                result.add(file);
            }
        }
        return result;
    }

    public static final void main(final String... args) {
        final String[] processedCommandStringArgs = PreprocessorUtils.replaceChar(args, '$', '\"');

        PreprocessorContext cfg = null;

        try {
            cfg = processCommandString(null, processedCommandStringArgs);
        } catch (IOException ex) {
            System.err.println("Error during command string processing [" + ex.getMessage() + ']');
            System.exit(1);
        }

        final JCPreprocessor preprocessor = new JCPreprocessor(cfg);

        try {
            preprocessor.execute();
        } catch (Exception ex) {
            cfg.error(ex.toString());
            System.exit(1);
        }

        System.exit(0);
    }

    private static PreprocessorContext processCommandString(final PreprocessorContext configurator, final String... args) throws IOException {
        final PreprocessorContext result = configurator == null ? new PreprocessorContext() : configurator;

        for (final String arg : args) {
            boolean processed = false;
            for (final CommandLineHandler processor : COMMAND_LINE_PROCESSORS) {
                if (processor.processArgument(arg, result)) {
                    processed = true;
                    if (processor instanceof HelpHandler) {
                        help();
                        System.exit(1);
                    }
                    break;
                }
            }

            if (!processed) {
                help();
                System.exit(1);
            }
        }

        return result;
    }

    private static void loadVariablesFromFile(final String fileName, final PreprocessorContext context) throws IOException {
        final File cfgFile = new File(fileName);

        if (!cfgFile.exists() || cfgFile.isDirectory()) {
            throw new IOException("I can't find the file " + cfgFile.getPath());
        }

        BufferedReader p_bufreader = PreprocessorUtils.makeFileReader(cfgFile, context.getCharacterEncoding(), -1);
        try {
            int strCounter = 0;

            while (true) {
                String readString = p_bufreader.readLine();
                if (readString == null) {
                    break;
                }
                strCounter++;

                readString = readString.trim();

                if (readString.length() == 0 || readString.startsWith("#")) {
                    continue;
                }

                readString = PreprocessorUtils.processMacros(cfgFile, readString, context);

                String[] parsedValue = PreprocessorUtils.splitForChar(readString, '=');

                String varName = null;
                String varValue = null;

                int i_equindx = readString.indexOf('=');
                if (parsedValue.length != 2) {
                    throw new IOException("Wrong global parameter format [" + readString + "] detected in  " + cfgFile.getPath() + " at the line:" + strCounter);
                }

                varName = parsedValue[0];
                varValue = parsedValue[1];

                Value evaluatedValue = null;
                varValue = varValue.trim();
                if (varValue.startsWith("@")) {
                    // This is a file
                    varValue = PreprocessorUtils.extractTail("@", varValue);
                    evaluatedValue = Expression.eval(varValue, context);
                    if (evaluatedValue == null || evaluatedValue.getType() != ValueType.STRING) {
                        throw new IOException("You have not a string value in " + cfgFile.getPath() + " at line:" + strCounter);
                    }
                    varValue = (String) evaluatedValue.getValue();

                    loadVariablesFromFile(varValue, context);
                } else {
                    // This is a value
                    evaluatedValue = Expression.eval(varValue, context);
                    if (evaluatedValue == null) {
                        throw new IOException("Wrong value definition [" + readString + "] in " + cfgFile.getAbsolutePath() + " at " + strCounter);
                    }
                }

                if (context.containsGlobalVariable(varName)) {
                    throw new IOException("Duplicated global variable name [" + varName + "] in " + cfgFile.getPath() + " at line:" + strCounter);
                }

                if (context.isVerbose()) {
                    context.info("A global variable has been added [" + varName + "=" + evaluatedValue.toString() + "] from " + cfgFile.getPath() + " file at line:" + strCounter);
                }
                context.setGlobalVariable(varName, evaluatedValue);
            }
        } finally {
            if (p_bufreader != null) {
                try {
                    p_bufreader.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    private static void help() {
        System.out.println(InfoHelper.getProductName() + ' ' + InfoHelper.getVersion());
        System.out.println(InfoHelper.getCopyright());
        System.out.println();

        System.out.println("Command line arguments");
        System.out.println("---------------------------");

        for (final CommandLineHandler processor : COMMAND_LINE_PROCESSORS) {
            System.out.println(processor.getKeyName() + "\t\t" + processor.getDescription());
        }
    }
}
