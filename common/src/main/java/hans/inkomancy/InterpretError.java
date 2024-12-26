package hans.inkomancy;

public abstract class InterpretError extends Exception {
  private InterpretError(String message) {
    super(message);
  }

  public static class Conversion extends InterpretError {
    public Conversion(Morpheme morpheme, Morpheme.Type type) {
      super("cannot convert " + morpheme + " to " + type);
    }
  }

  public static class OOM extends InterpretError {
    public OOM() {
      super("out of mana");
    }
  }
}
