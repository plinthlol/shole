package main.konfy.lib.core.config.local.options;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.builders.OptionBuilder;

public class StringListOption extends OptionBuilder<List<String>, StringListOption> {
   public StringListOption(String name, Supplier<List<String>> getter, List<String> defaultValue, Consumer<List<String>> setter) {
      super(name, getter, defaultValue, setter);
   }

   public static StringListOption createBuilder(String name, Supplier<List<String>> getter, List<String> defaultValue, Consumer<List<String>> setter) {
      return new StringListOption(name, getter, defaultValue, setter);
   }

   @Override
   @SuppressWarnings("unchecked")
   public Option<List<String>> build() {
      Class<List<String>> clazz = (Class<List<String>>) (Class<?>) List.class;
      return new Option<>(
         this.name, this.description, this.getter, this.setter, this.availability, this.availabilityHelp, clazz, this.defaultValue, this.onChange
      );
   }
}
