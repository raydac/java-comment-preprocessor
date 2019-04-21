package android.it.igormaznitsa.com.jcpandroid.utils;

public final class Utils {

  private Utils(){

  }

  //#if remove.secret
  //$public static String makeSecretPassword() {
  //$  return "testPassword";
  //$}
  //#else
  public static String makeSecretPassword() {
    return "122sdsferSSADSD123232";
  }
  //#endif
}
