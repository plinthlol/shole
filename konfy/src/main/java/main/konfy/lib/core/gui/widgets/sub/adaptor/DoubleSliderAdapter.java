package main.konfy.lib.core.gui.widgets.sub.adaptor;

public class DoubleSliderAdapter implements SliderAdapter<Double> {
   private final double min;
   private final double max;
   private final double def;

   public DoubleSliderAdapter(double min, double max, double def) {
      this.min = min;
      this.max = max;
      this.def = def;
   }

   public float toSliderPosition(Double value) {
      return (float)((value - this.min) / (this.max - this.min));
   }

   public Double fromSliderPosition(float sliderPos) {
      return this.min + sliderPos * (this.max - this.min);
   }

   public Double clamp(Double value) {
      return Math.max(this.min, Math.min(this.max, value));
   }

   public String format(Double value) {
      return String.format("%.1f", value);
   }

   public Double defaultValue() {
      return this.def;
   }

   public Double getMin() {
      return this.min;
   }

   public Double getMax() {
      return this.max;
   }
}
