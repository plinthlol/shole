package main.konfy.lib.core.config.local.options;

import java.util.function.Consumer;
import java.util.function.Supplier;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.builders.OptionBuilder;

public class StringOption extends OptionBuilder<String, StringOption> {
   public StringOption(String name, Supplier<String> getter, String defaultValue, Consumer<String> setter) {
      super(name, getter, defaultValue, setter);
   }

   public static StringOption createBuilder(String name, Supplier<String> getter, String defaultValue, Consumer<String> setter) {
      return new StringOption(name, getter, defaultValue, setter);
   }

   @Override
   public Option<String> build() {
      return new Option<>(
         this.name, this.description, this.getter, this.setter, this.availability, this.availabilityHelp, String.class, this.defaultValue, this.onChange
      );
   }
}
