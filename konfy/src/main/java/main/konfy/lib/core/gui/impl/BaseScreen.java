package main.konfy.lib.core.gui.impl;

import main.konfy.lib.core.gui.Graphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BaseScreen extends Screen {
   public final Screen parent;
   private int upTime = 0;
   protected float delta;
   protected boolean suppressWidgetMouse = false;

   protected BaseScreen(String title, Screen parent) {
      super(Component.literal(title));
      this.parent = parent;
   }

   public void onClose() {
      this.minecraft.setScreenAndShow(this.parent);
   }

   public void tick() {
      super.tick();
      this.upTime++;
   }

   public void addWidget(AbstractWidget widget) {
      this.addRenderableWidget(widget);
   }

   private String activeTooltipText = null;

   public void setActiveTooltip(String text) {
      this.activeTooltipText = text;
   }

   public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
      this.activeTooltipText = null;
      this.delta = delta;
      int renderMouseX = this.suppressWidgetMouse ? 0 : mouseX;
      int renderMouseY = this.suppressWidgetMouse ? 0 : mouseY;
      this.suppressWidgetMouse = false;
      super.extractRenderState(extractor, renderMouseX, renderMouseY, delta);
      this.extract(new Graphics(extractor), mouseX, mouseY);
      this.drawCustomTooltip(new Graphics(extractor), mouseX, mouseY);
   }

   private void drawCustomTooltip(Graphics graphics, int mouseX, int mouseY) {
      if (this.activeTooltipText == null || this.activeTooltipText.isEmpty()) {
         return;
      }
      GuiGraphicsExtractor extractor = graphics.extractor();
      java.util.List<net.minecraft.network.chat.FormattedText> lines = new java.util.ArrayList<>();
      for (String part : this.activeTooltipText.split("\n")) {
         lines.addAll(this.font.splitIgnoringLanguage(net.minecraft.network.chat.Component.literal(part), 220));
      }
      int textWidth = lines.stream().mapToInt(this.font::width).max().orElse(0);
      int boxWidth = textWidth + 8;
      int boxHeight = lines.size() * 9 + (lines.size() - 1) * 2 + 8;

      int tooltipX = mouseX + 12;
      int tooltipY = mouseY - 12;
      if (tooltipX + boxWidth > this.width) {
         tooltipX = mouseX - boxWidth - 12;
      }
      if (tooltipY + boxHeight > this.height) {
         tooltipY = this.height - boxHeight - 4;
      }
      if (tooltipY < 4) {
         tooltipY = 4;
      }

      graphics.fillRoundedRect(tooltipX, tooltipY, boxWidth, boxHeight, 2, new java.awt.Color(0, 0, 0, 180).getRGB());
      graphics.fillRoundedRectOutline(tooltipX, tooltipY, boxWidth, boxHeight, 2, 1, new java.awt.Color(255, 255, 255, 40).getRGB());

      int currentY = tooltipY + 4;
      for (net.minecraft.network.chat.FormattedText line : lines) {
         extractor.text(this.font, line.getString(), tooltipX + 4, currentY, new java.awt.Color(220, 220, 220).getRGB(), true);
         currentY += 9 + 2;
      }
   }

   protected void extract(Graphics graphics, int mouseX, int mouseY) {
   }

   protected void extractBlurredBackground(GuiGraphicsExtractor graphics) {
      graphics.blurBeforeThisStratum();
   }

   public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
      if (this.isInGameUi()) {
         this.extractTransparentBackground(graphics);
      } else {
         if (this.minecraft.level == null) {
            this.extractPanorama(graphics, a);
         }

         this.extractBlurredBackground(graphics);
      }

      this.minecraft.gui.extractDeferredSubtitles();
   }

   public int getUpTime() {
      return this.upTime;
   }
}
