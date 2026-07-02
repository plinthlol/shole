package main.konfy.lib.core.gui.widgets;

import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.utils.MainColors;
import main.konfy.lib.core.utils.ScreenGlobals;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public abstract class OpenableWidget extends OptionWidget {
   public boolean open = false;
   public int OPEN_HEIGHT;

   public OpenableWidget(OptionGroup parent, KonfyLibConfigScreen screen, Option<?> option, int x, int y, int width, int height, String name, int openedHeight) {
      super(parent, screen, option, x, y, width, height, name);
      this.OPEN_HEIGHT = openedHeight;
   }

   @Override
   protected void extractWidgetRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
      extractor.enableScissor(0, 24, this.screen.width, this.screen.height - 28);
      if (this.isVisible()) {
         Graphics g = this.screen.currentGraphicsContext();
         g.renderMiniArrow(
            this.getX() - 8,
            this.getTextYCentered() + (this.open ? 4 : 5),
            1.0F,
            this.open ? Graphics.ArrowDirection.DOWN : Graphics.ArrowDirection.RIGHT,
            this.isHovered() ? MainColors.OUTLINE_WHITE_HOVERED.getRGB() : MainColors.OUTLINE_WHITE.getRGB()
         );
      }

      extractor.disableScissor();
      super.extractWidgetRenderState(extractor, mouseX, mouseY, delta);
   }

   @Override
   public void extract(Graphics graphics, int mouseX, int mouseY, float delta) {
   }

   @Override
   public void onMouseClick(MouseButtonEvent click, boolean doubled) {
      if (this.isHovered() && click.button() == 0) {
         this.toggleOpen();
      }
   }

   public float getCurrentHeight() {
      return (float)this.height;
   }

   private void toggleOpen() {
      boolean prev = this.open;
      this.open = !this.open;
      this.setHeight(this.open ? this.OPEN_HEIGHT : ScreenGlobals.OPTION_HEIGHT);
      this.update();
      this.onOpen(prev);
   }

   public boolean fullyClosed() {
      return !this.open;
   }

   protected abstract void onOpen(boolean var1);

   @Override
   public void onWidgetUpdate() {
   }
}
