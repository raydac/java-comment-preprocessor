# Java Comment Preprocessor

Java Comment Preprocessor (JCP) is a Maven plugin that allows preprocessing of source code for languages that support
C-style comments. It stores its directives inside comments and supports some automation features, such as loops and file
loading/processing. JCP was originally created for developing mobile J2ME games, but later evolved into a full-featured
tool.

Just add code-snippet below into build section and the plugin will be started during build.

```xml

<build>
    <plugins>
        <plugin>
            <groupId>com.igormaznitsa</groupId>
            <artifactId>jcp</artifactId>
            <version>7.2.1</version>
            <executions>
                <execution>
                    <id>preprocess-sources</id>
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
