package main.konfy.lib.core.config.local.options;

import java.util.function.Consumer;
import java.util.function.Supplier;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.builders.OptionBuilder;
import main.konfy.lib.core.config.local.options.type.KonfyLibColor;

public class ColorOption extends OptionBuilder<KonfyLibColor, ColorOption> {
   public ColorOption(String name, Supplier<KonfyLibColor> getter, KonfyLibColor defaultValue, Consumer<KonfyLibColor> setter) {
      super(name, getter, defaultValue, setter);
   }

   public static ColorOption createBuilder(String name, Supplier<KonfyLibColor> getter, KonfyLibColor defaultValue, Consumer<KonfyLibColor> setter) {
      return new ColorOption(name, getter, defaultValue, setter);
   }

   @Override
   public Option<KonfyLibColor> build() {
      return new Option<>(
         this.name,
         this.description,
         this.getter,
         this.setter,
         this.availability,
         this.availabilityHelp,
         KonfyLibColor.class,
         this.defaultValue,
         this.onChange
      );
   }
}
