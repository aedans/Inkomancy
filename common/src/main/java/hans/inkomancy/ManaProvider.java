package hans.inkomancy;

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
    current = ink.consumeMana(initial, current, amount);
  }
}
