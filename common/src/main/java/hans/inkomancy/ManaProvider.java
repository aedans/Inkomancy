package hans.inkomancy;

import hans.inkomancy.inks.RedInk;

public class ManaProvider {
  public Ink ink;
  public int current;
  public int initial;

  public ManaProvider(Ink ink, int current) {
    this.initial = current;
    this.current = current;
    this.ink = ink;
  }

  public void consume(int amount) throws InterpretError {
    current = ink.modifyMana(initial, current, amount);
  }

  public void produce(int amount) throws InterpretError {
    current = ink.modifyMana(initial, current, -amount);
  }

  public static ManaProvider infinite() {
    return of(Integer.MAX_VALUE);
  }

  public static ManaProvider of(int mana) {
    return new ManaProvider(RedInk.INSTANCE, mana);
  }
}
