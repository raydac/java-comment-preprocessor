package com.igormaznitsa.jcp.it.test;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

public class JavassistMain {

  static {
    try {
      final ClassPool cp = ClassPool.getDefault();
      cp.appendClassPath(new LoaderClassPath(JavassistMain.class.getClassLoader()));

      final CtClass ctClazz = cp.get(JavassistMain.class.getPackage().getName() + ".Main");
      final CtMethod printLines = ctClazz.getDeclaredMethod("printLines");

      // source of the method from the Javassistable file will be injected
      printLines.insertBefore(
          /*$str2java(evalfile("Javassistable.java"),true)$*//*-*/""
      );
      ctClazz.toClass();
    } catch (Exception ex) {
      throw new Error("Can't make javassist work", ex);
    }
  }

  public boolean printLines(int lineNumber, String pattern, String text) {
    return new Main().printLines(lineNumber, pattern, text);
  }
}
