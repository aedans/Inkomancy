package hans.inkomancy.morphemes;

import hans.inkomancy.InterpretError;
import hans.inkomancy.Morpheme;
import hans.inkomancy.Spell;
import hans.inkomancy.SpellContext;

import java.util.Set;

public class UndoMorpheme extends Morpheme {
  public static final UndoMorpheme INSTANCE = new UndoMorpheme();

  private UndoMorpheme() {
    super("undo", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    for (var s : spell.connected()) {
      if (s.morpheme().supported.contains(Type.ACTION)) {
        s.morpheme().interpretAsAction(s, context, !undo);
      }
    }
  }
}
