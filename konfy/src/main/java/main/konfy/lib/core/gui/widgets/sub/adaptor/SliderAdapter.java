package main.konfy.lib.core.gui.widgets.sub.adaptor;

public interface SliderAdapter<T> {
   float toSliderPosition(T var1);

   T fromSliderPosition(float var1);

   T clamp(T var1);

   String format(T var1);

   T defaultValue();

   T getMin();

   T getMax();
}
