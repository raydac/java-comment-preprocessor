/*
 * Copyright 2011 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307  USA
 */
package com.igormaznitsa.jcp;

import com.igormaznitsa.jcp.cmdline.*;
import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.context.*;
import com.igormaznitsa.jcp.directives.*;
import com.igormaznitsa.jcp.exceptions.*;
import com.igormaznitsa.jcp.expression.*;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.io.*;
import java.util.*;

/**
 * The main class implements the Java Comment Preprocessor, it has the main method and can be started from a command string
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class JCPreprocessor {

    private final PreprocessorContext context;
    static final CommandLineHandler[] COMMAND_LINE_HANDLERS = new CommandLineHandler[]{
        new HelpHandler(),
        new InCharsetHandler(),
        new OutCharsetHandler(),
        new ClearDstDirectoryHandler(),
        new SourceDirectoryHandler(),
        new DestinationDirectoryHandler(),
        new FileExtensionsHandler(),
        new ExcludedFileExtensionsHandler(),
        new RemoveCommentsHandler(),
        new VerboseHandler(),
        new GlobalVariableDefiningFileHandler(),
        new GlobalVariableHandler()
    };

    public static Iterable<CommandLineHandler> getCommandLineHandlers(){
        return Arrays.asList(COMMAND_LINE_HANDLERS);
    }
    
    
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
        processCfgFiles();

        final File[] srcDirs = context.getSourceDirectoryAsFiles();

        final Collection<FileInfoContainer> filesToBePreprocessed = findAllFilesToBePreprocessed(srcDirs);

        final List<PreprocessingState.ExcludeIfInfo> excludedIf = processGlobalDirectives(filesToBePreprocessed);

        processFileExclusion(excludedIf);
        if (!context.isFileOutputDisabled()) {
            createDestinationDirectory();
        }
        preprocessFiles(filesToBePreprocessed);
    }

    private void processFileExclusion(final List<PreprocessingState.ExcludeIfInfo> foundExcludeIf) throws PreprocessorException {
        final String DIRECTIVE_NAME = AbstractDirectiveHandler.DIRECTIVE_PREFIX + (new ExcludeIfDirectiveHandler().getName());

        for (final PreprocessingState.ExcludeIfInfo item : foundExcludeIf) {
            final String condition = item.getCondition();
            final File file = item.getFileInfoContainer().getSourceFile();

            Value val = null;

            try {
                val = Expression.evalExpression(condition, context);
            } catch (IllegalArgumentException ex) {
                throw new PreprocessorException("Wrong expression at " + DIRECTIVE_NAME, condition, new FilePositionInfo[]{new FilePositionInfo(file, item.getStringIndex())}, ex);
            }

            if (val.getType() != ValueType.BOOLEAN) {
                throw new PreprocessorException("Expression at " + DIRECTIVE_NAME + " is not a boolean one", condition, new FilePositionInfo[]{new FilePositionInfo(file, item.getStringIndex())}, null);
            }

            if (val.asBoolean().booleanValue()) {
                item.getFileInfoContainer().setExcluded(true);
            }
        }
    }

    private List<PreprocessingState.ExcludeIfInfo> processGlobalDirectives(final Collection<FileInfoContainer> files) throws PreprocessorException, IOException {
        final List<PreprocessingState.ExcludeIfInfo> result = new ArrayList<PreprocessingState.ExcludeIfInfo>();
        for (final FileInfoContainer fileRef : files) {
            if (fileRef.isExcludedFromPreprocessing() || fileRef.isForCopyOnly()) {
                continue;
            } else {
                result.addAll(fileRef.processGlobalDirectives(null, context));
            }
        }
        return result;
    }

    private void preprocessFiles(final Collection<FileInfoContainer> files) throws IOException, PreprocessorException {
        for (final FileInfoContainer fileRef : files) {
            if (fileRef.isExcludedFromPreprocessing()) {
                continue;
            } else if (fileRef.isForCopyOnly()) {
                if (!context.isFileOutputDisabled()) {
                    PreprocessorUtils.copyFile(fileRef.getSourceFile(), context.createDestinationFileForPath(fileRef.getDestinationFilePath()));
                }
                continue;
            } else {
                fileRef.preprocessFile(null, context);
            }
        }
    }

    private void createDestinationDirectory() throws IOException {
        final File destination = context.getDestinationDirectoryAsFile();

        final boolean destinationExistsAndDirectory = destination.exists() && destination.isDirectory();

        if (context.doesClearDestinationDirBefore()) {
            if (destinationExistsAndDirectory) {
                if (!PreprocessorUtils.clearDirectory(destination)) {
                    throw new IOException("I can't clear the destination directory [" + PreprocessorUtils.getFilePath(destination) + ']');
                }
            }
        }
        if (!destinationExistsAndDirectory) {
            if (!destination.mkdirs()) {
                throw new IOException("I can't make the destination directory [" + PreprocessorUtils.getFilePath(destination) + ']');
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

    public static void main(final String... args) {
        printHeader();

        final String[] processedCommandStringArgs = PreprocessorUtils.replaceChar(args, '$', '\"');

        PreprocessorContext preprocessorContext = null;

        try {
            preprocessorContext = processCommandString(null, processedCommandStringArgs);
        } catch (IOException ex) {
            System.err.println("Error during command string processing [" + ex.getMessage() + ']');
            System.exit(1);
        }

        final JCPreprocessor preprocessor = new JCPreprocessor(preprocessorContext);

        try {
            preprocessor.execute();
        } catch (Exception unexpected) {
            preprocessorContext.logError(unexpected.toString());
            unexpected.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }

    private static PreprocessorContext processCommandString(final PreprocessorContext context, final String... args) throws IOException {
        final PreprocessorContext result = context == null ? new PreprocessorContext() : context;

        for (final String arg : args) {
            boolean processed = false;
            for (final CommandLineHandler processor : getCommandLineHandlers()) {
                if (processor.processCommandLineKey(arg, result)) {
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

    void processCfgFiles() throws IOException, PreprocessorException {

        for (final File file : context.getConfigFiles()) {
            final String[] wholeFile = PreprocessorUtils.readWholeTextFileIntoArray(file, "UTF-8");

            int readStringIndex = -1;
            for (final String curString : wholeFile) {
                final String trimmed = curString.trim();
                readStringIndex++;

                if (trimmed.isEmpty() || trimmed.charAt(0) == '#') {
                    continue;
                } else if (trimmed.charAt(0) == '@') {
                    PreprocessorUtils.throwPreprocessorException("You can't start any string in a global variable defining file with \'@\'", trimmed, file, readStringIndex, null);
                } else if (trimmed.charAt(0) == '/') {
                    // a command line argument
                    boolean processed = false;
                    try {
                        for (CommandLineHandler handler : getCommandLineHandlers()) {
                            if (handler.processCommandLineKey(trimmed, context)) {
                                if (context.isVerbose()) {
                                    context.logInfo("Processed key \'" + trimmed + "\' at " + file.getName() + ':' + (readStringIndex+1));
                                }
                                processed = true;
                                break;
                            }
                        }
                    } catch (Exception unexpected) {
                        PreprocessorUtils.throwPreprocessorException("Exception during directive processing", trimmed, file, readStringIndex, unexpected);
                    }

                    if (!processed) {
                        PreprocessorUtils.throwPreprocessorException("Unsupported or disallowed directive", trimmed, file, readStringIndex, null);
                    }
                } else {
                    // a global variable
                    final String[] splitted = PreprocessorUtils.splitForSetOperator(trimmed);
                    if (splitted.length != 2) {
                        PreprocessorUtils.throwPreprocessorException("Wrong variable definition", trimmed, file, readStringIndex, null);
                    }
                    final String name = splitted[0].trim().toLowerCase(Locale.ENGLISH);
                    final String expression = splitted[1].trim();
                    if (name.isEmpty()) {
                        PreprocessorUtils.throwPreprocessorException("Empty variable name detected", trimmed, file, readStringIndex, null);
                    }

                    try {
                        final Value result = Expression.evalExpression(expression, context);
                        context.setGlobalVariable(name, result);

                        if (context.isVerbose()) {
                            context.logInfo("Added global variable " + name + " = " + result.toString() + " (" + file.getName() + ':' + (readStringIndex+1) + ')');
                        }
                    } catch (Exception unexpected) {
                        PreprocessorUtils.throwPreprocessorException("Can't process the global variable definition", trimmed, file, readStringIndex, unexpected);
                    }
                }
            }
        }
    }

    private static void printHeader() {
        System.out.println(InfoHelper.getProductName() + ' ' + InfoHelper.getVersion());
        System.out.println(InfoHelper.getCopyright());
    }

    private static void help() {
        System.out.println();

        for (final String str : InfoHelper.makeTextForHelpInfo()) {
            System.out.println(str);
        }
    }
}
