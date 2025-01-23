package hans.inkomancy.morphemes;

import hans.inkomancy.Morpheme;

import java.util.Set;

public class ContinueMorpheme extends Morpheme {
  public static final ContinueMorpheme INSTANCE = new ContinueMorpheme();

  protected ContinueMorpheme() {
    super("continue", Set.of());
  }
}
