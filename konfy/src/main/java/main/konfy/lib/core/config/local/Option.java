package main.konfy.lib.core.config.local;

import java.awt.Color;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import main.konfy.lib.core.config.local.options.BooleanOption;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.config.local.options.type.PixelGridAnimation;
import main.konfy.lib.core.config.local.options.type.KonfyLibColor;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.gui.widgets.BooleanWidget;
import main.konfy.lib.core.gui.widgets.ButtonOptionWidget;
import main.konfy.lib.core.gui.widgets.ColorWidget;
import main.konfy.lib.core.gui.widgets.EnumOptionWidget;
import main.konfy.lib.core.gui.widgets.NumericalWidget;
import main.konfy.lib.core.gui.widgets.OptionWidget;
import main.konfy.lib.core.gui.widgets.PixelGridAnimationWidget;
import main.konfy.lib.core.gui.widgets.StringListOptionWidget;
import main.konfy.lib.core.gui.widgets.StringOptionWidget;
import main.konfy.lib.core.utils.SearchUtils;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class Option<T> {
   private final String name;
   private final Supplier<T> getter;
   private final Consumer<T> setter;
   private final Class<T> type;
   private final T min;
   private final T max;
   private final T increment;
   private OptionDescription description;
   private final T defaultValue;
   private final Supplier<Boolean> availability;
   private final String availabilityHelper;
   private T prevValue;
   private final Runnable onChange;
   public T screenInstanceValue = (T)null;
   private final BooleanOption.Warning warning;
   private String searchQ = "";

   public Option(
      String name,
      OptionDescription description,
      Supplier<T> getter,
      Consumer<T> setter,
      Supplier<Boolean> availability,
      String availabilityHelper,
      Class<T> type,
      T defaultValue,
      BooleanOption.Warning warning,
      Runnable onChange
   ) {
      this(name, description, getter, setter, availability, availabilityHelper, type, null, null, null, defaultValue, warning, onChange);
   }

   public Option(
      String name,
      OptionDescription description,
      Supplier<T> getter,
      Consumer<T> setter,
      Supplier<Boolean> availability,
      String availabilityHelper,
      Class<T> type,
      T defaultValue,
      Runnable onChange
   ) {
      this(name, description, getter, setter, availability, availabilityHelper, type, null, null, null, defaultValue, null, onChange);
   }

   public Option(
      String name,
      OptionDescription description,
      Supplier<T> getter,
      Consumer<T> setter,
      Supplier<Boolean> availability,
      String availabilityHelper,
      Class<T> type,
      T min,
      T max,
      T increment,
      T defaultValue,
      BooleanOption.Warning warning,
      Runnable onChange
   ) {
      this.name = name;
      this.description = description;
      this.getter = getter;
      this.setter = setter;
      this.availability = availability;
      this.availabilityHelper = availabilityHelper;
      this.type = type;
      this.min = min;
      this.max = max;
      this.increment = increment;
      if (getter.get() instanceof PixelGridAnimation) {
         this.defaultValue = (T)((PixelGridAnimation)defaultValue).copy();
      } else if (getter.get() instanceof KonfyLibColor) {
         this.defaultValue = (T)((KonfyLibColor)defaultValue).copy();
      } else {
         this.defaultValue = defaultValue;
      }

      this.prevValue = null;
      this.onChange = onChange;
      this.warning = warning;
   }

   public String getName() {
      return this.name;
   }

   private Supplier<T> getGetter() {
      return this.getter;
   }

   private Consumer<T> getSetter() {
      return this.setter;
   }

   public Class<T> getType() {
      return this.type;
   }

   public T getMin() {
      return this.min;
   }

   public T getMax() {
      return this.max;
   }

   public T getIncrement() {
      return this.increment;
   }

   public OptionDescription getDescription() {
      return this.description;
   }

   public String getAvailabilityHelper() {
      return this.availabilityHelper;
   }

   public T getValue() {
      return this.getter.get();
   }

   public T getDefaultValue() {
      return this.defaultValue;
   }

   public boolean screenInstanceCheck() {
      return Objects.equals(this.screenInstanceValue, this.getValue());
   }

   public void setScreenInstance() {
      if (this.getValue() instanceof PixelGridAnimation animation) {
         this.screenInstanceValue = (T)animation.copy();
      } else if (this.getValue() instanceof KonfyLibColor color) {
         this.screenInstanceValue = (T)color.copy();
      } else {
         this.screenInstanceValue = this.getter.get();
      }
   }

   public void undo() {
      if (this.setter != null) {
         this.setter.accept(this.screenInstanceValue);
      }
   }

   public void setValue(Object value) {
      if (this.type.isInstance(value)) {
         if (this.setter != null) {
            if (!this.getter.get().equals(value)) {
               this.runChange();
            }

            this.setter.accept((T)value);
         }
      } else {
         throw new IllegalArgumentException("Invalid value type: " + value.getClass().getName());
      }
   }

   public boolean canReset() {
      return this.getValue() != this.defaultValue;
   }

   public boolean isAvailable() {
      return this.availability.get();
   }

   public void setPrev(String config) {
      if (!Objects.equals(this.getValue(), this.screenInstanceValue)) {
         this.prevValue = this.screenInstanceValue;
         Object oldVal = this.prevValue;
         Object newVal = this.getValue();
         if (oldVal instanceof KonfyLibColor oldColor && newVal instanceof KonfyLibColor newColor) {
            if (oldColor.isRainbow() != newColor.isRainbow()) {
               this.logField(config, this.getName() + "'s Rainbow", oldColor.isRainbow(), newColor.isRainbow());
            }

            if (oldColor.getRainbowSpeed() != newColor.getRainbowSpeed()) {
               this.logField(config, this.getName() + "'s Rainbow Speed", oldColor.getRainbowSpeed(), newColor.getRainbowSpeed());
            }

            if (oldColor.isPulse() != newColor.isPulse()) {
               this.logField(config, this.getName() + "'s Pulse", oldColor.isPulse(), newColor.isPulse());
            }

            if (oldColor.getPulseSpeed() != newColor.getPulseSpeed()) {
               this.logField(config, this.getName() + "'s Pulse Speed", oldColor.getPulseSpeed(), newColor.getPulseSpeed());
            }

            if (Float.compare(oldColor.getSaturation(), newColor.getSaturation()) != 0) {
               this.logField(config, this.getName() + "'s Saturation", oldColor.getSaturation(), newColor.getSaturation());
            }

            if (Float.compare(oldColor.getBrightness(), newColor.getBrightness()) != 0) {
               this.logField(config, this.getName() + "'s Brightness", oldColor.getBrightness(), newColor.getBrightness());
            }
         } else if (oldVal instanceof PixelGridAnimation oldAnim && newVal instanceof PixelGridAnimation newAnim) {
            if (oldAnim.getAnimationSpeed() != newAnim.getAnimationSpeed()) {
               this.logField(config, this.getName() + "'s Speed", oldAnim.getAnimationSpeed(), newAnim.getAnimationSpeed());
            }

            if (Double.compare(oldAnim.getOffsetX(), newAnim.getOffsetX()) != 0) {
               this.logField(config, this.getName() + "'s X Pos", oldAnim.getOffsetX(), newAnim.getOffsetX());
            }

            if (Double.compare(oldAnim.getOffsetY(), newAnim.getOffsetY()) != 0) {
               this.logField(config, this.getName() + "'s Y Pos", oldAnim.getOffsetY(), newAnim.getOffsetY());
            }
         } else {
            this.logField(config, this.getName(), oldVal, newVal);
         }
      }
   }

   private <V> void logField(String configName, String name, V oldVal, V newVal) {
   }

   public void reset() {
      if (this.getType() != Runnable.class) {
         if (this.getValue() instanceof PixelGridAnimation) {
            this.setter.accept((T)((PixelGridAnimation)this.defaultValue).copy());
         } else if (this.getValue() instanceof KonfyLibColor c) {
            this.setter.accept((T)((KonfyLibColor)this.defaultValue).copy());
         } else {
            this.setter.accept(this.defaultValue);
         }
      }
   }

   public Option<T> description(OptionDescription description) {
      this.description = description;
      return this;
   }

   public boolean hasChanged() {
      T value = this.getValue();
      T defaultValue = this.getDefaultValue();
      return value != null && defaultValue != null ? !Objects.equals(value, defaultValue) : value != defaultValue;
   }

   public T getPrevValue() {
      return this.prevValue;
   }

   public boolean searched() {
      if (this.searchQ.isEmpty()) {
         return true;
      }

      String[] queryWords = this.searchQ.toLowerCase().trim().split("\\s+");
      String[] nameWords = this.getName().toLowerCase().trim().split("\\s+");

      label35:
      for (String qWord : queryWords) {
         for (String numWord : nameWords) {
            if (numWord.contains(qWord) || numWord.startsWith(qWord) || SearchUtils.levenshteinDistance(numWord, qWord) <= 2) {
               continue label35;
            }
         }

         return false;
      }

      return true;
   }

   public void updateSearchQ(String searchQ) {
      this.searchQ = searchQ;
   }

   public void runChange() {
      if (this.onChange != null) {
         this.onChange.run();
      }
   }

   @SuppressWarnings("unchecked")
   public OptionWidget createWidget(OptionGroup parent, KonfyLibConfigScreen screen, int x, int y, int width, int height) {
      if (this.type == Boolean.class) {
         return new BooleanWidget(parent, screen, x, y, width, height, (Option<Boolean>) (Option<?>) this, this.warning);
      } else if (this.type == Integer.class) {
         return new NumericalWidget<>(parent, screen, x, y, width, height, (Option<Integer>) (Option<?>) this);
      } else if (this.type == Double.class) {
         return new NumericalWidget<>(parent, screen, x, y, width, height, (Option<Double>) (Option<?>) this);
      } else if (this.type == Float.class) {
         return new NumericalWidget<>(parent, screen, x, y, width, height, (Option<Float>) (Option<?>) this);
      } else if (this.type == Color.class || this.type == KonfyLibColor.class) {
         return new ColorWidget(parent, screen, x, y, width, height, (Option<KonfyLibColor>) (Option<?>) this);
      } else if (this.type == PixelGridAnimation.class) {
         return new PixelGridAnimationWidget(parent, screen, x, y, width, height, (Option<PixelGridAnimation>) (Option<?>) this);
      } else if (this.type == List.class) {
         return new StringListOptionWidget(parent, screen, x, y, width, height, (Option<List<String>>) (Option<?>) this);
      } else if (this.type == Runnable.class) {
         return new ButtonOptionWidget(parent, screen, x, y, width, height, (Option<Runnable>) (Option<?>) this);
      } else if (this.type == String.class) {
         return new StringOptionWidget(parent, screen, x, y, width, height, (Option<String>) (Option<?>) this);
      } else if (Enum.class.isAssignableFrom(this.type)) {
         Option<? extends Enum<?>> enumOption = (Option<? extends Enum<?>>) (Option<?>) this;
         return createEnumWidget(parent, screen, x, y, width, height, enumOption);
      } else {
         throw new UnsupportedOperationException("Unsupported option type: " + this.type);
      }
   }

   @SuppressWarnings("unchecked")
   private static <E extends Enum<E>> OptionWidget createEnumWidget(
      main.konfy.lib.core.config.local.options.groups.OptionGroup parent,
      KonfyLibConfigScreen screen,
      int x, int y, int width, int height,
      Option<? extends Enum<?>> enumOption
   ) {
      return new EnumOptionWidget<>(parent, screen, x, y, width, height, (Option<E>) enumOption);
   }
}
