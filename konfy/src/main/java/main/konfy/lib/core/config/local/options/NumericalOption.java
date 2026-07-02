package main.konfy.lib.core.config.local.options;

import java.util.function.Consumer;
import java.util.function.Supplier;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.builders.OptionBuilder;

public class NumericalOption<T extends Number> extends OptionBuilder<T, NumericalOption<T>> {
   private T min;
   private T max;
   private T increment;

   public NumericalOption(String name, Supplier<T> getter, T defaultOption, Consumer<T> setter) {
      super(name, getter, defaultOption, setter);
   }

   public static <T extends Number> NumericalOption<T> createBuilder(String name, Supplier<T> getter, T defaultValue, Consumer<T> setter) {
      return new NumericalOption<>(name, getter, defaultValue, setter);
   }

   public NumericalOption<T> values(T min, T max, T increment) {
      this.min = min;
      this.max = max;
      this.increment = increment;
      return this;
   }

   @Override
   public Option<T> build() {
      return new Option<>(
         this.name,
         this.description,
         this.getter,
         this.setter,
         this.availability,
         this.availabilityHelp,
         (Class<T>)this.getter.get().getClass(),
         this.min,
         this.max,
         this.increment,
         this.defaultValue,
         null,
         this.onChange
      );
   }
}
