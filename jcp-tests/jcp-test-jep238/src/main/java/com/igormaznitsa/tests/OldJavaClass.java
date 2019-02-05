// the string belows tells to the preprocessor to exclude the file from module variant for JDK 9
//#excludeif java.version>8
package com.igormaznitsa.tests;

/**
 * The Class will be presented only for Java which version less than 9.
 */
public class OldJavaClass {
    public String getString(){
        return "POJO";
    }
}
