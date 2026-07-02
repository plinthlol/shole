package main.konfy.lib.core.config.local.options;

import java.util.function.Consumer;
import java.util.function.Supplier;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.builders.OptionBuilder;
import main.konfy.lib.core.config.local.options.type.PixelGrid;

public class PixelGridOption extends OptionBuilder<PixelGrid, PixelGridOption> {
   public PixelGridOption(String name, Supplier<PixelGrid> getter, PixelGrid defaultValue, Consumer<PixelGrid> setter) {
      super(name, getter, defaultValue, setter);
   }

   public static PixelGridOption createBuilder(String name, Supplier<PixelGrid> getter, PixelGrid defaultValue, Consumer<PixelGrid> setter) {
      return new PixelGridOption(name, getter, defaultValue, setter);
   }

   @Override
   public Option<PixelGrid> build() {
      return new Option<>(
         this.name, this.description, this.getter, this.setter, this.availability, this.availabilityHelp, PixelGrid.class, this.defaultValue, this.onChange
      );
   }
}
