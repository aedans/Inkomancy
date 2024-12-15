package hans.inkomancy.morphemes;

import hans.inkomancy.InterpretError;
import hans.inkomancy.Morpheme;
import hans.inkomancy.Spell;
import hans.inkomancy.SpellContext;

import java.util.Set;

public class SourceMorpheme extends Morpheme {
  public static final SourceMorpheme INSTANCE = new SourceMorpheme();

  private SourceMorpheme() {
    super("source", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    for (var s : spell.connected()) {
      s.morpheme().interpretAsAction(s, context);
    }
  }
}
