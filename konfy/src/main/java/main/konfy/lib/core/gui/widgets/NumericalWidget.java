package main.konfy.lib.core.gui.widgets;

import java.awt.Point;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.gui.widgets.sub.SliderSubWidget;
import main.konfy.lib.core.gui.widgets.sub.adaptor.DoubleSliderAdapter;
import main.konfy.lib.core.gui.widgets.sub.adaptor.FloatSliderAdapter;
import main.konfy.lib.core.gui.widgets.sub.adaptor.IntSliderAdapter;
import main.konfy.lib.core.gui.widgets.sub.adaptor.SliderAdapter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class NumericalWidget<T extends Number> extends OptionWidget {
   private final SliderSubWidget<T> slider;
   private final Option<T> option;

   public NumericalWidget(OptionGroup parent, KonfyLibConfigScreen screen, int x, int y, int width, int height, Option<T> option) {
      super(parent, screen, option, x, y, width, height, option.getName());
      this.option = option;
      @SuppressWarnings("unchecked")
      SliderAdapter<T> adapter = null;
      Number value = option.getValue();
      if (value instanceof Integer) {
         adapter = (SliderAdapter<T>) new IntSliderAdapter(option.getMin().intValue(), option.getMax().intValue(), option.getValue().intValue());
      } else if (value instanceof Float) {
         adapter = (SliderAdapter<T>) new FloatSliderAdapter(option.getMin().floatValue(), option.getMax().floatValue(), option.getValue().floatValue());
      } else if (value instanceof Double) {
         adapter = (SliderAdapter<T>) new DoubleSliderAdapter(option.getMin().doubleValue(), option.getMax().doubleValue(), option.getValue().doubleValue());
      }

      this.slider = new SliderSubWidget<>(this.getX() + width - 110, y + 6, 100, height - 12, adapter, option.getValue(), option::setValue, false);
   }

   @Override
   public void extract(Graphics graphics, int mouseX, int mouseY, float delta) {
      GuiGraphicsExtractor extractor = graphics.extractor();
      this.slider.extract(graphics, mouseX, mouseY, delta);
   }

   @Override
   public void onMouseClick(MouseButtonEvent click, boolean doubled) {
      this.slider.onClick(click, doubled);
   }

   @Override
   public void onMouseRelease(MouseButtonEvent click) {
      this.slider.release();
   }

   @Override
   public void onMouseDrag(MouseButtonEvent click, double deltaX, double deltaY) {
      this.slider.onDrag(this.mouseX);
   }

   @Override
   public void onWidgetUpdate() {
      this.slider.setPos(new Point(this.getX() + this.width - 110, this.getY() + 6));
   }

   @Override
   public boolean isHovered() {
      return super.isHovered() && this.slider.isHovered;
   }

   @Override
   public <V> void onThirdPartyChange(V value) {
      super.onThirdPartyChange(value);
      this.slider.setValue(this.option.getValue());
   }
}
