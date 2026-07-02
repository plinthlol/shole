package main.konfy.lib.core.utils;

public class Scroller {
   public boolean active = true;
   private double value;
   private final double step;
   private double min = Double.NEGATIVE_INFINITY;
   private double max = Double.POSITIVE_INFINITY;

   public Scroller(double startValue, double step) {
      this.value = startValue;
      this.step = step;
   }

   public void onScroll(double amount) {
      if (this.active) {
         this.value = this.value - amount * this.step;
         if (this.value < this.min) {
            this.value = this.min;
         }

         if (this.value > this.max) {
            this.value = this.max;
         }
      }
   }

   public double getValue() {
      return this.value;
   }

   public void setValue(double value) {
      this.value = Math.max(this.min, Math.min(this.max, value));
   }

   public void setBounds(double min, double max) {
      this.min = min;
      this.max = max;
      if (this.active) {
         this.value = this.clamp(this.value, min, max);
      }
   }

   private double clamp(double val, double min, double max) {
      return Math.max(min, Math.min(max, val));
   }
}
