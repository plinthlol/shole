package main.konfy.lib.core.gui.widgets;

import java.awt.Color;
import java.util.function.Consumer;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.mixin.EditBoxAccessor;
import main.konfy.lib.core.utils.MainColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class SearchBarWidget extends EditBox {
   private final KonfyLibConfigScreen parent;
   private final Consumer<String> searchQuery;

   public SearchBarWidget(KonfyLibConfigScreen parent, int x, int y, int width, int height, Consumer<String> searchQuery) {
      super(Minecraft.getInstance().font, x, y, width, height, Component.empty());
      this.parent = parent;
      this.searchQuery = searchQuery;
      this.setMaxLength((width - 8) / 6);
   }

   private int getFirstCharacterIndex() {
      return ((EditBoxAccessor)this).getDisplayPosition();
   }

   private int getSelectionStart() {
      return Math.min(this.getCursorPosition(), ((EditBoxAccessor)this).getHighlightPos());
   }

   private int getSelectionEnd() {
      return Math.max(this.getCursorPosition(), ((EditBoxAccessor)this).getHighlightPos());
   }

   public void extractWidgetRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float a) {
      if (this.isVisible()) {
         Graphics g = this.parent.currentGraphicsContext();
         g.fillRoundedRect(this.getX() + 1, this.getY() + 1, this.getWidth() - 2, this.getHeight() - 2, 2, MainColors.OUTLINE_BLACK.getRGB());
         int color = this.isHovered ? MainColors.OUTLINE_WHITE_HOVERED.getRGB() : MainColors.OUTLINE_WHITE.getRGB();
         if (this.isFocused()) {
            color = Color.WHITE.getRGB();
         }

         g.fillRoundedRectOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 2, 1, color);
         int textY = (int)((float)this.getY() + (float)(this.height - 9) / 2.0F + 2.0F);

         if (this.getValue().isEmpty() && !this.isFocused()) {
            extractor.text(this.parent.getFont(), "Search...", this.getX() + 6, textY, Color.GRAY.getRGB(), true);
         }

         if (this.isFocused() && this.parent.getUpTime() % 20 < 10) {
            extractor.verticalLine(this.getX() + 6 + this.parent.getFont().width(this.getValue()), textY - 1, textY + 9 + 1, -1);
         }

         extractor.text(this.parent.getFont(), this.getValue(), this.getX() + 6, textY, -1, true);
         Font textRenderer = this.parent.getFont();
         int textX = this.getX() + 6;
         int firstCharIndex = this.getFirstCharacterIndex();
         String visibleText = textRenderer.plainSubstrByWidth(this.getValue().substring(firstCharIndex), this.getInnerWidth());
         int selectionStart = Mth.clamp(this.getSelectionStart(), 0, this.getValue().length());
         int selectionEnd = Mth.clamp(this.getSelectionEnd(), 0, this.getValue().length());
         int visibleSelectionStart = Mth.clamp(selectionStart - firstCharIndex, 0, visibleText.length());
         int visibleSelectionEnd = Mth.clamp(selectionEnd - firstCharIndex, 0, visibleText.length());
         if (visibleSelectionStart != visibleSelectionEnd) {
            int highlightStartX = textX + textRenderer.width(visibleText.substring(0, visibleSelectionStart));
            int highlightEndX = textX + textRenderer.width(visibleText.substring(0, visibleSelectionEnd));
            int highlightTop = textY - 1;
            int highlightBottom = textY + 9;
            this.drawSelectionHighlight(extractor, highlightStartX - 1, highlightTop, highlightEndX - 1, highlightBottom);
         }
      }
   }

   public void onClick(MouseButtonEvent event, boolean doubleClick) {
      if (this.isHovered()) {
         AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
      }

      super.onClick(event, doubleClick);
   }

   public boolean keyPressed(KeyEvent event) {
      boolean result = super.keyPressed(event);
      this.searchQuery.accept(this.getValue().toLowerCase());
      return result;
   }

   public boolean charTyped(CharacterEvent event) {
      boolean result = super.charTyped(event);
      this.searchQuery.accept(this.getValue().toLowerCase());
      return result;
   }

   private void drawSelectionHighlight(GuiGraphicsExtractor extractor, int x1, int y1, int x2, int y2) {
      if (x1 < x2) {
         int i = x1;
         x1 = x2;
         x2 = i;
      }

      if (y1 < y2) {
         int i = y1;
         y1 = y2;
         y2 = i;
      }

      if (x2 > this.getX() + this.width) {
         x2 = this.getX() + this.width;
      }

      if (x1 > this.getX() + this.width) {
         x1 = this.getX() + this.width;
      }

      extractor.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, x1, y1, x2, y2, -16776961);
   }
}
