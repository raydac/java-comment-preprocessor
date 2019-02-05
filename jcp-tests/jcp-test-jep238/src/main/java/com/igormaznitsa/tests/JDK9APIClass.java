// the directive below tells preprocessor to exclude the class from module if JDK version is less than 9
//#excludeif jdk.version<9
package com.igormaznitsa.tests;

import java.util.List;

/**
 * The Class will be presented only for Java 9+.
 */
public class JDK9APIClass {
    public List<String> getList() {
        // Let use JDK 9 new feature
        return List.of("one", "two", "three");
    }
}
