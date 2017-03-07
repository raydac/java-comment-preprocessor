package excludedfolder;

//#abort Must not be called!

public class Some {
  public Some(){
    throw new Error("Must be excluded");
  }
}