package main.konfy.lib.core.gui.widgets.sub.adaptor;

public class FloatSliderAdapter implements SliderAdapter<Float> {
   private final float min;
   private final float max;
   private final float def;

   public FloatSliderAdapter(float min, float max, float def) {
      this.min = min;
      this.max = max;
      this.def = def;
   }

   public float toSliderPosition(Float value) {
      return (value - this.min) / (this.max - this.min);
   }

   public Float fromSliderPosition(float sliderPos) {
      return this.min + sliderPos * (this.max - this.min);
   }

   public Float clamp(Float value) {
      return Math.max(this.min, Math.min(this.max, value));
   }

   public String format(Float value) {
      return String.format("%.1f", value);
   }

   public Float defaultValue() {
      return this.def;
   }

   public Float getMin() {
      return this.min;
   }

   public Float getMax() {
      return this.max;
   }
}
