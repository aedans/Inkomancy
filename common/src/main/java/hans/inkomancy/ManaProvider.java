package hans.inkomancy;

public class ManaProvider {
  public Ink ink;
  public int current;

  public ManaProvider(Ink ink, int current) {
    this.current = current;
    this.ink = ink;
  }

  public boolean canConsume(int amount) {
    return amount <= current;
  }

  public void consume(int amount) throws InterpretError {
    if (!canConsume(amount)) {
      throw new InterpretError.OOM();
    } else {
      current -= amount;
    }
  }
}
