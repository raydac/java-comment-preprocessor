//#excludeif java.version<9
package com.igormaznitsa.tests;

import static org.junit.Assert.*;
import  org.junit.*;

public class JDK9APIClassTest {
    @Test
    public void testGetList() {
        Assert.assertArrayEquals(new String[]{"one","two","three"},new JDK9APIClass().getList().toArray());
    }
}