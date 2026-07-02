package main.konfy.lib.core.gui.widgets;

import java.awt.Color;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.options.BooleanOption;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.gui.popup.impl.WarningPopUp;
import main.konfy.lib.core.utils.MainColors;
import main.konfy.lib.core.utils.ScreenGlobals;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;

public class BooleanWidget extends OptionWidget {
   private final Option<Boolean> option;
   private final WarningPopUp warningPopUp;
   private int onX;
   private int offX;

   public BooleanWidget(
      OptionGroup parent, KonfyLibConfigScreen screen, int x, int y, int width, int height, Option<Boolean> option, BooleanOption.Warning warning
   ) {
      super(parent, screen, option, x, y, width, height, option.getName());
      this.option = option;
      this.onX = this.getX() + width - 21;
      this.offX = this.getX() + width - 30;
      if (warning != null) {
         this.warningPopUp = new WarningPopUp(screen, warning.title(), warning.message(), () -> {
            option.setValue(!option.getValue());
            this.onChange();
            if (warning.onYes() != null) {
               warning.onYes().run();
            }

            this.screen.popUp.close();
         }, () -> {
            if (warning.onNo() != null) {
               warning.onNo().run();
            }

            this.screen.popUp.close();
         });
      } else {
         this.warningPopUp = null;
      }
   }

   @Override
   public void extract(Graphics graphics, int mouseX, int mouseY, float delta) {
      GuiGraphicsExtractor extractor = graphics.extractor();
      if (ScreenGlobals.DEBUG) {
         this.renderDebug(extractor);
      }

      graphics.fillRoundedRect(this.getX() + this.getWidth() - 31, this.getY() + 3, 25.0F, this.getHeight() - 6, 2, new Color(255, 255, 255, 20).getRGB());
      graphics.fillRoundedRectOutline(this.getX() + this.getWidth() - 31, this.getY() + 3, 25, this.getHeight() - 6, 2, 1, MainColors.OUTLINE_BLACK.getRGB());
      int color = this.option.getValue() ? Color.WHITE.getRGB() : MainColors.OUTLINE_WHITE.getRGB();
      float animX = this.option.getValue() ? this.onX : this.offX;
      graphics.fillRoundedRect(animX, this.getY() + 4, 14.0F, this.getHeight() - 8, 2, color);
   }

   @Override
   public void onMouseClick(MouseButtonEvent click, boolean doubled) {
      if (this.isHovered() && click.button() == 0) {
         if (this.warningPopUp != null && !this.warningPopUp.visible && !this.option.getValue()) {
            this.screen.popUp = this.warningPopUp;
            return;
         }

         this.option.setValue(!this.option.getValue());
         this.onChange();
      }
   }

   @Override
   public void onWidgetUpdate() {
      this.onX = this.getX() + this.width - 21;
      this.offX = this.getX() + this.width - 30;
   }

   public void playDownSound(SoundManager soundManager) {
   }

   private void renderDebug(GuiGraphicsExtractor extractor) {
      extractor.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), new Color(255, 255, 255, 150).getRGB());
   }

   @Override
   public void onChange() {
      super.onChange();
   }

   @Override
   public <V> void onThirdPartyChange(V value) {
      super.onThirdPartyChange(value);
   }
}
