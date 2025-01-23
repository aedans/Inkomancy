package hans.inkomancy;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Delegate<T> {
  T get();

  void set(T modified);

  void action(boolean replace);

  default void update(Consumer<T> f) {
    var t = get();
    f.accept(t);
    set(t);
  }

  default void modify(Function<T, T> f) {
    set(f.apply(get()));
  }

  class Instance<T> implements Delegate<T> {
    private T t;

    public Instance(T t) {
      this.t = t;
    }

    @Override
    public T get() {
      return t;
    }

    @Override
    public void set(T modified) {
      t = modified;
    }

    @Override
    public void action(boolean replace) {

    }

    @Override
    public String toString() {
      return "Delegate{" + t + '}';
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      Instance<?> delegate = (Instance<?>) o;
      return Objects.equals(t, delegate.t);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(t);
    }
  }
}
