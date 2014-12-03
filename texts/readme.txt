JCPreprocessor
---------------

Author: Igor Maznitsa (http://www.igormaznitsa.com)

It is a multi-pass preprocessor with very powerful features (like loops and global definitions accessible in all preprocessed files). The Initial version was developed in 2002 by Igor Maznitsa to make easier the development for the J2ME platform because it was very hard to support multi-variants of the same sources for different J2ME API usage variants provided in devices of different vendors. To keep the preprocessor compatible with IDE and regular Java development chain, commented directives were chosen what doesn't make any effect in the regular java development process and IDEs but allows to make changes related to external definitions.
In 2011 the preprocessor was totaly refactored and published as an OSS project, it can be downloaded from https://code.google.com/p/java-comment-preprocessor/
Now the preprocessor supports work by different ways:
 - as a maven plugin
 - as an ant task
 - as a standalone application called through CLI (command line interface)

Licensing
-----------
Initially the preprocessor was published and distributed under GNU LGPL v3 but since the 5.3.3 version (2014) it has been distributed under Apache License 2.0

Usage with Maven
------------------

Since version 5.3.2 I public the plugin in the central Maven repository:
<build>
  <plugins>
...
            <plugin>
                <groupId>com.igormaznitsa</groupId>
                <artifactId>jcp</artifactId>
                <version>5.4.0</version>
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

If you don't want use the maven repository then you can install the plugin manually into your local maven repository with the install:install-file goal:

    mvn install:install-file -Dfile=./jcp-5.4.0.jar -DpomFile=./pom.xml


Building
---------
The project needs as minimum Maven 3.0.3 and JDK 1.6 to be built. To build the preprocessor manually you have to define the path to your Maven directory in the configuration property 'maven.home' of the 'maven-failsafe-plugin' in the pom.xml.
It is a solid project without modules so that its inside testing (especially for the maven part) looks a bit bizarre, may be it would be more better to be implemented as a multi-module maven project but it's some kind of legacy.


History of changes
----------------------
5.4.0
- fixed vanished main class attribute in the manifest
- added function 'STR evalfile(STR)' for local preprocessing of a file body and return it as a string
- added predefined special read only variables '__filename__','__filefolder__' and '__file__' which allow to get name and path parameters for the current preprocessing file
- added function 'STR str2java(STR,BOOL)' to escape and split string to be presented as java sources
- added functions 'STR xml_xlist(STR,STR)' and 'STR xml_xelement(STR,STR)' which allow to use xpath to get element lists an elements
- apache common-io and common-lang libraries have been packed into the jar and hidden
- added the short variant '//#ifdef BOOL' for '//#ifdefined BOOL'
- added '//#ifndef BOOL' to check that a variable is undefined
- added '//#definel NAME' to define a local (!) variable as TRUE (just //#define defines a global(!) variable)
- added '//#undefl NAME' to remove a local (!) variable from the current context, and '//#undef NAME' to remove a global definition
- //#define and //#definel can use not only the default TRUE value for defined variables, but also result of expression (example: //#define ten 2*5)
- added '//#error EXPR' and '//#warning EXPR' directives to throw exception and log warnings

5.3.4
- added support of test source folder preprocessing for maven projects
- added the "clear" maven goal to clear created preprocessing folders or any defined folders and files
- by default the maven plugin trying to keep numeration of lines in preprocessed files (the keepLines is true by default)

5.3.3
- fixed the bug in the comment removing (multiple stars before closing slash)
- fixed the exception if there is not any organization tag in a project pom.xml
- added support for '-' and '--' prefixes in CLI arguments
- improved CLI argument error messaging
- the license has been changed to Apache 2.0

5.3.2
- very minor refactoring.
- fixed issue (ID 5) "Removing strings contain only spaces"
- the first version published in the maven central

5.3.1
- very minor fixing, added the main-class attribute in the preprocessor JAR Manifest 

5.3
- Added feature to keep non-executing lines as commented ones (/k command line key), all non-executing lines will be saved in the output as commented ones

5.2
- Fixed issue (ID 3). The default charset was used to read text files.

5.1
- Fixed issue (ID 1). Inaccessible functionality both "load a file with global variables" and "define global variable" through a command line call. 

5.0 
- The initial published version of totally reworked preprocessor