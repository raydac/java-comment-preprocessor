# Java Comment Preprocessor — User Manual

|                          |                                        |
|--------------------------|----------------------------------------|
| **Preprocessor version** | 7.3.1-SNAPSHOT (latest release: 7.3.0) |
| **Document version**     | 1.0                                    |
| **Document date**        | 2026-07-21                             |

---

## Table of Contents

1. [What Is JCP and Why Use It in a Java Project](#1-what-is-jcp-and-why-use-it-in-a-java-project)
2. [How to Connect JCP to Your Project](#2-how-to-connect-jcp-to-your-project)
    - [2.1 Command Line (CLI)](#21-command-line-cli)
    - [2.2 Maven Projects](#22-maven-projects)
    - [2.3 Gradle Projects](#23-gradle-projects)
3. [Preprocessor Directives and Processing Phases](#3-preprocessor-directives-and-processing-phases)
4. [Preprocessor Functions](#4-preprocessor-functions)
5. [Appendix: Special Variables and Expression Syntax](#5-appendix-special-variables-and-expression-syntax)

---

## 1. What Is JCP and Why Use It in a Java Project

**Java Comment Preprocessor (JCP)** is a source-code preprocessor for Java and similar C-style languages. It reads
directives placed inside comments, evaluates expressions, and writes transformed source files to an output folder.

Because directives live in comments, your original sources stay valid for the IDE and for the compiler **before**
preprocessing. JCP runs as a separate build step and produces the files that compilation actually uses.

### Typical use cases

| Use case                                 | What JCP helps with                                                                                           |
|------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| **Multi-platform or multi-version code** | Keep one source tree; enable or disable blocks with `//#if` for different JDK versions, devices, or features. |
| **Code generation from templates**       | Use loops, includes, and variables to generate repetitive Java code without a separate generator tool.        |
| **Static site or resource generation**   | Process HTML, text, or XML templates (when included in file extensions) into final files.                     |
| **Build-time configuration**             | Inject Maven/Gradle properties, paths, and flags into sources as constants or conditional blocks.             |
| **JEP 238 multi-release JARs**           | Prepare version-specific source variants before packaging.                                                    |
| **Comment cleanup**                      | Optionally remove preprocessor directives or all comments from output.                                        |

### How it fits into a Java build

```
  Original sources          JCP preprocessing          Compiler
  (src/main/java)    -->    (generated folder)    -->   (javac)
       |                          |
   //#directives              plain Java
   /*$macros$*/               (directives removed
                               or kept as comments)
```

JCP is delivered as one **uber-jar** that also contains:

- a **CLI** tool,
- a **Java API** (`JcpPreprocessor`),
- a **Maven plugin** (`preprocess` goal),
- a **Gradle plugin** (`preprocess` task),
- and an **Ant task**.

Minimum runtime: **Java 11+**. The preprocessor JAR works with Maven 3.0+, Gradle 6.0+, and Ant 1.8.2+.

---

## 2. How to Connect JCP to Your Project

### 2.1 Command Line (CLI)

Download the JCP uber-jar from [Maven Central](https://search.maven.org/artifact/com.igormaznitsa/jcp) or build it from
source, then run:

```bash
java -jar jcp-7.3.0.jar --i:./src --o:./build/preprocessed
```

#### Basic workflow

1. Put sources with preprocessor directives into the **input folder** (`--i:`).
2. Run JCP.
3. Use files from the **output folder** (`--o:`) for compilation or deployment.

By default JCP preprocesses files with extensions `java`, `txt`, `htm`, and `html`. Files with other extensions are
**copied** unchanged (unless excluded). XML is excluded by default.

#### Command-line argument prefixes

Arguments may start with `/`, `-`, or `--`. In documentation below, the `/` form is shown; `-` and `--` work the same
way (for example `/I:` = `-I:` = `--i:`).

You may also pass a **config file** as the first argument without a prefix. Lines in the config file can define global
variables (`name=expression`) or repeat CLI switches (lines starting with `/`).

Set the base directory for relative paths with the system property:

```bash
java -Djcp.base.dir=/path/to/project -jar jcp-7.3.0.jar /I:src /O:target/preprocessed
```

If not set, the current working directory is used.

#### CLI arguments reference

| Key        | Description                                                                                                                                            |
|------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/H`, `/?` | Show full help (directives, functions, operators, special variables) and exit.                                                                         |
| `/I:`      | **Input folder** — source directory to scan. Default: `./`                                                                                             |
| `/O:`      | **Output folder** — destination for preprocessed files. Default: `../preprocessed`                                                                     |
| `/C`       | **Clear target** — delete contents of the output folder before processing.                                                                             |
| `/F:`      | **Allowed extensions** — comma-separated list of file extensions to preprocess. Default: `java,txt,htm,html`                                           |
| `/EF:`     | **Excluded extensions** — comma-separated list of extensions to skip entirely. Default: `xml`                                                          |
| `/ED:`     | **Excluded folders** — ANT-style path patterns, separated by the OS path separator.                                                                    |
| `/P:`      | **Global variable** — define a global variable, e.g. `/P:DEBUG=true` or `/P:MSG=$Hello world$` (use `$` instead of `"` in CLI strings).                |
| `@`        | **Config file** — load variables and CLI options from a file.                                                                                          |
| `/T:`      | **Input charset** — encoding for reading source files. Default: `UTF-8`                                                                                |
| `/TT:`     | **Output charset** — encoding for writing result files. Default: `UTF-8`                                                                               |
| `/R`       | **Remove all comments** from result files.                                                                                                             |
| `/M:`      | **Comment mode** — how to handle comments in output. Values: `KEEP_ALL`, `REMOVE_C_STYLE`, `REMOVE_JCP_ONLY`, or `true`/`false`. Default: keep all.    |
| `/K`       | **Keep lines** — leave unprocessed directive lines as comments (helps preserve line numbers). Default: on in Maven/Gradle; off in bare CLI unless set. |
| `/V`       | **Verbose** — print detailed processing log.                                                                                                           |
| `/U`       | **Unknown as false** — treat undefined variables as `false` instead of error.                                                                          |
| `/Z`       | **Skip same content** — do not overwrite a target file if its content is unchanged.                                                                    |
| `/N`       | **Care for last EOL** — preserve the end-of-line character at the end of files.                                                                        |
| `/ES`      | **Allow whitespace** — allow spaces between `//` and `#` in directives (e.g. `// #if`).                                                                |
| `/PI`      | **Preserve indents** — when using `//$` / `//$$`, replace the directive with spaces to keep indentation.                                               |
| `/B`       | **Allow blocks** — treat `//$"""` and `//$$"""` lines as single multiline text blocks.                                                                 |
| `/A`       | **Keep attributes** — copy file attributes (timestamps, permissions) to output files.                                                                  |
| `/EA:`     | **Action extension** — class name implementing `PreprocessorExtension` for `//#action` directives.                                                     |

#### CLI examples

**Simple preprocessing:**

```bash
java -jar jcp-7.3.0.jar /I:./test /O:./result
```

**Full-featured run:**

```bash
java -jar jcp-7.3.0.jar \
  /C /R /V \
  /F:java,xml /EF:none \
  /I:./test /O:./result \
  '/P:HelloWorld=$Hello world$' \
  /Z /ES
```

| Flag in example | Effect                           |
|-----------------|----------------------------------|
| `/C`            | Clear `./result` before writing  |
| `/R`            | Strip all comments from output   |
| `/V`            | Verbose logging                  |
| `/F:java,xml`   | Also preprocess `.xml` files     |
| `/EF:none`      | Do not exclude any extension     |
| `/P:...`        | Set global variable `HelloWorld` |
| `/Z`            | Skip writing unchanged files     |
| `/ES`           | Allow `// #if` style directives  |

**Remove all comments:**

```bash
java -jar jcp-7.3.0.jar /I:/sourceFolder /O:/resultFolder /EF:none /R
```

---

### 2.2 Maven Projects

Add the JCP plugin to your `pom.xml`. The artifact is published as `com.igormaznitsa:jcp`.

#### Minimal setup

```xml

<build>
    <plugins>
        <plugin>
            <groupId>com.igormaznitsa</groupId>
            <artifactId>jcp</artifactId>
            <version>7.3.0</version>
            <executions>
                <execution>
                    <id>preprocessSources</id>
                    <phase>generate-sources</phase>
                    <goals>
                        <goal>preprocess</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

After `mvn clean compile`, preprocessed sources appear in:

```
target/generated-sources/preprocessed/
```

By default the plugin **replaces** the project compile source root with this folder, so `mvn compile` uses preprocessed
code automatically.

#### Preprocessing test sources

Add a second execution bound to `generate-test-sources`:

```xml

<execution>
    <id>preprocessTestSources</id>
    <phase>generate-test-sources</phase>
    <goals>
        <goal>preprocess</goal>
    </goals>
    <configuration>
        <useTestSources>true</useTestSources>
    </configuration>
</execution>
```

Test output goes to `target/generated-test-sources/preprocessed/`.

#### Common Maven configuration options

| Parameter                           | Default                                      | Description                                                       |
|-------------------------------------|----------------------------------------------|-------------------------------------------------------------------|
| `sources`                           | project compile (or test) roots              | Explicit list of source folders                                   |
| `target`                            | `target/generated-sources/preprocessed`      | Output folder for main sources                                    |
| `targetTest`                        | `target/generated-test-sources/preprocessed` | Output folder for test sources                                    |
| `replaceSources`                    | `true`                                       | Replace Maven source roots with preprocessed folder               |
| `extensions`                        | `java, txt, htm, html`                       | Extensions to preprocess                                          |
| `excludeExtensions`                 | `xml`                                        | Extensions to exclude                                             |
| `excludeFolders`                    | —                                            | ANT-style folder patterns to skip                                 |
| `vars`                              | —                                            | Map of global variables (`<name>value</name>`)                    |
| `configFiles`                       | —                                            | External config files with variables/CLI options                  |
| `sourceEncoding` / `targetEncoding` | `${project.build.sourceEncoding}`            | File encodings                                                    |
| `keepComments`                      | `KEEP_ALL`                                   | Comment handling: `KEEP_ALL`, `REMOVE_C_STYLE`, `REMOVE_JCP_ONLY` |
| `keepLines`                         | `true`                                       | Keep directive lines as comments in output                        |
| `clearTarget`                       | `false`                                      | Clear output folder before run                                    |
| `verbose`                           | `false`                                      | Verbose logging                                                   |
| `unknownVarAsFalse`                 | `false`                                      | Treat unknown variables as `false`                                |
| `dontOverwriteSameContent`          | `false`                                      | Skip writing unchanged files                                      |
| `allowWhitespaces`                  | `false`                                      | Allow spaces in `// #directive`                                   |
| `preserveIndents`                   | `false`                                      | Preserve indentation for `//$` / `//$$`                           |
| `allowBlocks`                       | `false`                                      | Enable multiline `//$"""` blocks                                  |
| `careForLastEol`                    | `false`                                      | Preserve trailing end-of-line                                     |
| `keepAttributes`                    | `false`                                      | Copy file attributes to output                                    |
| `dryRun`                            | `false`                                      | Process without writing files                                     |
| `skip`                              | `false`                                      | Skip preprocessing (`-Djcp.preprocess.skip=true`)                 |
| `ignoreMissingSources`              | `false`                                      | Ignore missing source folders                                     |
| `useTestSources`                    | `false`                                      | Use test source roots instead of main                             |
| `actionPreprocessorExtensions`      | —                                            | List of `PreprocessorExtension` class names                       |
| `eol`                               | `${line.separator}`                          | End-of-line string for output files                               |
| `baseDir`                           | `${project.basedir}`                         | Base directory for relative paths                                 |

When the plugin runs inside Maven, it also registers **Maven-aware special variables** (project version, properties,
etc.) through `MavenPropertiesImporter`.

#### Example with variables and options

```xml

<plugin>
    <groupId>com.igormaznitsa</groupId>
    <artifactId>jcp</artifactId>
    <version>7.3.0</version>
    <configuration>
        <allowWhitespaces>true</allowWhitespaces>
        <extensions>
            <extension>java</extension>
        </extensions>
        <excludeExtensions>
            <extension>xml</extension>
        </excludeExtensions>
        <vars>
            <feature.debug>true</feature.debug>
        </vars>
        <verbose>true</verbose>
        <clearTarget>true</clearTarget>
    </configuration>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>preprocess</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

More details: [Maven plugin site](https://raydac.github.io/jcp-maven-plugin-site/index.html).

---

### 2.3 Gradle Projects

JCP provides the Gradle plugin id **`com.igormaznitsa.jcp`**. It registers a task named **`preprocess`**.

#### Setup (Gradle 6+)

```gradle
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "com.igormaznitsa:jcp:7.3.0"
    }
}

apply plugin: 'java'
apply plugin: 'com.igormaznitsa.jcp'
```

For **Gradle 7+** you can use the `plugins` block if the plugin is published to a repository that supports it; the
`buildscript` + `apply plugin` approach works on all supported Gradle versions.

#### Configure the preprocess task

```gradle
preprocess {
    sources = sourceSets.main.java.srcDirs
    target = file("$buildDir/java-comment-preprocessor/main")
    fileExtensions = ['java']
    excludeExtensions = ['xml']
    vars = [
        'feature.debug': 'true'
    ]
    verbose = true
    clearTarget = true
}
```

**Important:** unlike the Maven plugin, Gradle does **not** automatically switch source folders. Point compilation at
the preprocessed output:

```gradle
compileJava.dependsOn preprocess

// After preprocessing, use the generated folder for compilation
sourceSets.main.java.srcDirs = [preprocess.target]
```

Or use a dedicated task:

```gradle
task usePreprocessedSources {
    sourceSets.main.java.srcDirs = [preprocess.target]
}.dependsOn preprocess

compileJava.dependsOn usePreprocessedSources
```

#### Android project example

```gradle
buildscript {
    dependencies {
        classpath "com.igormaznitsa:jcp:7.3.0"
    }
}
apply plugin: 'com.android.application'
apply plugin: 'com.igormaznitsa.jcp'

preprocess {
    sources = android.sourceSets.main.java.srcDirs
    keepComments = false
    vars = ['remove.secret': 'true']
}

preBuild.dependsOn preprocess

task switchToPreprocessedSources {
    android.sourceSets.main.java.srcDirs = [preprocess.target]
}.dependsOn preprocess
```

#### Gradle task properties

Most Maven parameters have Gradle equivalents on the `preprocess` task:

| Property                            | Default                                      | Description                                                    |
|-------------------------------------|----------------------------------------------|----------------------------------------------------------------|
| `sources`                           | *(required)*                                 | List of source directories                                     |
| `target`                            | `$buildDir/java-comment-preprocessor/<task>` | Output directory                                               |
| `fileExtensions`                    | `java, txt, htm, html`                       | Extensions to preprocess                                       |
| `excludeExtensions`                 | `xml`                                        | Extensions to skip                                             |
| `excludeFolders`                    | —                                            | ANT-style excluded folder patterns                             |
| `vars`                              | —                                            | Map of global variables                                        |
| `configFiles`                       | —                                            | External config files                                          |
| `sourceEncoding` / `targetEncoding` | `UTF-8`                                      | File encodings                                                 |
| `keepComments`                      | `false`                                      | Comment mode (`KEEP_ALL`, `REMOVE_C_STYLE`, `REMOVE_JCP_ONLY`) |
| `keepLines`                         | `true`                                       | Keep directive lines as comments                               |
| `clearTarget`                       | `false`                                      | Clear target folder                                            |
| `verbose`                           | `false`                                      | Verbose logging                                                |
| `unknownVarAsFalse`                 | `false`                                      | Unknown variables = `false`                                    |
| `dontOverwriteSameContent`          | `false`                                      | Skip unchanged files                                           |
| `allowWhitespaces`                  | `false`                                      | Spaces in directives                                           |
| `preserveIndents`                   | `false`                                      | Indent preservation for `//$`                                  |
| `allowBlocks`                       | `false`                                      | Multiline block mode                                           |
| `careForLastEol`                    | `false`                                      | Preserve trailing EOL                                          |
| `keepAttributes`                    | `false`                                      | Copy file attributes                                           |
| `dryRun`                            | `false`                                      | Process without writing                                        |
| `ignoreMissingSources`              | `false`                                      | Ignore missing folders                                         |
| `actionPreprocessorExtensions`      | —                                            | Extension class names                                          |
| `eol`                               | system line separator                        | Output end-of-line                                             |
| `baseDir`                           | `$projectDir`                                | Base for relative paths                                        |

After the task runs, `preprocess.incomingFiles` and `preprocess.outcomingFiles` expose input and output file collections
for build inspection.

---

## 3. Preprocessor Directives and Processing Phases

### How directives look in Java source

All standard directives use the prefix **`//#`** inside a comment:

```java
//#local DEBUG=true
//#if DEBUG
System.out.println("Debug mode");
//#endif
```

**Macros** (expression substitution) use the form `/*$expression$*/`:

```java
System.out.println("Version: /*$jcp.version$*/");
System.out.

println("Line: /*$__line__$*/");
```

Variable names in expressions are **case-insensitive**.

### Processing phases

JCP works in **two passes** for each file:

```
┌─────────────────────────────────────────────────────────────┐
│  Phase 1 — Global phase (1st pass)                          │
│  Only global directives run. Used to set up file-level       │
│  decisions before main processing.                           │
│  Directives: //#global, //#_if, //#_else, //#_endif,        │
│              //#excludeif                                   │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│  Phase 2 — Preprocessing phase (2nd pass)                   │
│  Full directive set, includes, loops, macros, output control.│
│  Most //#directives work here.                              │
└─────────────────────────────────────────────────────────────┘
```

**Typical full run order** (all files):

1. Scan source folders and collect files.
2. **Global phase** on each file — evaluate `//#global`, `//#_if`, `//#excludeif`, etc.
3. Apply `//#excludeif` results — excluded files are skipped.
4. Prepare the target folder.
5. **Preprocessing phase** — transform each remaining file and write output (or copy unchanged files).

### Multi-section Java documents

Java files have three logical output sections:

| Section           | Typical content                               |
|-------------------|-----------------------------------------------|
| **Prefix**        | `import` statements, package declaration area |
| **Middle (body)** | Main class code                               |
| **Postfix**       | Trailing content after the main body          |

Use `//#prefix+` / `//#prefix-` and `//#postfix+` / `//#postfix-` to control which section receives output:

```java
//#prefix+

import java.lang.*;

//#prefix-
public class Main {
  //#prefix+
    import java.util .*;

  //#prefix-
  public static void main(String... args) {
  }
}
```

### Special comment directives

These are not `//#` directives but are processed during the preprocessing phase:

| Directive | Description                                                          | Example                                |
|-----------|----------------------------------------------------------------------|----------------------------------------|
| `//$`     | Uncomment the rest of the line and process macros in it              | `//$ hello /*$name$*/` → `hello World` |
| `//$$`    | Uncomment the line but **do not** process macros                     | `//$$ value=/*$x$*/` → literal text    |
| `//$"""`  | Start a multiline uncomment block (needs `/B` or `allowBlocks=true`) | See tests below                        |
| `/*-*/`   | Remove the directive and join text around it                         | `hello/*-*/world` → `helloworld`       |

Example (`//$` and `//$$`):

```java
//$hello /*$111+112$*/ world       // becomes: hello 223 world
//$$hello /*$111+112$*/ world      // becomes: hello /*$111+112$*/ world
hello/*-*/world                    // becomes: helloworld
```

### Directives reference

Unless noted, directives work in the **preprocessing phase (2nd pass)** only.

#### Variables

| Directive    | Phase | Description                                                           | Example                                    |
|--------------|-------|-----------------------------------------------------------------------|--------------------------------------------|
| `//#global`  | 1st   | Set or define a global variable (including special vars)              | `//#global VERSION="1.0"`                  |
| `//#local`   | 2nd   | Define a local variable (file scope)                                  | `//#local counter=10`                      |
| `//#define`  | 2nd   | Define a global **boolean** flag (default `true`, or from expression) | `//#define DEBUG` or `//#define COUNT=3*8` |
| `//#definel` | 2nd   | Define a local **boolean** flag                                       | `//#definel FEATURE_X`                     |
| `//#undef`   | 2nd   | Remove a variable from context                                        | `//#undef DEBUG`                           |

#### Conditionals

| Directive      | Phase | Description                         | Example                            |
|----------------|-------|-------------------------------------|------------------------------------|
| `//#if`        | 2nd   | Start conditional block             | `//#if DEBUG` … `//#endif`         |
| `//#else`      | 2nd   | Else branch for `//#if`             | `//#if X` … `//#else` … `//#endif` |
| `//#endif`     | 2nd   | End `//#if` block                   |                                    |
| `//#ifdef`     | 2nd   | Short form of `//#ifdefined`        | `//#ifdef MY_FLAG`                 |
| `//#ifdefined` | 2nd   | True if variable exists             | `//#ifdefined test`                |
| `//#ifndef`    | 2nd   | True if variable does **not** exist | `//#ifndef test`                   |
| `//#_if`       | 1st   | Global conditional block            | `//#_if true` … `//#_endif`        |
| `//#_else`     | 1st   | Else for `//#_if`                   |                                    |
| `//#_endif`    | 1st   | End `//#_if` block                  |                                    |

Example:

```java
//#local FEATURE=true
//#if FEATURE
    System.out.println("Feature enabled");
//#else
    System.out.

println("Feature disabled");
//#endif
```

#### Loops and flow control

| Directive     | Phase | Description                               | Example                         |
|---------------|-------|-------------------------------------------|---------------------------------|
| `//#while`    | 2nd   | Loop while condition is true              | `//#while counter>0` … `//#end` |
| `//#end`      | 2nd   | End of `//#while` loop                    |                                 |
| `//#break`    | 2nd   | Exit the current `//#while` loop          |                                 |
| `//#continue` | 2nd   | Skip to next loop iteration               |                                 |
| `//#exit`     | 2nd   | Stop processing current file immediately  |                                 |
| `//#exitif`   | 2nd   | Stop file processing if condition is true | `//#exitif done`                |

Example loop:

```java
//#local counter=3
//#while counter>0
System.out.println("Count: /*$counter$*/");
//#local counter=counter-1
//#end
```

#### File inclusion and output control

| Directive                     | Phase | Description                                          | Example                             |
|-------------------------------|-------|------------------------------------------------------|-------------------------------------|
| `//#include`                  | 2nd   | Insert another file into current context             | `//#include "./common/Header.java"` |
| `//#outdir`                   | 2nd   | Change output folder (same as setting `jcp.dst.dir`) | `//#outdir "./generated"`           |
| `//#outname`                  | 2nd   | Change output file name (same as `jcp.dst.name`)     | `//#outname "Result.java"`          |
| `//#+`                        | 2nd   | Enable writing text to output buffers                |                                     |
| `//#-`                        | 2nd   | Disable writing text to output buffers               |                                     |
| `//#prefix+` / `//#prefix-`   | 2nd   | Enable/disable prefix buffer output                  |                                     |
| `//#postfix+` / `//#postfix-` | 2nd   | Enable/disable postfix buffer output                 |                                     |
| `//#flush`                    | 2nd   | Write buffers to file and clear them                 |                                     |
| `//#noautoflush`              | 2nd   | Disable automatic flush at end of file               |                                     |
| `//#excludeif`                | 1st   | Skip entire file if condition is true                | `//#excludeif SKIP_THIS_FILE`       |

Example — suppress output temporarily:

```java
someCode();       // always written

//#-
secretDraft();    // not written to output

//#+
moreCode();       // written again
```

#### Logging, errors, and extensions

| Directive    | Phase | Description                                    | Example                       |
|--------------|-------|------------------------------------------------|-------------------------------|
| `//#echo`    | 2nd   | Log info message (macros allowed)              | `//#echo Building /*$NAME$*/` |
| `//#msg`     | 2nd   | Log info with include stack in verbose mode    | `//#msg Processing step 2`    |
| `//#warning` | 2nd   | Log warning (macros allowed)                   | `//#warning Old API used`     |
| `//#error`   | 2nd   | Fail preprocessing with error message          | `//#error Invalid config`     |
| `//#abort`   | 2nd   | Abort preprocessing and print message          | `//#abort Fatal problem`      |
| `//#action`  | 2nd   | Call a `PreprocessorExtension` action          | `//#action arg1,arg2`         |
| `//#//`      | 2nd   | Comment out the **next** source line in output |                               |

Example:

```java
//#local TESTVAR="TEST LOCAL VARIABLE"
//#echo TESTVAR=/*$TESTVAR$*/
//#include "./fragments/Setup.java"

//#if issubstr("Hello","Hello world")
System.out.println("Substring found");
//#endif
```

---

## 4. Preprocessor Functions

Functions are called inside expressions — in directives or in `/*$...$*/` macros.

### Value types

| Type       | Examples                                               |
|------------|--------------------------------------------------------|
| **BOOL**   | `true`, `false`                                        |
| **INT**    | `2374`, `0x56FE` (signed 64-bit)                       |
| **FLOAT**  | `0.745` (32-bit)                                       |
| **STRING** | `"Hello World!"` (in CLI you may use `$Hello World!$`) |

### General-purpose functions

| Function    | Returns      | Arguments      | Description                              | Example                                  |
|-------------|--------------|----------------|------------------------------------------|------------------------------------------|
| `abs`       | INT or FLOAT | number         | Absolute value                           | `abs(-10)` → `10`                        |
| `round`     | INT          | FLOAT or INT   | Round to nearest integer                 | `round(4.7)` → `5`                       |
| `strlen`    | INT          | STRING         | String length                            | `strlen("hello")` → `5`                  |
| `trimlines` | STRING       | STRING         | Trim each line, remove empty lines       | `trimlines("  a  \n\n  b  ")`            |
| `issubstr`  | BOOL         | STRING, STRING | Case-insensitive substring check         | `issubstr("test","oneTESTtwo")` → `true` |
| `is`        | BOOL         | STRING, ANY    | Check string matches value's string form | `is("flag", true)`                       |
| `esc`       | STRING       | STRING         | Escape special characters                | `esc("a\nb")` → `a\\nb`                  |

### String conversion functions

These escape strings for use in other languages or formats:

| Function   | Returns | Arguments    | Target format       | Example                            |
|------------|---------|--------------|---------------------|------------------------------------|
| `str2int`  | INT     | STRING       | Parse integer       | `str2int("100")` → `100`           |
| `str2web`  | STRING  | STRING       | HTML                | `str2web("<tag>")` → `&lt;tag&gt;` |
| `str2csv`  | STRING  | STRING       | CSV field           | `str2csv("a,b")` → `"a,b"`         |
| `str2js`   | STRING  | STRING       | JavaScript string   | `str2js("\"hi\"")`                 |
| `str2json` | STRING  | STRING       | JSON string         | `str2json("\"hi\"")`               |
| `str2xml`  | STRING  | STRING       | XML text            | `str2xml("<a/>")`                  |
| `str2java` | STRING  | STRING, BOOL | Java string literal | `str2java("a\nb", false)`          |
| `str2go`   | STRING  | STRING, BOOL | Go string literal   | `str2go("a\nb", false)`            |

The second BOOL argument in `str2java` / `str2go` controls quoting style (double-quoted vs raw).

### File functions

| Function   | Returns | Arguments                  | Description                                   | Example                          |
|------------|---------|----------------------------|-----------------------------------------------|----------------------------------|
| `evalfile` | STRING  | STRING path                | Preprocess a file and return result as string | `evalfile("./template.txt")`     |
| `binfile`  | STRING  | STRING path, STRING format | Read binary file as encoded string            | `binfile("./data.bin","base64")` |

**`binfile` format string:** `[base64|byte[]|uint8[]|int8[]][s|d|sd|ds]`

- Type: `base64`, `byte[]`, `uint8[]`, or `int8[]`
- Optional suffix: `s` = split lines, `d` = deflate compression

Example:

```java
//#local encoded=binfile("./logo.png","base64")
String img = "/*$encoded$*/";
```

### XML functions

XML functions use **document/element IDs** returned by previous calls (not DOM objects — opaque string handles inside
JCP).

| Function       | Returns             | Arguments             | Description                                       |
|----------------|---------------------|-----------------------|---------------------------------------------------|
| `xml_open`     | STRING (doc id)     | file path             | Load and parse XML file                           |
| `xml_root`     | STRING (element id) | doc id                | Get root element                                  |
| `xml_name`     | STRING              | element id            | Element tag name                                  |
| `xml_list`     | STRING (list id)    | element id, tag name  | List child elements by tag                        |
| `xml_get`      | STRING (element id) | list id, index        | Get element from list (0-based)                   |
| `xml_text`     | STRING              | element id            | Text content of element                           |
| `xml_attr`     | STRING              | element id, attr name | Attribute value (empty if missing)                |
| `xml_size`     | INT                 | list id               | Number of elements in list                        |
| `xml_xlist`    | STRING (list id)    | doc id, XPath         | Find elements by XPath                            |
| `xml_xelement` | STRING (element id) | doc id, XPath         | Find single element by XPath (error if not found) |

Example pipeline:

```java
//#local doc=xml_open("config.xml")
//#local root=xml_root(doc)
//#local items=xml_list(root,"item")
//#local first=xml_get(items,0)
//#local title=xml_attr(first,"title")
//#echo First item title: /*$title$*/
```

### User-defined functions

Extensions can register custom functions whose names start with `$` (for example `$myfunc(...)`). Register the extension
class via `/EA:`, Maven `actionPreprocessorExtensions`, or Gradle `actionPreprocessorExtensions`.

---

## 5. Appendix: Special Variables and Expression Syntax

### Macro syntax

Insert an expression result into output text:

```
/*$expression$*/
```

Examples:

```java
System.out.println("File: /*$jcp.src.name$*/");
System.out.

println("2 + 2 = /*$2+2$*/");
```

### Special variables

| Variable                         | Access      | Description                         |
|----------------------------------|-------------|-------------------------------------|
| `jcp.version`                    | read-only   | Preprocessor version string         |
| `jcp.src.path` / `__file__`      | read-only   | Full path of current source file    |
| `jcp.src.dir` / `__filefolder__` | read-only   | Source file directory               |
| `jcp.src.name` / `__filename__`  | read-only   | Source file name                    |
| `jcp.dst.path`                   | read-only   | Full path of destination file       |
| `jcp.dst.dir`                    | read-write  | Destination folder                  |
| `jcp.dst.name`                   | read-write  | Destination file name               |
| `__line__`                       | read-only   | Current line number                 |
| `__date__`                       | read-only   | Current date (`MMM dd yyyy`)        |
| `__time__`                       | read-only   | Current time (`HH:mm:ss`)           |
| `__timestamp__`                  | read-only   | Source file last-modified timestamp |
| `jcp.text.buffer.all`            | read-write* | Entire current text buffer          |
| `jcp.text.buffer.middle`         | read-write* | Middle (body) buffer                |
| `jcp.text.buffer.prefix`         | read-write* | Prefix buffer                       |
| `jcp.text.buffer.postfix`        | read-write* | Postfix buffer                      |

\* Buffer variables cannot be changed during the **global phase**.

In Maven projects, additional variables from project properties are available through the Maven integration (for example
`${project.version}` mapped into preprocessor context).

> **Note:** Older documentation may refer to `SRV_CUR_FILE` or `SRV_OUT_DIR`. Current versions use `jcp.src.name` and
> `jcp.dst.dir` instead.

### Expression operators

Expressions support arithmetic (`+`, `-`, `*`, `/`, `%`), comparisons (`==`, `!=`, `<`, `>`, `<=`, `>=`), logical
operators (`&&`, `||`, `!`), string concatenation (`+` on strings), and parentheses. Run `java -jar jcp.jar /H` for the
full operator list.

### Complete example

```java
//#local TESTVAR="TEST LOCAL VARIABLE"
//#echo TESTVAR=/*$TESTVAR$*/
//#include "./fragments/MainProcedure.java"

public static void testproc() {
  System.out.println(/*$VARHELLO$*/);
  //#local counter=10
  //#while counter!=0
  System.out.println("Number /*$counter$*/");
  //#local counter=counter-1
  //#end
  System.out.println("Current file: /*$jcp.src.name$*/");
  System.out.println("Output dir: /*$jcp.dst.dir$*/");
  //#if issubstr("Hello","Hello world")
  System.out.println("Substring found");
  //#endif
}
```

---

## Further Reading

- Project repository: [github.com/raydac/java-comment-preprocessor](https://github.com/raydac/java-comment-preprocessor)
- Maven Central artifact: [com.igormaznitsa:jcp](https://search.maven.org/artifact/com.igormaznitsa/jcp)
- Example projects: `jcp-tests/jcp-test-maven`, `jcp-tests/jcp-test-gradle-*`, `jcp-tests/jcp-test-jep238`,
  `jcp-tests/jcp-test-android`
- Options mind map: [assets/documap.png](documap.png)
