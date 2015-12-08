JCPreprocessor
---------------

Author: Igor Maznitsa (http://www.igormaznitsa.com)

It is a multi-pass preprocessor with very powerful features (prefixes and postfixes in result, text generation within loops and global and local definitions). The Initial version was developed in 2002 by Igor Maznitsa to make easier the development for the J2ME platform because it was very hard to support multi-variants of the same sources for different J2ME API usage variants provided in devices of different vendors. To keep the preprocessor compatible with IDE and regular Java development chain, commented directives were chosen what doesn't make any effect in the regular java development process and IDEs but allows to make changes related to external definitions.
In 2011 the preprocessor was totally reworked and published as an OSS project on Google Code https://github.com/raydac/java-comment-preprocessor
Now the preprocessor supports work as :
 - a MAVEN plugin
 - an ANT task
 - a standalone application called through CLI (command line interface)
 - a Java library with direct call to JCPreprocessor class

Licensing
-----------
Initially the preprocessor was published and distributed under GNU LGPL v3 but since the 5.3.3 version (2014) it has been distributed under Apache License 2.0

Usage with Maven
------------------

Since version 5.3.2 I public the released plugin versions in the central Maven repository:
<build>
  <plugins>
...
            <plugin>
                <groupId>com.igormaznitsa</groupId>
                <artifactId>jcp</artifactId>
                <version>6.0.2</version>
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

    mvn install:install-file -Dfile=./jcp-6.0.2.jar -DpomFile=./pom.xml


Building
---------
The project needs as minimum Maven 3.0.3 and JDK 1.6 to be built. To build the preprocessor manually you have to define the path to your Maven directory in the configuration property 'maven.home' of the 'maven-failsafe-plugin' in the pom.xml.
It is a solid project without modules so that its inside testing (especially for the maven part) looks a bit bizarre, may be it would be more better to be implemented as a multi-module maven project but it's some kind of legacy.

Versions
---------

See the changelog.txt file