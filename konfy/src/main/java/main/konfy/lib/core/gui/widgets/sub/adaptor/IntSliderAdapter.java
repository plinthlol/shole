package main.konfy.lib.core.gui.widgets.sub.adaptor;

public class IntSliderAdapter implements SliderAdapter<Integer> {
   private final int min;
   private final int max;
   private final int def;

   public IntSliderAdapter(int min, int max, int def) {
      this.min = min;
      this.max = max;
      this.def = def;
   }

   public float toSliderPosition(Integer value) {
      return (float)(value - this.min) / (this.max - this.min);
   }

   public Integer fromSliderPosition(float sliderPos) {
      return this.min + Math.round(sliderPos * (this.max - this.min));
   }

   public Integer clamp(Integer value) {
      return Math.max(this.min, Math.min(this.max, value));
   }

   public String format(Integer value) {
      return Integer.toString(value);
   }

   public Integer defaultValue() {
      return this.def;
   }

   public Integer getMin() {
      return this.min;
   }

   public Integer getMax() {
      return this.max;
   }
}
