# Usage

Just add the plugin into the module build section.

```xml

<plugin>
    <groupId>com.igormaznitsa</groupId>
    <artifactId>gosdk-wrapper-maven-plugin</artifactId>
    <version>1.0.5</version>
    <configuration>
        <goVersion>1.24.4</goVersion>
    </configuration>
    <executions>
        <execution>
            <id>go-help</id>
            <goals>
                <goal>execute</goal>
            </goals>
            <configuration>
                <args>
                    <arg>help</arg>
                </args>
            </configuration>
        </execution>
    </executions>
</plugin>
```

If you use `jar` packaging then you should deactivate default plugin calls. You can disable
them with below code snippet.

```xml

<plugin>
    <artifactId>maven-clean-plugin</artifactId>
    <executions>
        <execution>
            <id>default-clean</id>
            <phase>none</phase>
        </execution>
    </executions>
</plugin>
<plugin>
<artifactId>maven-jar-plugin</artifactId>
<executions>
    <execution>
        <id>default-jar</id>
        <phase>none</phase>
    </execution>
</executions>
</plugin>
<plugin>
<artifactId>maven-surefire-plugin</artifactId>
<executions>
    <execution>
        <id>default-test</id>
        <phase>none</phase>
    </execution>
</executions>
</plugin>
<plugin>
<artifactId>maven-compiler-plugin</artifactId>
<executions>
    <execution>
        <id>default-compile</id>
        <phase>none</phase>
    </execution>
    <execution>
        <id>default-testCompile</id>
        <phase>none</phase>
    </execution>
</executions>
</plugin>
<plugin>
<artifactId>maven-install-plugin</artifactId>
<executions>
    <execution>
        <id>default-install</id>
        <phase>none</phase>
    </execution>
</executions>
</plugin>
```
