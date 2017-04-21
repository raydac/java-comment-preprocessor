[![License Apache 2.0](https://img.shields.io/badge/license-Apache%20License%202.0-green.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/com.igormaznitsa/jcp/badge.svg)](http://search.maven.org/#artifactdetails|com.igormaznitsa|jcp|6.1.2|jar)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/c6acda63097a40c68d8ca8eaef6180d8)](https://www.codacy.com/app/rrg4400/java-comment-preprocessor)
[![Java 6.0+](https://img.shields.io/badge/java-6.0%2b-green.svg)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![Maven 3.0+](https://img.shields.io/badge/maven-3.0%2b-green.svg)](https://maven.apache.org/)
[![Ant 1.8.2+](https://img.shields.io/badge/ant-1.8.2%2b-green.svg)](http://ant.apache.org/)
[![PayPal donation](https://img.shields.io/badge/donation-PayPal-red.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=AHWJHJFBAWGL2)
[![Yandex.Money donation](https://img.shields.io/badge/donation-Я.деньги-yellow.svg)](https://money.yandex.ru/embed/small.xml?account=41001158080699&quickpay=small&yamoney-payment-type=on&button-text=01&button-size=l&button-color=orange&targets=%D0%9F%D0%BE%D0%B6%D0%B5%D1%80%D1%82%D0%B2%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5+%D0%BD%D0%B0+%D0%BF%D1%80%D0%BE%D0%B5%D0%BA%D1%82%D1%8B+%D1%81+%D0%BE%D1%82%D0%BA%D1%80%D1%8B%D1%82%D1%8B%D0%BC+%D0%B8%D1%81%D1%85%D0%BE%D0%B4%D0%BD%D1%8B%D0%BC+%D0%BA%D0%BE%D0%B4%D0%BE%D0%BC&default-sum=100&successURL=)

# Changelog

- **6.1.3-SNAPSHOT**
  - CORE: added `/U` key to turn on mode to interpret unknown variables as FALSE (in Maven and ANT `unknownVarAsFalse`), [#17](https://github.com/raydac/java-comment-preprocessor/issues/17)

- **6.1.2 (02 apr 2017)**
  - CORE: added STR TRIMLINES(STR) function to trim lines represented as string and removing empty lines
  - CORE: added `/A` command line option (`copyFileAttributes` in Maven and ANT) to copy file attributes
  - CORE: added `/ED:` command line option to exclude sub-folders from preprocessing (`excludedFolders` in Maven and ANT) with ANT pattern support.
  - CORE: added `/PI` command line option (`preserveIndent` in Maven and ANT), turn on mode to preserve indent when removing `//$` and `//$$`, thanks to [jamuir](https://github.com/jamuir)
  - CORE: comma in split lines in BINFILE function moved from the start of line to the end of the previous line (to increase compatibility with Go)

- **6.1.1 (11 feb 2017)**
  - MAVEN: information about imported maven properties will be shown only in either verbose mode or debug mode
  - MAVEN: added auxiliary goal `preprocessTests` which provides flag `useTestSources` as true and activated by default in GENERATE_TEST_SOURCES phase [#14](https://github.com/raydac/java-comment-preprocessor/issues/14)
  - MAVEN: added `ignoreMissingSources` boolean parameter, allows to skip preprocessing if source folders not found or not provided [#12](https://github.com/raydac/java-comment-preprocessor/issues/12)
  - MAVEN: added `skip` boolean parameter, it allows to skip execution, also it is possible to use `-Djcp.preprocess.skip=true` [#13](https://github.com/raydac/java-comment-preprocessor/issues/13)
  -  CORE: added function `BOOL is(STR,ANY)` to check existence of variable for its name and compare its value with etalon (through string conversion, it will ensure true for `true` and `"true"` case) [#10](https://github.com/raydac/java-comment-preprocessor/issues/10)
  -  CORE: added `STR str2go(STR)` function to escape strings to be represented in Golang sources
  -  CORE: improved the BINFILE function, it allows `base64|byte[]|uint8[]|int8` and modifiers `s|d|ds|sd` where s - means splitting to lines and d - means deflate compression

# Introduction
I guess it is most powerful preprocessor for Java because it is a multi-pass one and can work with XML files as data sources. The First version of the preprocessor was published in 2003 and it was very actively used for J2ME developments. Modern version can be used for any kind of Java project because it can be used with ANT, MAVEN and Gradle.
![Features](https://raw.githubusercontent.com/raydac/java-comment-preprocessor/master/assets/doc1.png)
  
# How to use
[The Full list of the preprocessor directives can be found in the wiki.](https://github.com/raydac/java-comment-preprocessor/wiki/PreprocessorDirectives)   

The Preprocessor can be used by different ways:
  - as ANT task, and with Android SDK
  - as Maven plugin
  - [with Gradle through ANT task](https://github.com/raydac/java-comment-preprocessor/wiki/AndroidGradlePreprocessing)
  - as Java framework with direct class calls
  - as external utility through CLI (command line interface) 
The Preprocessor is published in the Maven Central so that can be added in Maven projects without any problems
```
    <build>
        <plugins>
...
           <plugin>
                <groupId>com.igormaznitsa</groupId>
                <artifactId>jcp</artifactId>
                <version>6.1.0</version>
                <executions>
                    <execution>
                        <id>preprocessSources</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>preprocess</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>clearGeneratedFolders</id>
                        <goals>
                            <goal>clear</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
...
        </plugins>
    </build>    
```

# How to use from command line
The Preprocessor jar can be started under Java as a console application. Let's take a look at short example below how to start in command line under Linux The Easy variant of usage:
```
java -jar jcp-6.1.0.jar  --i:./test --o:./result
```
The Example just preprocess files from ./test folder which extensions allowed to be preprocessed by default, and places result into ./result folder, but keep in your mind that the preprocessor copies not all files, XML files will not be preprocessed by default. Files which extension are not marked for preprocessing will be just copied (of course if the extensions is not in the list of excluded file extensions) 

More complex example:
```
java -jar jcp-6.1.0.jar  --c --r --v --f:java,xml --ef:none --i:./test --o:./result  '--p:HelloWorld=$Hello world$'
```
- --c clear the destination folder before work
- --r remove all Java-style comments from preprocessed result files
- --v show verbose log about preprocessing process
- --f include .java and .xml files into preprocessing (by default the preprocessor doesn't preprocess XNL files and the extension should to be defined explicitly)
- --ef don't exclude any extension from preprocessing
- --i use ./test as source folder
- --o use ./result as destination folder
- --p define named global variable HelloWorld? with the 'Hello world' content 
- --z turn on checking of file content before replacement, if the same content then preprocessor will not replace the file  
- --es allow whitespace between comment and directive (by default it is turned off)

# The Main idea
The Java language was born without any preprocessor in creator's mind and even now there are not any plans to include preprocessing into Java. It was good until mass usage Java on mobile and TV devices where we have bunches of half-compatible devices with (sometime) very bizarre standard framework implementations. In the case, preprocessing allows to decrease support of sources dramatically.  
The only possible way to include preprocessing directives into Java and to not break standard processes and Java tool chain is to inject them into comments, take a look at the example below:
```Java
//#local TESTVAR="TEST LOCAL VARIABLE"
//#echo TESTVAR=/*$TESTVAR$*/
//#include "./test/_MainProcedure.java"

public static final void testproc()
{
 System.out.println(/*$VARHELLO$*/);
 System.out.println("// Hello commentaries");
 //#local counter=10
        //#while counter!=0
        System.out.println("Number /*$counter$*/");
        //#local counter=counter-1
        //#end
 System.out.println("Current file name is /*$SRV_CUR_FILE$*/");
 System.out.println("Output dir is /*$SRV_OUT_DIR$*/");
 //#if issubstr("Hello","Hello world")
 System.out.println("Substring found");
 //#endif
}
```

# Multi-sectioned documents
Java sources usually have sections, there are the import section and the main section thus JCP has support for such case and there are three section where the preprocessor can write results - the prefix, the middle part and the postfix. Usually I use the prefix to form the import section for Java files. You can switch the text output for sections with //#prefix[+|-] and //#postfix[+|-] directives. 
```Java
//#prefix+
 import java.lang.*;
 //#prefix-
 public class Main {
  //#prefix+
  import java.util.*;
  //#prefix-
  public static void main(String ... args){}
 }
```
# OMG! It allows to remove all your comments!
Sometime it is very useful to remove all comments from my sources at all, JCP has such feature which can be turned on by special flag or command line switcher (see wiki). The Example of use for comment removing through CLI interface 
```
java -jar ./jcp-6.1.0.jar --i:/sourceFolder --o:/resultFolder -ef:none --r
```
# Public snapshot repository for the plugin
To make accessible the snapshot version of the plugin during development, I have tuned public maven snapshot repository which can be added into project with snippet
```xml
<repositories>
 <repository>
  <id>coldcore.ru-snapshots</id>
  <name>ColdCore.RU Mvn Snapshots</name>
  <url>http://coldcore.ru/m2</url>
  <snapshots>
   <enabled>true</enabled>
  </snapshots>
  <releases>
   <enabled>false</enabled>
  </releases>
 </repository>
</repositories>
```
