package main.konfy.lib.core.config.local.options;

import java.util.function.Consumer;
import java.util.function.Supplier;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.builders.OptionBuilder;

public class ButtonOption extends OptionBuilder<Runnable, ButtonOption> {
   public ButtonOption(String name, Supplier<Runnable> getter, Runnable defaultValue, Consumer<Runnable> setter) {
      super(name, getter, defaultValue, setter);
   }

   public static ButtonOption createBuilder(String name, Runnable action) {
      return new ButtonOption(name, () -> action, action, null);
   }

   @Override
   public Option<Runnable> build() {
      return new Option<>(
         this.name, this.description, this.getter, this.setter, this.availability, this.availabilityHelp, Runnable.class, this.defaultValue, this.onChange
      );
   }
}
