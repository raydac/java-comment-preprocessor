# Java Comment Preprocessor — User Manual

|                          |                                        |
|--------------------------|----------------------------------------|
| **Preprocessor version** | 7.3.1-SNAPSHOT (latest release: 7.3.0) |
| **Document version**     | 1.3                                    |
| **Document date**        | 2026-07-21                             |

---

## Table of Contents

1. [What Is JCP and Why Use It in a Java Project](#1-what-is-jcp-and-why-use-it-in-a-java-project)
2. [How to Connect JCP to Your Project](#2-how-to-connect-jcp-to-your-project)
    - [2.1 Command Line (CLI)](#21-command-line-cli)
    - [2.2 Maven Projects](#22-maven-projects)
    - [2.3 Gradle Projects](#23-gradle-projects)
   - [2.4 Apache Ant Projects](#24-apache-ant-projects)
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

### 2.4 Apache Ant Projects

JCP includes an **Ant task** named **`preprocess`**, implemented by `com.igormaznitsa.jcp.ant.PreprocessTask`. Minimum
Ant version: **1.8.2+**.

The task is bundled inside the same `jcp` JAR as the CLI, Maven plugin, and Gradle plugin.

#### Register the task

Put the JCP JAR on the Ant classpath, then load the task definition from the JAR resource file:

```xml

<project name="myproject" default="build" basedir=".">

   <!-- Load preprocess task from jcp.jar -->
   <taskdef resource="com/igormaznitsa/jcp/ant/tasks.properties">
      <classpath>
         <pathelement location="lib/jcp-7.3.0.jar"/>
      </classpath>
   </taskdef>

   <!-- your targets here -->

</project>
```

Alternative — define the task class explicitly:

```xml

<taskdef name="preprocess" classname="com.igormaznitsa.jcp.ant.PreprocessTask">
   <classpath>
      <pathelement location="lib/jcp-7.3.0.jar"/>
   </classpath>
</taskdef>
```

If you use **maven-antrun-plugin**, add `jcp` as a plugin dependency (see example project `jcp-tests/jcp-test-ant`) so
the task is available without a manual classpath entry.

#### Minimal preprocessing target

```xml

<target name="preprocess">
   <preprocess target="build/preprocessed">
      <sources>
         <path>src</path>
      </sources>
      <extensions>
         <extension>java</extension>
      </extensions>
   </preprocess>
</target>
```

Preprocessed files are written to the folder set in the `target` attribute, preserving the relative directory structure
from each source root.

#### Full example (from JCP integration tests)

```xml

<project name="myproject" default="preprocess" basedir=".">

   <taskdef resource="com/igormaznitsa/jcp/ant/tasks.properties">
      <classpath>
         <pathelement location="lib/jcp-7.3.0.jar"/>
      </classpath>
   </taskdef>

   <target name="preprocess">
      <preprocess
              target="build/preprocessed"
              verbose="true"
              clearTarget="true"
              keepComments="REMOVE_C_STYLE"
              keepLines="true"
              allowWhitespaces="true"
              allowBlocks="true"
              eol="\r\n"
              sourceEncoding="UTF-8"
              targetEncoding="UTF-8"
              actionPreprocessorExtensions="com.igormaznitsa.jcp.extension.LogPreprocessorExtension">
         <sources>
            <path>src</path>
         </sources>
         <extensions>
            <extension>java</extension>
         </extensions>
         <excludeExtensions>
            <extension>xml</extension>
         </excludeExtensions>
         <excludeFolders>
            <folder>**/generated/**</folder>
         </excludeFolders>
         <configFiles>
            <path>config/preprocessor.cfg</path>
         </configFiles>
         <vars>
            <var name="feature.debug">true</var>
         </vars>
      </preprocess>
   </target>

</project>
```

#### Chain preprocessing with compilation

Like Gradle, Ant does **not** switch source folders automatically. Point `javac` (or another tool) at the preprocessed
output:

```xml

<target name="compile" depends="preprocess">
   <mkdir dir="build/classes"/>
   <javac srcdir="build/preprocessed"
          destdir="build/classes"
          includeantruntime="false"
          source="11"
          target="11"/>
</target>
```

#### Reading Ant properties from preprocessor sources

The Ant task registers all Ant project properties as preprocessor variables with the **`ant.`** prefix (property names
are lower-cased).

If your `build.xml` defines:

```xml

<property name="app.version" value="2.1"/>
```

You can use it in source as:

```java
public static final String VERSION = "/*$ant.app.version$*/";
```

Another example from the test project — the Ant built-in property `ant.project.name` is available as:

```java
return"/*$ant.ant.project.name$*/";
```

Ant properties are **read-only** inside the preprocessor. You cannot change them with `//#global` or `//#local`.

#### Ant task attributes and nested elements

**Top-level attributes** (same meaning as Maven/Gradle where noted):

| Attribute                      | Default          | Description                                                                      |
|--------------------------------|------------------|----------------------------------------------------------------------------------|
| `target`                       | —                | **Required.** Output folder for preprocessed files                               |
| `verbose`                      | `false`          | Verbose logging                                                                  |
| `clearTarget`                  | `false`          | Clear output folder before run                                                   |
| `keepComments`                 | `REMOVE_C_STYLE` | Comment mode: `KEEP_ALL`, `REMOVE_C_STYLE`, `REMOVE_JCP_ONLY`, or `true`/`false` |
| `keepLines`                    | `true`           | Keep directive lines as comments in output                                       |
| `sourceEncoding`               | platform default | Input file encoding                                                              |
| `targetEncoding`               | platform default | Output file encoding                                                             |
| `eol`                          | —                | End-of-line string (Java escape sequences allowed, e.g. `\r\n`)                  |
| `extensions`                   | —                | Nested `<extension>` elements — file types to preprocess                         |
| `excludeExtensions`            | —                | Nested `<extension>` elements — file types to skip                               |
| `excludeFolders`               | —                | Nested `<folder>` elements — ANT-style path patterns to exclude                  |
| `sources`                      | —                | Nested `<path>` elements — source root folders                                   |
| `configFiles`                  | —                | Nested `<path>` elements — external config files with variables/CLI options      |
| `vars`                         | —                | Nested `<var name="…">value</var>` — global preprocessor variables               |
| `unknownVarAsFalse`            | `false`          | Treat unknown variables as `false`                                               |
| `dontOverwriteSameContent`     | `false`          | Skip writing unchanged files                                                     |
| `allowWhitespaces`             | `false`          | Allow spaces between `//` and `#` in directives                                  |
| `preserveIndents`              | `false`          | Preserve indentation for `//$` / `//$$`                                          |
| `allowBlocks`                  | `false`          | Enable multiline `//$"""` blocks                                                 |
| `careForLastEol`               | `false`          | Preserve trailing end-of-line                                                    |
| `keepAttributes`               | `false`          | Copy file attributes to output files                                             |
| `dryRun`                       | `false`          | Process without writing files                                                    |
| `ignoreMissingSources`         | `false`          | Ignore missing source folders                                                    |
| `actionPreprocessorExtensions` | —                | Comma-separated list of `PreprocessorExtension` class names                      |

**Nested element structure:**

```xml

<preprocess target="build/preprocessed">
   <sources>
      <path>src/main</path>
      <path>src/extra</path>
   </sources>
   <extensions>
      <extension>java</extension>
   </extensions>
   <excludeExtensions>
      <extension>xml</extension>
   </excludeExtensions>
   <excludeFolders>
      <folder>**/test/**</folder>
   </excludeFolders>
   <configFiles>
      <path>preprocessor.cfg</path>
   </configFiles>
   <vars>
      <var name="build.type">release</var>
   </vars>
</preprocess>
```

Directive messages (`//#echo`, `//#warning`, `//#error`) are written to the **Ant build log** at the appropriate level.

#### Config file example

A config file referenced by `<configFiles>` can define variables and CLI-style options:

```
# preprocessor.cfg
cfg.test="hellocfg"
build.mode=release
```

Lines starting with `#` are comments. Other lines define global variables; lines starting with `/` are treated as CLI
switches.

---

## 3. Preprocessor Directives and Processing Phases

### How directives look in Java source

JCP directives are written as **single-line comments** that start with `//#`. This keeps source files valid Java — the
compiler and IDE ignore them until preprocessing runs.

```java
//#local DEBUG=true
//#if DEBUG
System.out.println("Debug mode");
//#endif
```

**Macros** substitute expression results into the output text. Wrap an expression in `/*$` and `$*/`:

```java
System.out.println("Version: /*$jcp.version$*/");
System.out.

println("Line: /*$__line__$*/");
System.out.

println("Sum: /*$2+2$*/");   // prints: Sum: 4
```

#### Basic rules

| Rule                   | Detail                                                                                                                    |
|------------------------|---------------------------------------------------------------------------------------------------------------------------|
| **Case**               | Variable names are case-insensitive (`DEBUG` = `debug`).                                                                  |
| **Whitespace**         | By default there must be no space between `//` and `#`. Enable `/ES` or `allowWhitespaces=true` for `// #if` style.       |
| **Expressions**        | Used in directives (`//#if x>0`) and macros (`/*$x+1$*/`). Support arithmetic, logic, functions, and strings.             |
| **Strings**            | Use double quotes: `"Hello"`. In CLI, `$Hello$` can be used instead of quotes.                                            |
| **Comments in output** | With `keepLines=true` (Maven/Gradle default), unused directive lines stay as `//JCP! ...` comments so line numbers match. |

---

### Processing phases

JCP processes each file in **two passes**. Some directives only work in one of them.

```
┌─────────────────────────────────────────────────────────────┐
│  Phase 1 — Global phase (1st pass)                          │
│  Runs once per file before main transformation.             │
│  Sets global variables and decides whether to skip a file.    │
│  Active: //#global, //#_if, //#_else, //#_endif,            │
│          //#excludeif                                       │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│  Phase 2 — Preprocessing phase (2nd pass)                   │
│  Full transformation: conditionals, loops, includes,        │
│  macros, output buffers. Most //#directives work here.       │
└─────────────────────────────────────────────────────────────┘
```

**Full run order** (all files in the project):

1. Scan source folders and build the file list.
2. Run the **global phase** on every file.
3. Evaluate all `//#excludeif` conditions — matching files are dropped.
4. Create or clear the target folder.
5. Run the **preprocessing phase** on remaining files (or copy unchanged files).

#### Why two phases matter

Use the global phase when a decision must happen **before** the file body is processed — for example, setting a variable
that other files depend on, or skipping a file entirely.

**Example — global variable in phase 1, used in phase 2:**

Source (`Config.java`):

```java
//#global BUILD_TYPE="release"
public class Config {
   //#if BUILD_TYPE=="release"
   public static final boolean DEBUG = false;
   //#else
   public static final boolean DEBUG = true;
   //#endif
}
```

The `//#global` line runs in pass 1 and defines `BUILD_TYPE` for the whole project context. The `//#if` block runs in
pass 2 and writes the matching field into the output.

**Example — skip a file with `//#excludeif`:**

```java
//#excludeif PLATFORM!="android"
// This entire file is preprocessed only when PLATFORM equals "android"

package com.example.android;
// ...
```

If `PLATFORM` is not `"android"`, the file is not written to the output folder at all.

---

### Prefix, middle, and postfix — the three output buffers

This is one of the most important concepts in JCP. Understanding it unlocks the main reason to use the preprocessor with
Java sources.

#### What prefix and postfix actually mean

JCP does **not** parse Java syntax and does **not** automatically detect `package`, `import`, or `class` boundaries.
Instead, while a file is processed line by line, every piece of output text is written into one of **three independent
text buffers**:

| Buffer      | Name in code | Special variable          | Role                                                             |
|-------------|--------------|---------------------------|------------------------------------------------------------------|
| **Prefix**  | `PREFIX`     | `jcp.text.buffer.prefix`  | Content that must appear at the **beginning** of the output file |
| **Middle**  | `NORMAL`     | `jcp.text.buffer.middle`  | Main body — the default target for all output                    |
| **Postfix** | `POSTFIX`    | `jcp.text.buffer.postfix` | Content that must appear at the **end** of the output file       |

When preprocessing finishes (or when `//#flush` runs), the result file is assembled by **concatenating the three buffers
in fixed order**:

```
final file = prefix buffer  +  middle buffer  +  postfix buffer
```

Nothing else determines the order — not the line number in the source where text was written, and not the order in which
you switched buffers.

#### Visual model

While you read and process the source **top to bottom**, imagine three buckets collecting output:

```
  Source file (read sequentially)
  ─────────────────────────────────────────────────────────────
  line 1  ──►  [ PREFIX bucket  ]     ──┐
  line 5  ──►  [ MIDDLE bucket  ]     ──┼──►  prefix + middle + postfix  ──►  output file
  line 9  ──►  [ POSTFIX bucket ]     ──┘
  line 12 ──►  [ MIDDLE bucket  ]     ──┘   (back to middle)
```

Text written while the prefix buffer is active may be triggered deep inside a class body, but it still lands at the
**top** of the generated file. That is the whole point.

#### Default behaviour

At the start of each file, the active buffer is **middle**. Every source line that produces output goes into the middle
buffer unless you explicitly switch with `//#prefix+` or `//#postfix+`.

There is **no automatic routing** — if you write `import java.util.List;` at the top of your source without
`//#prefix+`, it stays in the middle buffer exactly where it appears in the source.

#### Routing directives

| Directive     | Effect                                                 |
|---------------|--------------------------------------------------------|
| `//#prefix+`  | Send following output to the **prefix** buffer         |
| `//#prefix-`  | Stop prefix mode; return output routing to **middle**  |
| `//#postfix+` | Send following output to the **postfix** buffer        |
| `//#postfix-` | Stop postfix mode; return output routing to **middle** |

Important details:

- **`//#prefix-` and `//#postfix-` both return to middle**, not to the other section. There is no direct
  `prefix → postfix` switch; go back to middle first.
- These directives only change **where new text is stored**. They do not move text already written.
- They work only in the **preprocessing phase** (2nd pass), not in the global phase.
- Directives inside an inactive `//#if false` block are **not executed** — a `//#prefix+` there has no effect.

#### Do not confuse with `//#-` and `//#+`

These are **different** mechanisms:

| Directive                    | What it controls                                                                 |
|------------------------------|----------------------------------------------------------------------------------|
| `//#prefix+` / `//#postfix+` | **Which buffer** receives output (prefix, middle, or postfix)                    |
| `//#-` / `//#+`              | **Whether any output is written at all** to whichever buffer is currently active |

`//#-` sets a global "output muted" flag. While muted, no text reaches any buffer. `//#+` turns output back on. The
active buffer selection (`prefix` / `middle` / `postfix`) is remembered across mute/unmute.

#### Step-by-step example (from JCP tests)

**Source** (processing order is top to bottom):

```
//#postfix+
This string must be in the postfix
//#postfix-
This string must be in the normal (1)
//#prefix+
This string must be in the prefix
//#prefix-
This string must be in the normal (2)
//#if false
//#postfix+
//#endif
This string must be in the normal (3)
ok
```

**What happens:**

1. `//#postfix+` — routing switches to postfix.
2. `"This string must be in the postfix"` — stored in **postfix** bucket.
3. `//#postfix-` — routing back to **middle**.
4. `"This string must be in the normal (1)"` — stored in **middle**.
5. `//#prefix+` — routing switches to prefix.
6. `"This string must be in the prefix"` — stored in **prefix** bucket (will appear first in output).
7. `//#prefix-` — routing back to **middle**.
8. Remaining lines — stored in **middle**. The `//#postfix+` inside `//#if false` is ignored.

**Output file** (prefix + middle + postfix):

```
This string must be in the prefix
This string must be in the normal (1)
This string must be in the normal (2)
This string must be in the normal (3)
ok
This string must be in the postfix
```

Notice: the postfix line was written **first** in the source, but appears **last** in the output. The prefix line was
written **in the middle** of the source, but appears **first** in the output.

#### Why this matters for Java

In Java, `import` statements must appear before the class body. That creates a problem for preprocessors: you often want
to **decide imports conditionally** or **inject imports from a template** while writing code inside a class or a shared
include file.

With prefix buffers you can write this inside a method or class:

```java
public class App {
   //#if USE_LOGGING
   //#prefix+
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
   //#prefix-
   //#endif

   private static final Logger log = LoggerFactory.getLogger(App.class);
}
```

**After preprocessing**, the imports appear at the top of the file:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
   private static final Logger log = LoggerFactory.getLogger(App.class);
}
```

The same technique works with `//#include` — an included fragment can emit imports into the prefix buffer of the host
file.

#### Typical section usage in Java

| Section     | Put here                                 | Common examples                                                         |
|-------------|------------------------------------------|-------------------------------------------------------------------------|
| **Prefix**  | Everything that must be at the file top  | `package`, `import`, file-level annotations, license headers            |
| **Middle**  | The main program text                    | Classes, interfaces, enums, methods, fields                             |
| **Postfix** | Everything that must come after the body | Closing HTML tags in templates, trailing script blocks, footer comments |

For plain `.java` files postfix is used less often than prefix, but it is essential for HTML/text templates and for
generated documents with headers and footers.

#### Complete Java example

**Source:**

```java
package com.example;

//#prefix+

import java.util.List;
import java.util.ArrayList;
//#prefix-

public class Main {
   //#if GENERATE_EXTRA_IMPORT
   //#prefix+
    import java.util.Map;
   //#prefix-
   //#endif

   public static void main(String[] args) {
      List<String> items = new ArrayList<>();
   }
}

//#postfix+
// Generated by JCP on /*$__date__$*/
//#postfix-
```

**Output:**

```java
package com.example;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Main {
   public static void main(String[] args) {
      List<String> items = new ArrayList<>();
   }
}
// Generated by JCP on Jul 21 2026
```

(With `GENERATE_EXTRA_IMPORT` set to `true`.)

#### Reading and writing buffers from expressions

You can inspect or replace buffer contents through special variables:

| Variable                  | Read                             | Write |
|---------------------------|----------------------------------|-------|
| `jcp.text.buffer.prefix`  | yes                              | yes*  |
| `jcp.text.buffer.middle`  | yes                              | yes*  |
| `jcp.text.buffer.postfix` | yes                              | yes*  |
| `jcp.text.buffer.all`     | yes (concatenation of all three) | —     |

\* Writing buffer variables is **not allowed during the global phase** (1st pass).

Example — log how large the prefix buffer has grown:

```java
//#echo Prefix buffer length: /*$strlen(jcp.text.buffer.prefix)$*/
```

Replacing a buffer overwrites its entire content. Use this carefully — it is mainly for advanced extension scenarios.

#### Prefix/postfix with `//#flush`

`//#flush` writes the current state of **all three buffers** (joined as prefix + middle + postfix) to the destination
file and then **clears all buffers**. Use it when generating a file incrementally in stages. With `//#noautoflush`,
automatic writing at end-of-file is disabled and you must flush manually.

#### Summary checklist

- [ ] Prefix/postfix are **output routing buffers**, not Java language sections detected by the parser.
- [ ] Default output goes to **middle**; use `//#prefix+` / `//#postfix+` to redirect.
- [ ] Final file order is always **prefix → middle → postfix**, regardless of source line order.
- [ ] `//#prefix-` and `//#postfix-` both return to **middle**.
- [ ] `//#-` / `//#+` mute all output; they do not select a buffer.
- [ ] Inactive `//#if` blocks ignore buffer switches inside them.

---

### Special comment directives

These directives do not use the `//#` prefix. They work during the preprocessing phase.

| Directive | What it does                                                    |
|-----------|-----------------------------------------------------------------|
| `//$`     | Uncomment the rest of the line and evaluate macros              |
| `//$$`    | Uncomment the line but leave macros unchanged                   |
| `//$"""`  | Start a multiline uncomment block (requires `allowBlocks=true`) |
| `//$$"""` | Same as above, but macros are not evaluated                     |
| `/*-*/`   | Delete itself and join surrounding text                         |

**Single-line uncomment:**

```java
//$hello /*$111+112$*/ world
//$$hello /*$111+112$*/ world
hello/*-*/world
```

**Output:**

```java
hello 223world
hello /*$111+112$*/ world
helloworld
```

**Multiline blocks** (enable with `/B` or `allowBlocks=true`):

```java
//$"""hello /*$111+112$*/ world
next line
here
```

**Output:**

```java
hello 223world
next line
here
```

Use `//$"""` when you need to uncomment several lines of code or text at once. A line containing only `//$split` or
`//$$split` ends the current block early.

---

### Variables

Variables hold values used in expressions. There are three scopes:

| Scope            | Directives                       | Lifetime                                                                      |
|------------------|----------------------------------|-------------------------------------------------------------------------------|
| **Global**       | `//#global`, `//#define`         | Whole preprocessing run (all files)                                           |
| **Local (file)** | `//#local`, `//#definel`         | Current file only                                                             |
| **Special**      | `jcp.src.name`, `__line__`, etc. | Built-in, see [Appendix](#5-appendix-special-variables-and-expression-syntax) |

#### `//#global` — set a global variable (phase 1)

```java
//#global MAX_RETRIES=3
//#global APP_NAME="MyApp"
```

Runs in the **global phase**. Use it to pass settings from one file to others in the same run.

#### `//#local` — set a file-local variable (phase 2)

**Source:**

```java
//#local menuId = "hello"
//#local menuName = "menu"
/*$menuId+menuName$*/
//#local x = 5
//#local y = 10
//#local result = x + y + "." + x + "." + y
/*$result$*/
```

**Output:**

```
hellomenu
15.5.10
```

Variables can be reassigned: `//#local counter=counter-1` inside a loop updates the value each iteration.

#### `//#define` and `//#definel` — boolean flags

`define` creates a **global** boolean; `definel` creates a **local** boolean.

**Source:**

```java
//#define FEATURE_X          // same as //#define FEATURE_X=true
//#define MAX_COUNT=3*8      // boolean from expression (non-zero = true)
/*$feature_x$*/ /*$max_count$*/
//#definel LOCAL_FLAG
/*$local_flag$*/
//#undef FEATURE_X
//#undef LOCAL_FLAG
```

**Output:**

```
true true
true true
```

After `//#undef`, the variable no longer exists — `//#ifdef` will treat it as undefined.

---

### Conditionals

Conditional blocks use `//#if` … `//#else` … `//#endif`. Blocks can be nested.

#### `//#if` / `//#else` / `//#endif`

**Source:**

```java
//#local featureEnabled = true
//#if featureEnabled
    System.out.println("Feature is ON");
//#else
    System.out.

println("Feature is OFF");
//#endif
```

Any expression that evaluates to a boolean can be used: `//#if count>0`, `//#if name=="admin"`, `//#if !debug`.

#### Nested conditionals

**Source:**

```java
hello
//#local x=true
//#local y=false
//#if x
        //#if y
        unexpected
//#else
//#if !y
expected
        //#endif
        //#endif
//#endif
        world
```

**Output:**

```
hello
expected
world
```

When `//#if false`, everything until the matching `//#endif` is skipped — including nested text and macro expansion.

#### `//#ifdef`, `//#ifdefined`, `//#ifndef`

These check whether a **variable name exists**, not its value.

**Source:**

```java
//#ifdef TEST_FLAG
unexpected —
TEST_FLAG not
defined yet
//#else
expected1
//#endif

//#define TEST_FLAG
//#if true
        //#ifdef TEST_FLAG
        expected2
//#endif
//#endif
```

**Output:**

```
expected1
expected2
```

| Directive                             | True when                          |
|---------------------------------------|------------------------------------|
| `//#ifdef NAME` / `//#ifdefined NAME` | Variable `NAME` exists             |
| `//#ifndef NAME`                      | Variable `NAME` does **not** exist |

#### Global conditionals — `//#_if` / `//#_else` / `//#_endif` (phase 1)

Global conditionals run in the **first pass** only. Use them together with `//#global`:

**Source:**

```java
//#_if true
//#global expected=true
//#_else
//#global expected=false
//#_endif
//#if expected
configured
//#endif
```

Only the `//#_if true` branch runs during the global phase, so `expected` is set to `true` before the second pass
begins.

---

### Loops

#### `//#while` … `//#end`

The loop condition is checked **before** each iteration. Use `//#local` inside the loop to update counters.

**Source — generate repeated lines:**

```java
//#local counter = 3
//#while counter > 0
System.out.println("Count: /*$counter$*/");
//#local counter = counter - 1
//#end
```

**Output:**

```java
System.out.println("Count: 3");
System.out.

println("Count: 2");
System.out.

println("Count: 1");
```

**Source — nested loops with `//#break` and `//#continue`:**

```java
//#local counter = 3
//#while counter > 0
//#if counter == 2
//#local counter = counter - 1
//#continue          // skip printing when counter was 2
//#endif
value=/*$counter$*/
//#local counter = counter - 1
//#end
```

When `counter` equals 2, `//#continue` jumps to the next loop iteration without printing.

| Directive     | Effect                                                     |
|---------------|------------------------------------------------------------|
| `//#break`    | Exit the innermost `//#while` loop immediately             |
| `//#continue` | Skip rest of current iteration, re-check condition         |
| `//#end`      | Marks the end of a `//#while` loop (not used with `//#if`) |

---

### File inclusion

#### `//#include`

Inserts the content of another file at the current position. The included file is processed with the same variable
context.

**Source (`Main.java`):**

```java
start
//#include "./fragments/Header.java"
//#include __filefolder__ + "/fragments/Footer.java"
        end
```

**`Header.java`:**

```java
// Generated header content
```

**Output:**

```
start
// Generated header content
// Footer content
end
```

Tips:

- Paths can be string expressions: `__filefolder__` is the directory of the current source file.
- Included files can contain their own directives, including further `//#include` calls.
- Use `//#exit` inside an included file to stop processing that file and return to the caller.

---

### Output control

Output control directives decide **whether** text is written and **where** it goes.
See [Prefix, middle, and postfix](#prefix-middle-and-postfix--the-three-output-buffers) for the full model.

| Mechanism             | Directives                                               | Controls                                 |
|-----------------------|----------------------------------------------------------|------------------------------------------|
| **Buffer routing**    | `//#prefix+`, `//#prefix-`, `//#postfix+`, `//#postfix-` | Which of the three buffers receives text |
| **Output mute**       | `//#-`, `//#+`                                           | Whether any text is written at all       |
| **Output location**   | `//#outdir`, `//#outname`                                | Destination folder and file name on disk |
| **Incremental write** | `//#flush`, `//#noautoflush`                             | When buffer contents are saved to disk   |

#### `//#prefix+` / `//#postfix+` — route text to another section

See the dedicated section above for examples. In short: use `//#prefix+` to collect imports and headers, `//#postfix+`
to collect footers — both without changing where those directives appear in your source.

#### `//#-` and `//#+` — pause and resume all output

**Source:**

```java
hello world
//#-
this
text is
suppressed
//#if true
also suppressed
//#endif
//#+
visible again
```

**Output:**

```
hello world
visible again
```

While output is disabled (`//#-`), no text reaches **any** buffer (prefix, middle, or postfix). The currently selected
buffer is remembered — when you `//#+` again, routing continues to the same buffer as before.

#### `//#outdir` and `//#outname` — change output location

**Source:**

```java
//#local path = "generated/"
//#outdir path + "classes"
//#outname "Result.java"
// file will be written to generated/classes/Result.java
```

These are equivalent to setting the special variables `jcp.dst.dir` and `jcp.dst.name`.

#### `//#flush` and `//#noautoflush`

- `//#flush` — write all buffers to disk immediately and clear them.
- `//#noautoflush` — prevent automatic write at end of file; you must call `//#flush` manually.

Useful when generating very large files or writing incremental output.

---

### Flow control

| Directive             | Effect                                                                    |
|-----------------------|---------------------------------------------------------------------------|
| `//#exit`             | Stop processing the current file; return to caller if inside `//#include` |
| `//#exitif condition` | Same as `//#exit`, but only when `condition` is true                      |

**Source:**

```java
part one
//#exit
part two —
never appears
in output
```

**Output:**

```
part one
```

---

### Logging, errors, and extensions

| Directive         | Effect                                       | Stops build?         |
|-------------------|----------------------------------------------|----------------------|
| `//#echo expr`    | Print info message to log                    | No                   |
| `//#msg expr`     | Print info with file/include stack (verbose) | No                   |
| `//#warning expr` | Print warning                                | No                   |
| `//#error expr`   | Report error                                 | **Yes**              |
| `//#abort expr`   | Abort entire preprocessing run               | **Yes**              |
| `//#action args…` | Call a registered `PreprocessorExtension`    | Depends on extension |

**Example:**

```java
//#local version = "2.1"
//#echo Building version /*$version$*/
//#warning Deprecated API in /*$jcp.src.name$*/
//#if version == ""
//#error Version must not be empty
//#endif
```

Messages support full expressions: `//#echo "Count="+count`.

---

### `//#//` — comment out the next line

Forces the **next source line** to appear as a comment in the output.

**Source:**

```java
begin
//#//
        hello
//#//
test
```

**Output:**

```java
begin
//     hello
//
        test
```

Useful when you want to keep code in the source file but ensure it never compiles in the output.

---

### Practical recipes

#### Feature flag for debug logging

Pass a flag from Maven/Gradle (`vars`) or CLI (`/P:DEBUG=true`):

```java
//#if DEBUG
System.out.println("Debug: starting /*$jcp.src.name$*/");
//#endif
```

#### Platform-specific source file

At the top of a file that should only be generated for one platform:

```java
//#excludeif TARGET_PLATFORM != "android"

package com.example.app;
// ... rest of Android-specific code
```

Set `TARGET_PLATFORM` from the build: `<vars><TARGET_PLATFORM>android</TARGET_PLATFORM></vars>`.

#### Generate enum values from a loop

```java
public enum Size {
   //#local i = 0
//#while i < 5
   SIZE_/*$i$*/(/*$i$*/),
//#local i = i + 1
//#end
   ;
   private final int value;

   Size(int value) {
      this.value = value;
   }
}
```

#### Include shared boilerplate

```java
package com.example;

//#include "./templates/LicenseHeader.java"
//#include "./templates/Imports.java"

public class App {
   // class body
}
```

---

### Directives quick reference

Unless noted, directives work in the **preprocessing phase (2nd pass)** only.

#### Variables

| Directive    | Phase | Description                                                       |
|--------------|-------|-------------------------------------------------------------------|
| `//#global`  | 1st   | Set or define a global variable (including special vars)          |
| `//#local`   | 2nd   | Define a local variable (file scope)                              |
| `//#define`  | 2nd   | Define a global boolean flag (default `true`, or from expression) |
| `//#definel` | 2nd   | Define a local boolean flag                                       |
| `//#undef`   | 2nd   | Remove a variable from context                                    |

#### Conditionals

| Directive      | Phase | Description                         |
|----------------|-------|-------------------------------------|
| `//#if`        | 2nd   | Start conditional block             |
| `//#else`      | 2nd   | Else branch for `//#if`             |
| `//#endif`     | 2nd   | End `//#if` block                   |
| `//#ifdef`     | 2nd   | Short form of `//#ifdefined`        |
| `//#ifdefined` | 2nd   | True if variable exists             |
| `//#ifndef`    | 2nd   | True if variable does **not** exist |
| `//#_if`       | 1st   | Global conditional block            |
| `//#_else`     | 1st   | Else for `//#_if`                   |
| `//#_endif`    | 1st   | End `//#_if` block                  |

#### Loops and flow control

| Directive     | Phase | Description                               |
|---------------|-------|-------------------------------------------|
| `//#while`    | 2nd   | Loop while condition is true              |
| `//#end`      | 2nd   | End of `//#while` loop                    |
| `//#break`    | 2nd   | Exit the current `//#while` loop          |
| `//#continue` | 2nd   | Skip to next loop iteration               |
| `//#exit`     | 2nd   | Stop processing current file immediately  |
| `//#exitif`   | 2nd   | Stop file processing if condition is true |

#### File inclusion and output control

| Directive                     | Phase | Description                                      |
|-------------------------------|-------|--------------------------------------------------|
| `//#include`                  | 2nd   | Insert another file into current context         |
| `//#outdir`                   | 2nd   | Change output folder (same as `jcp.dst.dir`)     |
| `//#outname`                  | 2nd   | Change output file name (same as `jcp.dst.name`) |
| `//#+`                        | 2nd   | Enable writing text to output buffers            |
| `//#-`                        | 2nd   | Disable writing text to output buffers           |
| `//#prefix+` / `//#prefix-`   | 2nd   | Enable/disable prefix buffer output              |
| `//#postfix+` / `//#postfix-` | 2nd   | Enable/disable postfix buffer output             |
| `//#flush`                    | 2nd   | Write buffers to file and clear them             |
| `//#noautoflush`              | 2nd   | Disable automatic flush at end of file           |
| `//#excludeif`                | 1st   | Skip entire file if condition is true            |

#### Logging, errors, and extensions

| Directive    | Phase | Description                                    |
|--------------|-------|------------------------------------------------|
| `//#echo`    | 2nd   | Log info message (macros allowed)              |
| `//#msg`     | 2nd   | Log info with include stack in verbose mode    |
| `//#warning` | 2nd   | Log warning (macros allowed)                   |
| `//#error`   | 2nd   | Fail preprocessing with error message          |
| `//#abort`   | 2nd   | Abort preprocessing and print message          |
| `//#action`  | 2nd   | Call a `PreprocessorExtension` action          |
| `//#//`      | 2nd   | Comment out the **next** source line in output |

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
- Example projects: `jcp-tests/jcp-test-maven`, `jcp-tests/jcp-test-gradle-*`, `jcp-tests/jcp-test-ant`,
  `jcp-tests/jcp-test-jep238`, `jcp-tests/jcp-test-android`
- Options mind map: [assets/documap.png](documap.png)
