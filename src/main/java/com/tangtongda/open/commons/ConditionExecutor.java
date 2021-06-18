package com.tangtongda.open.commons;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * condition method executor
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2021/3/25
 */
public class ConditionExecutor {

  private ConditionExecutor() {}

  /** condition param */
  public static boolean param = false;

  /** builder class */
  public static class Builder<R> {

    public Builder() {}

    private R result;

    public Builder(R result) {
      this.result = result;
    }

    /**
     * add condition
     *
     * @param condition boolean true
     * @param <R> result type
     * @return Builder instance
     */
    public static <R> Builder<R> condition(boolean condition) {
      param = condition;
      return new Builder<>();
    }

    /**
     * map handler with result returned
     *
     * @param t param
     * @param mapper method reference
     * @param <T> param type
     * @return map result
     */
    public <T> Builder<R> map(T t, Function<? super T, ? extends R> mapper) {
      Objects.requireNonNull(mapper);
      if (!param) return new Builder<>();
      else {
        return new Builder<>(mapper.apply(t));
      }
    }

    public <T> void execute(T t, Consumer<? super T> consumer) {
      Objects.requireNonNull(consumer);
      if (param) consumer.accept(t);
    }

    public R getResult() {
      return result;
    }
  }

  public static <R> Builder<R> when(boolean condition) {
    return Builder.condition(condition);
  }
}
