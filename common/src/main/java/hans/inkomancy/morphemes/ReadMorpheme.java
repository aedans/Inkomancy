package hans.inkomancy.morphemes;

import hans.inkomancy.InterpretError;
import hans.inkomancy.Morpheme;
import hans.inkomancy.Spell;
import hans.inkomancy.SpellContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReadMorpheme extends Morpheme {
  public static final ReadMorpheme INSTANCE = new ReadMorpheme();

  private ReadMorpheme() {
    super("read", Set.of(Type.SPELL, Type.POSITION));
  }

  @Override
  public Spell interpretAsSpell(Spell spell, SpellContext context) {
    return new Spell(SourceMorpheme.INSTANCE, spell.connected()).base();
  }

  @Override
  public List<Position> interpretAsPositions(Spell spell, SpellContext context) throws InterpretError {
    var positions = new ArrayList<Position>();
    for (var s : spell.connected()) {
      positions.addAll(s.morpheme().interpretAsPositions(s, context));
    }
    return positions;
  }
}
