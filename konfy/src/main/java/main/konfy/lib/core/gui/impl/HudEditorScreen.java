package main.konfy.lib.core.gui.impl;

import java.awt.Color;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.options.type.PixelGrid;
import main.konfy.lib.core.config.local.options.type.PixelGridAnimation;
import main.konfy.lib.core.gui.Graphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;

public class HudEditorScreen extends BaseScreen {
   private final Option<?> hudOption;
   private boolean dragging = false;
   private double dragOffsetX = 0.0;
   private double dragOffsetY = 0.0;

   public HudEditorScreen(Screen parent, Option<?> option) {
      super("HudEditor", parent);
      this.hudOption = option;
   }

   @Override
   protected void extract(Graphics graphics, int mouseX, int mouseY) {
      GuiGraphicsExtractor extractor = graphics.extractor();
      extractor.centeredText(this.font, "Currently Editing: " + this.hudOption.getName(), this.width / 2, 10, -1);
      extractor.centeredText(this.font, "(ESC to leave)", this.width / 2, 20, Color.GRAY.getRGB());
      if (this.hudOption.getValue() instanceof PixelGridAnimation pixelGridAnimation) {
         if (this.minecraft.level == null) {
            extractor.fill(0, 0, this.width, this.height, Color.BLACK.getRGB());

            extractor.fill(this.width / 2 - 1, this.height / 2 - 1, this.width / 2 + 1, this.height / 2 + 1, -1);
         }

         pixelGridAnimation.render(extractor, false);
         if (!this.dragging) {
            Vec2 pos = pixelGridAnimation.getAbsolutePosition();
            float size = pixelGridAnimation.getSize();
            extractor.pose().pushMatrix();
            extractor.pose().scale(size, size);
            graphics.renderGridOutline(pixelGridAnimation.getCurrentFrame(), Math.round(pos.x / size), Math.round(pos.y / size), 1, 0);
            extractor.pose().popMatrix();
         }
      }
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      if (this.hudOption.getValue() instanceof PixelGridAnimation pixelGridAnimation) {
         double var13 = click.x();
         double mouseY = click.y();
         HudEditorScreen.AnimBounds bounds = this.computeBounds(pixelGridAnimation);
         double rawX = bounds.baseX() + pixelGridAnimation.getOffsetX();
         double rawY = bounds.baseY() + pixelGridAnimation.getOffsetY();
         if (var13 >= rawX && var13 <= rawX + bounds.renderedW() && mouseY >= rawY && mouseY <= rawY + bounds.renderedH()) {
            this.dragging = true;
            this.dragOffsetX = var13 - rawX;
            this.dragOffsetY = mouseY - rawY;
            return true;
         } else {
            return super.mouseClicked(click, doubled);
         }
      } else {
         return super.mouseClicked(click, doubled);
      }
   }

   public boolean mouseDragged(MouseButtonEvent click, double oX, double oY) {
      if (this.dragging && this.hudOption.getValue() instanceof PixelGridAnimation pixelGridAnimation) {
         this.applyOffset(pixelGridAnimation, click.x(), click.y());
         return true;
      } else {
         return super.mouseDragged(click, oX, oY);
      }
   }

   public boolean mouseReleased(MouseButtonEvent click) {
      if (!this.dragging) {
         return super.mouseReleased(click);
      } else {
         this.dragging = false;
         if (this.hudOption.getValue() instanceof PixelGridAnimation pixelGridAnimation) {
            this.applyOffset(pixelGridAnimation, click.x(), click.y());
            this.hudOption.setValue(pixelGridAnimation);
            return true;
         } else {
            return super.mouseReleased(click);
         }
      }
   }

   public boolean keyPressed(KeyEvent input) {
      if (this.hudOption.getValue() instanceof PixelGridAnimation pixelGridAnimation) {
         switch (input.key()) {
            case 262:
               pixelGridAnimation.addOffset(0.5, 0.0);
               break;
            case 263:
               pixelGridAnimation.addOffset(-0.5, 0.0);
               break;
            case 264:
               pixelGridAnimation.addOffset(0.0, 0.5);
               break;
            case 265:
               pixelGridAnimation.addOffset(0.0, -0.5);
               break;
            default:
               return super.keyPressed(input);
         }

         this.hudOption.setValue(pixelGridAnimation);
         return true;
      } else {
         return super.keyPressed(input);
      }
   }

   @Override
   public void onClose() {
      if (this.hudOption.getValue() instanceof PixelGridAnimation pixelGridAnimation) {
         this.hudOption.setValue(pixelGridAnimation);
      }

      super.onClose();
   }

   @Override
   protected void extractBlurredBackground(GuiGraphicsExtractor graphics) {
   }

   private void applyOffset(PixelGridAnimation anim, double mouseX, double mouseY) {
      HudEditorScreen.AnimBounds bounds = this.computeBounds(anim);
      double offsetX = Math.round((mouseX - this.dragOffsetX - bounds.baseX()) * 2.0) / 2.0;
      double offsetY = Math.round((mouseY - this.dragOffsetY - bounds.baseY()) * 2.0) / 2.0;
      anim.setOffset(offsetX, offsetY);
   }

   private HudEditorScreen.AnimBounds computeBounds(PixelGridAnimation anim) {
      PixelGrid frame = anim.getCurrentFrame();
      float size = anim.getSize();
      int renderedW = Math.round((frame == null ? 0 : frame.getWidth()) * size);
      int renderedH = Math.round((frame == null ? 0 : frame.getHeight()) * size);
      int baseX = (this.minecraft.getWindow().getGuiScaledWidth() - renderedW) / 2;
      int baseY = (this.minecraft.getWindow().getGuiScaledHeight() - renderedH) / 2;
      return new HudEditorScreen.AnimBounds(baseX, baseY, renderedW, renderedH);
   }

   private record AnimBounds(int baseX, int baseY, int renderedW, int renderedH) {
   }
}
