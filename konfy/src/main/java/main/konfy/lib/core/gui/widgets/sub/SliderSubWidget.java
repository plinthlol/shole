package main.konfy.lib.core.gui.widgets.sub;

import java.awt.Color;
import java.util.function.Consumer;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.widgets.sub.adaptor.SliderAdapter;
import main.konfy.lib.core.utils.MainColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;

public class SliderSubWidget<T> extends SubWidget {
   private float sliderPosition = 0.0F;
   private boolean dragging = false;
   public boolean isHovered = false;
   private final SliderAdapter<T> adapter;
   private T value;
   private Consumer<T> onChange;
   private final boolean isRight;

   public SliderSubWidget(int x, int y, int width, int height, SliderAdapter<T> adapter, T initialValue, Consumer<T> onChange, boolean right) {
      super(x, y, width, height);
      this.adapter = adapter;
      this.isRight = right;
      this.onChange = onChange;
      this.setValue(initialValue);
   }

   @Override
   public void extract(Graphics graphics, int mouseX, int mouseY, float delta) {
      this.isHovered = mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
      graphics.fillRoundedRect(this.x, this.y, this.width, this.height, 1, new Color(255, 255, 255, 20).getRGB());
      graphics.fillRoundedRectOutline(this.x, this.y, this.width, this.height, 1, 1, MainColors.OUTLINE_BLACK.getRGB());
      int v = this.isHovered ? 220 : 155;
      graphics.fillRoundedRect(
         this.x + this.sliderPosition * (this.width - 10), this.y + (this.height - 10) / 2.0F, 10.0F, 10.0F, 2, new Color(v, v, v, 255).getRGB()
      );
      graphics.extractor()
         .text(
            Minecraft.getInstance().font,
            this.adapter.format(this.value),
            this.isRight ? this.x + this.width + 3 : this.x - Minecraft.getInstance().font.width(this.adapter.format(this.adapter.getMax())) - 3,
            this.y + (this.height - 8) / 2,
            -1
         );
   }

   @Override
   public void onClick(MouseButtonEvent click, boolean doubled) {
      if (this.isHovered) {
         this.dragging = true;
         this.onChange(click.x());
      }
   }

   @Override
   public void onDrag(int mouseX) {
      if (this.dragging) {
         this.onChange(mouseX);
      }
   }

   private void onChange(double mouseX) {
      this.sliderPosition = Mth.clamp((float)(mouseX - this.x) / this.width, 0.0F, 1.0F);
      this.value = this.adapter.fromSliderPosition(this.sliderPosition);
      this.onChange.accept(this.value);
   }

   public void setValue(T value) {
      this.value = this.adapter.clamp(value);
      this.sliderPosition = Mth.clamp(this.adapter.toSliderPosition(this.value), 0.0F, 1.0F);
   }

   public T getValue() {
      return this.value;
   }

   public void release() {
      this.dragging = false;
   }

   public void setOnChange(Consumer<T> onChange) {
      this.onChange = onChange;
   }
}
