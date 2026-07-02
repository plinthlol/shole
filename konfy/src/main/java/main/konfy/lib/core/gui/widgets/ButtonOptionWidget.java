package main.konfy.lib.core.gui.widgets;

import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.utils.MainColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonEvent;

public class ButtonOptionWidget extends OptionWidget {
   private final Option<Runnable> option;
   private boolean hoveredButton;

   public ButtonOptionWidget(OptionGroup parent, KonfyLibConfigScreen screen, int x, int y, int width, int height, Option<Runnable> option) {
      super(parent, screen, option, x, y, width, height, option.getName());
      this.option = option;
      this.hoveredButton = false;
      this.resetButton.visible = false;
   }

   @Override
   public void extract(Graphics graphics, int mouseX, int mouseY, float delta) {
      GuiGraphicsExtractor extractor = graphics.extractor();
      int startX = this.getX() + this.getWidth() - 48;
      this.hoveredButton = mouseX >= startX
         && mouseX <= startX + 38
         && mouseY >= this.getY() + 3
         && mouseY <= this.getY() + 3 + this.getHeight() - 6;
      graphics.fillRoundedRectOutline(startX - 1, this.getY() + 2, 40, this.getHeight() - 4, 2, 1, MainColors.OUTLINE_BLACK.getRGB());
      graphics.fillRoundedRectOutline(
         startX,
         this.getY() + 3,
         38,
         this.getHeight() - 6,
         2,
         1,
         this.hoveredButton ? MainColors.OUTLINE_WHITE_HOVERED.getRGB() : MainColors.OUTLINE_WHITE.getRGB()
      );
      extractor.text(
         this.screen.getFont(), "Press", startX + 1 + (38 - this.screen.getFont().width("Press")) / 2, this.getTextYCentered() + 1, -1, true
      );
   }

   @Override
   public void onMouseClick(MouseButtonEvent click, boolean doubled) {
      if (this.hoveredButton && click.button() == 0) {
         if (this.option.getValue() == null) {
            return;
         }

         this.option.getValue().run();
         AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
      }

      super.onMouseClick(click, doubled);
   }

   @Override
   public void onWidgetUpdate() {
   }

   @Override
   public boolean isHovered() {
      return false;
   }
}
