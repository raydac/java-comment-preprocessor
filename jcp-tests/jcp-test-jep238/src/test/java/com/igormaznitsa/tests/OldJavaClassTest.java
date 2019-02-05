//#excludeif java.version>8
package com.igormaznitsa.tests;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OldJavaClassTest {

    @Test
    public void testGetString() {
        assertEquals("POJO", new OldJavaClass().getString());
    }

}