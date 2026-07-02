package main.konfy.lib.core.gui.widgets;

import java.awt.Color;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.utils.MainColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class EnumOptionWidget<E extends Enum<E>> extends OptionWidget {
   private final Option<E> option;
   private int maxWidth;

   public EnumOptionWidget(OptionGroup parent, KonfyLibConfigScreen screen, int x, int y, int width, int height, Option<E> option) {
      super(parent, screen, option, x, y, width, height, option.getName());
      this.option = option;
      this.recalc();
   }

   @Override
   public void extract(Graphics graphics, int mouseX, int mouseY, float delta) {
      GuiGraphicsExtractor extractor = graphics.extractor();
      int rectX = this.getX() + this.getWidth() - 45 - this.maxWidth;
      int rectY = this.getY() + 3;
      int rectWidth = this.maxWidth + 38;
      int rectHeight = this.getHeight() - 6;
      boolean hovered = this.isHoveringEnum(mouseX, mouseY);
      graphics.fillRoundedRectOutline(
         rectX - 1, rectY - 1, rectWidth + 2, rectHeight + 2, 2, 1, hovered ? MainColors.OUTLINE_BLACK.getRGB() : new Color(0, 0, 0, 100).getRGB()
      );
      graphics.fillRoundedRectOutline(
         rectX, rectY, rectWidth, rectHeight, 2, 1, hovered ? MainColors.OUTLINE_WHITE_HOVERED.getRGB() : MainColors.OUTLINE_WHITE.getRGB()
      );
      Font textRenderer = Minecraft.getInstance().font;
      int textPos = (int)(rectY + rectHeight / 2.0F - 9.0F / 2.0F) + 1;
      extractor.centeredText(textRenderer, this.option.getValue().name(), (int)(rectX + rectWidth / 2.0F), textPos, hovered ? -1 : Color.LIGHT_GRAY.getRGB());
   }

   @Override
   public void onMouseClick(MouseButtonEvent click, boolean doubled) {
      super.onMouseClick(click, doubled);
      if (this.isHoveringEnum(click.x(), click.y())) {
         E[] constants = this.option.getValue().getDeclaringClass().getEnumConstants();
         int currentIndex = this.option.getValue().ordinal();
         int nextIndex;
         if (click.button() == 1) {
            nextIndex = (currentIndex - 1 + constants.length) % constants.length;
         } else {
            nextIndex = (currentIndex + 1) % constants.length;
         }

         this.option.setValue(constants[nextIndex]);
      }
   }

   @Override
   public void onWidgetUpdate() {
   }

   @Override
   public boolean isHovered() {
      return false;
   }

   @SuppressWarnings("unchecked")
   void recalc() {
      int longestWidth = 0;

      for (E constant : (E[]) this.option.getValue().getDeclaringClass().getEnumConstants()) {
         int width = this.screen.getFont().width(constant.name());
         if (width > longestWidth) {
            longestWidth = width;
         }
      }

      this.maxWidth = longestWidth;
   }

   private boolean isHoveringEnum(double mouseX, double mouseY) {
      int rectX = this.getX() + this.getWidth() - 45 - this.maxWidth;
      int rectY = this.getY() + 3;
      int rectWidth = this.maxWidth + 38;
      int rectHeight = this.getHeight() - 6;
      return mouseX >= rectX && mouseX <= rectX + rectWidth && mouseY >= rectY && mouseY <= rectY + rectHeight;
   }
}
