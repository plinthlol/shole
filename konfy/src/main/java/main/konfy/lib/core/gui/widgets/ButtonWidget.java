package main.konfy.lib.core.gui.widgets;

import java.awt.Color;
import main.konfy.lib.core.config.local.options.type.PixelGrid;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.utils.MainColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class ButtonWidget extends AbstractWidget {
   private Runnable action;
   private final Identifier texture;
   private final PixelGrid grid;
   private final boolean background;
   public boolean overrideHover = false;
   public boolean hovered = true;
   public float scrollY = 0.0F;
   private int outlineColor = -1;
   private int hoveredColor = -1;
   private boolean h = false;
   private int textureOffsetX = 1;
   private int textureOffsetY = 1;

   public ButtonWidget(int x, int y, int width, int height, boolean background, String name, @Nullable Runnable action) {
      super(x, y, width, height, Component.literal(name));
      this.action = action;
      this.background = background;
      this.grid = null;
      this.texture = null;
   }

   public ButtonWidget(int x, int y, int width, int height, boolean background, Identifier texture, @Nullable Runnable action) {
      super(x, y, width, height, Component.literal(texture.getPath()));
      this.action = action;
      this.background = background;
      this.grid = null;
      this.texture = texture;
   }

   public ButtonWidget(int x, int y, int width, int height, boolean background, Identifier texture, @Nullable Runnable action, int offsetX, int offsetY) {
      super(x, y, width, height, Component.literal(texture.getPath()));
      this.action = action;
      this.background = background;
      this.grid = null;
      this.texture = texture;
      this.textureOffsetX = offsetX;
      this.textureOffsetY = offsetY;
   }

   public void setOutlineColor(int color, int hovered) {
      this.outlineColor = color;
      this.hoveredColor = hovered;
   }

   protected void extractWidgetRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
      int drawX = this.getX();
      int drawY = this.getY() - (int)this.scrollY;
      this.h = ctx.containsPointInScissor(mouseX, mouseY)
         && mouseX >= this.getX()
         && mouseY >= drawY
         && mouseX < this.getX() + this.width
         && mouseY < drawY + this.height;
      if (this.background) {
         ctx.fill(
            drawX,
            drawY,
            drawX + this.getWidth(),
            drawY + this.getHeight(),
            this.active ? new Color(0, 0, 0, 100).getRGB() : new Color(50, 50, 50, 100).getRGB()
         );
      }

      Graphics g = new Graphics(ctx);
      if (this.outlineColor == -1) {
         g.fillRoundedRectOutline(
            drawX,
            drawY,
            this.width,
            this.height,
            2,
            1,
            !this.active
               ? new Color(180, 180, 180, 50).getRGB()
               : new Color(
                     255,
                     255,
                     255,
                     !this.isHovered() && !this.overrideHover ? MainColors.OUTLINE_WHITE.getAlpha() : MainColors.OUTLINE_WHITE_HOVERED.getAlpha()
                  )
                  .getRGB()
         );
      } else {
         g.fillRoundedRectOutline(
            drawX,
            drawY,
            this.width,
            this.height,
            2,
            1,
            this.active ? (!this.isHovered() && !this.overrideHover ? this.outlineColor : this.hoveredColor) : new Color(180, 180, 180, 50).getRGB()
         );
      }

      g.fillRoundedRectOutline(
         drawX - 1, drawY - 1, this.width + 2, this.height + 2, 2, 1, this.active ? new Color(0, 0, 0, 191).getRGB() : new Color(30, 30, 30, 120).getRGB()
      );
      if (this.texture == null && this.grid == null) {
         String text = this.getMessage().getString();
         int textX = drawX + (this.width - Minecraft.getInstance().font.width(text)) / 2 + 1;
         int textY = drawY + (this.height - 9) / 2 + 1;
         ctx.text(
            Minecraft.getInstance().font,
            text,
            text.equals("-") ? textX - 1 : textX,
            textY,
            this.active ? (!this.isHovered() && !this.overrideHover ? -7829368 : -3355444) : -11184811
         );
      } else if (this.grid == null) {
         ctx.blit(
            RenderPipelines.GUI_TEXTURED,
            this.texture,
            drawX + 3 + this.textureOffsetX,
            drawY + 3 + this.textureOffsetY,
            0.0F,
            0.0F,
            16,
            16,
            16,
            16,
            16,
            16,
            this.active ? -1 : Color.GRAY.getRGB()
         );
      }

      if (this.grid != null) {
         g.renderGridTexture(this.grid, drawX + 3, drawY + 3, 1, 1, false);
      }
   }

   public void onClick(@NonNull MouseButtonEvent click, boolean doubled) {
      super.onClick(click, doubled);
      if (this.isHovered() && this.action != null) {
         this.action.run();
      }
   }

   public boolean isHovered() {
      return this.h && this.hovered;
   }

   protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
   }

   public void setListener(Runnable runnable) {
      this.action = runnable;
   }

   public void setEnabled(boolean bl) {
      this.active = bl;
   }
}
