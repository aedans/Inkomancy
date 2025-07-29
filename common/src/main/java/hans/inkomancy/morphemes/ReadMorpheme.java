package hans.inkomancy.morphemes;

import hans.inkomancy.Morpheme;
import hans.inkomancy.Spell;
import hans.inkomancy.SpellContext;

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
}
