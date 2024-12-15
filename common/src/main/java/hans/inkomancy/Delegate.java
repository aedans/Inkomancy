package hans.inkomancy;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Delegate<T> {
  T get();

  void set(T modified);

  default void update(Consumer<T> f) {
    var t = get();
    f.accept(t);
    set(t);
  }

  default void modify(Function<T, T> f) {
    set(f.apply(get()));
  }

  default void destroy() {
    set(null);
  }

  static <T> Delegate<T> of(T t) {
    return new Delegate<>() {
      private T value = t;

      @Override
      public T get() {
        return value;
      }

      @Override
      public void set(T modified) {
        value = modified;
      }
    };
  }
}
