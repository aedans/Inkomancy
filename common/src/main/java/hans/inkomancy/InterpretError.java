package hans.inkomancy;

import java.util.Collection;

public abstract class InterpretError extends Exception {
  private InterpretError(String message) {
    super(message);
  }

  public static class Conversion extends InterpretError {
    public Conversion(Morpheme morpheme, Collection<Morpheme.Type> types) {
      super("cannot convert " + morpheme + " to " + types.stream().map(x -> x.toString().toLowerCase()).toList());
    }

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
