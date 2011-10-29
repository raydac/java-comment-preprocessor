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
import com.igormaznitsa.jcpreprocessor.containers.FileInfoContainer;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.directives.AbstractDirectiveHandler;
import com.igormaznitsa.jcpreprocessor.directives.ExcludeIfDirectiveHandler;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

        final Collection<FileInfoContainer> filesToBePreprocessed = findAllFilesToBePreprocessed(srcDirs);

        final List<PreprocessingState.ExcludeIfInfo> excludedIf = processFirstPass(filesToBePreprocessed);

        processFileExclusion(excludedIf);
        createDestinationDirectory();
        processSecondPass(filesToBePreprocessed);
    }

    private void processFileExclusion(final List<PreprocessingState.ExcludeIfInfo> foundExcludeIf) throws PreprocessorException {
        final String DIRECTIVE_NAME = AbstractDirectiveHandler.DIRECTIVE_PREFIX+(new ExcludeIfDirectiveHandler().getName());
  
        for (final PreprocessingState.ExcludeIfInfo item : foundExcludeIf) {
            final String condition = item.getCondition();
            final File file = item.getFileInfoContainer().getSourceFile();
            try {
                final Value val = Expression.eval(condition, context);
                
                if (val == null){
                    throw new PreprocessorException("Wrong expression at "+DIRECTIVE_NAME, file, file, condition, item.getStringIndex()-1, null);
                }
                
                if (val.getType() != ValueType.BOOLEAN) {
                    throw new PreprocessorException("Expression at "+DIRECTIVE_NAME+" is not a boolean one", file, file, condition, item.getStringIndex()-1, null);
                }
            } catch (Exception ex) {
                throw new PreprocessorException("Exception at at "+DIRECTIVE_NAME+" is not a boolean one", file, file, condition, item.getStringIndex()-1, ex);
            }
        }
    }

    private List<PreprocessingState.ExcludeIfInfo> processFirstPass(final Collection<FileInfoContainer> files) throws PreprocessorException, IOException {
        final List<PreprocessingState.ExcludeIfInfo> result = new ArrayList<PreprocessingState.ExcludeIfInfo>();
        for (final FileInfoContainer fileRef : files) {
            if (fileRef.isExcludedFromPreprocessing() || fileRef.isForCopyOnly()) {
                continue;
            } else {
                result.addAll(fileRef.processGlobalDirectives(context));
            }
        }
        return result;
    }

    private void processSecondPass(final Collection<FileInfoContainer> files) throws IOException, PreprocessorException {
        for (final FileInfoContainer fileRef : files) {
            if (fileRef.isExcludedFromPreprocessing()) {
                continue;
            } else if (fileRef.isForCopyOnly()) {
                PreprocessorUtils.copyFile(fileRef.getSourceFile(), context.makeDestinationFile(fileRef.getDestinationFilePath()));
                continue;
            } else {
                fileRef.preprocessFile(null,context);
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

    private Collection<FileInfoContainer> findAllFilesToBePreprocessed(final File[] srcDirs) throws IOException {
        final Collection<FileInfoContainer> result = new ArrayList<FileInfoContainer>();

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

                final FileInfoContainer reference = new FileInfoContainer(file, relativePath, !context.isFileAllowedToBeProcessed(file));
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

        final BufferedReader fileReader = PreprocessorUtils.makeFileReader(cfgFile, context.getCharacterEncoding(), -1);
        try {
            int strCounter = 0;

            while (true) {
                String readString = fileReader.readLine();
                if (readString == null) {
                    break;
                }
                strCounter++;

                readString = readString.trim();

                if (readString.length() == 0 || readString.startsWith("#")) {
                    continue;
                }

                readString = PreprocessorUtils.processMacroses(readString, context);

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
            if (fileReader != null) {
                try {
                    fileReader.close();
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
