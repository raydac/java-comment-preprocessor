The JCPreprocessor (Java Comment Preprocessor) is a very powerful multi-pass preprocessor supports loops and file generation features. Since it keeps its directives inside comment strings it can be used without problems in any Java IDE.
The first version of the JCPreprocessor was developed by Igor Maznitsa in 2002 and has being used in dozens of mobile application projects for well-known trademarks. In 2011 it was totally refactored and the MAVEN support was added. At present it can be used as:
 - A Maven Plugin
 - An ANT task
 - A Standalone application called through command line

The preprocessor is an open source project and its home page is http://code.google.com/p/java-comment-preprocessor/ where you can find new versions and wiki. Since 2011 the preprocessor is developed and distributed under GNU LGPL v3 license.

You can install the plugin into the local maven repository with the install:install-file goal:

    mvn install:install-file -Dfile=./jcp-5.0.jar -DpomFile=./pom.xml