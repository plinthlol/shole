package main.konfy.lib.core.config.local.options;

import java.util.function.Consumer;
import java.util.function.Supplier;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.builders.OptionBuilder;
import main.konfy.lib.core.config.local.options.type.PixelGridAnimation;

public class PixelGridAnimationOption extends OptionBuilder<PixelGridAnimation, PixelGridAnimationOption> {
   public PixelGridAnimationOption(String name, Supplier<PixelGridAnimation> getter, PixelGridAnimation defaultValue, Consumer<PixelGridAnimation> setter) {
      super(name, getter, defaultValue, setter);
   }

   public static PixelGridAnimationOption createBuilder(
      String name, Supplier<PixelGridAnimation> getter, PixelGridAnimation defaultValue, Consumer<PixelGridAnimation> setter
   ) {
      return new PixelGridAnimationOption(name, getter, defaultValue, setter);
   }

   @Override
   public Option<PixelGridAnimation> build() {
      return new Option<>(
         this.name,
         this.description,
         this.getter,
         this.setter,
         this.availability,
         this.availabilityHelp,
         PixelGridAnimation.class,
         null,
         null,
         null,
         this.defaultValue,
         null,
         this.onChange
      );
   }
}
