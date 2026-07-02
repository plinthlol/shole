package main.konfy.lib.core.gui.widgets.sub;

import java.awt.Color;
import java.util.function.Consumer;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.mixin.EditBoxAccessor;
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

public class TextboxSubWidget extends SubWidget {
   private final EditBox field;
   private final KonfyLibConfigScreen parent;
   public boolean hovered = false;
   private boolean centered;
   private int scrollWidth;
   private Runnable onFocusLost = null;

   public TextboxSubWidget(
      KonfyLibConfigScreen parent, int x, int y, int width, int scrollWidth, int height, String defaultV, Consumer<String> onChange, boolean centered
   ) {
      super(x, y, width, height);
      this.parent = parent;
      this.scrollWidth = scrollWidth;
      this.field = new EditBox(Minecraft.getInstance().font, x, y, width, height, Component.literal(defaultV));
      this.field.setResponder(onChange);
      this.field.setValue(defaultV);
      this.field.active = true;
      this.centered = centered;
   }

   public int getScrollOffset() {
      Font tr = Minecraft.getInstance().font;
      int textWidth = tr.width(this.field.getValue());
      return Math.max(0, textWidth - this.scrollWidth);
   }

   @Override
   public void extract(Graphics graphics, int mouseX, int mouseY, float delta) {
      if (this.field.isVisible()) {
         GuiGraphicsExtractor extractor = graphics.extractor();
         this.hovered = mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
         Font tr = Minecraft.getInstance().font;
         String text = this.field.getValue();
         int scrollOffset = this.getScrollOffset();
         int color = !this.hovered && !this.field.isFocused() ? new Color(255, 255, 255, 180).getRGB() : -1;
         if (text.isEmpty() && !this.field.isFocused()) {
            extractor.text(tr, "...", this.x + 3, this.y + 9 / 2, Color.GRAY.getRGB(), true);
         } else {
            EditBoxAccessor accessor = (EditBoxAccessor)this.field;
            if (this.field.isFocused() && this.parent.getUpTime() % 20 < 10) {
               int cursor = Mth.clamp(this.field.getCursorPosition() - accessor.getDisplayPosition(), 0, text.length());
               String visible = tr.plainSubstrByWidth(text.substring(accessor.getDisplayPosition()), this.field.getInnerWidth());
               int caretX = this.x + 4 + tr.width(visible.substring(0, Mth.clamp(cursor, 0, visible.length()))) - 1 - scrollOffset;
               extractor.verticalLine(caretX, this.y + 9 / 2 - 2, this.y + 9 + 4, -1);
            }

            if (this.centered) {
               String trimmed = tr.plainSubstrByWidth(text, this.field.getInnerWidth());
               extractor.text(tr, trimmed, this.x + this.width / 2 - tr.width(trimmed) / 2, this.y + 1 + 9 / 2, color);
            } else {
               extractor.text(tr, text, this.x + 3 - scrollOffset, this.y + 1 + 9 / 2, color, true);
            }

            int textX = this.x + 4 - scrollOffset;
            int textY = this.y + 1 + 9 / 2;
            int firstCharIndex = accessor.getDisplayPosition();
            String visibleText = tr.plainSubstrByWidth(text.substring(firstCharIndex), this.field.getInnerWidth());
            int selectionStart = Mth.clamp(this.getSelectionStart(accessor), 0, text.length());
            int selectionEnd = Mth.clamp(this.getSelectionEnd(accessor), 0, text.length());
            int visibleSelectionStart = Mth.clamp(selectionStart - firstCharIndex, 0, visibleText.length());
            int visibleSelectionEnd = Mth.clamp(selectionEnd - firstCharIndex, 0, visibleText.length());
            if (visibleSelectionStart != visibleSelectionEnd) {
               int highlightStartX = textX + tr.width(visibleText.substring(0, visibleSelectionStart));
               int highlightEndX = textX + tr.width(visibleText.substring(0, visibleSelectionEnd));
               this.drawSelectionHighlight(extractor, highlightStartX - 1, textY - 1, highlightEndX - 1, textY + 9);
            }
         }
      }
   }

   private int getSelectionStart(EditBoxAccessor editBox) {
      return Math.min(this.field.getCursorPosition(), editBox.getHighlightPos());
   }

   private int getSelectionEnd(EditBoxAccessor editBox) {
      return Math.max(this.field.getCursorPosition(), editBox.getHighlightPos());
   }

   @Override
   public void onClick(MouseButtonEvent click, boolean doubled) {
      if (this.hovered) {
         this.field.onClick(click, doubled);
         this.field.moveCursorToEnd(false);
         this.setFocus(true);
         AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
      } else {
         this.setFocus(false);
      }
   }

   @Override
   public void onDrag(int mouseX) {
   }

   @Override
   public void onKeyPress(KeyEvent input) {
      this.field.keyPressed(input);
      super.onKeyPress(input);
   }

   @Override
   public void onCharTyped(CharacterEvent input) {
      this.field.charTyped(input);
      super.onCharTyped(input);
   }

   public void setOnFocusLost(Runnable onFocusLost) {
      this.onFocusLost = onFocusLost;
   }

   public void setFocus(boolean focus) {
      this.field.setFocused(focus);
      if (!focus && this.onFocusLost != null) {
         this.onFocusLost.run();
      }
   }

   public boolean isFocused() {
      return this.field.isFocused();
   }

   public String getText() {
      return this.field.getValue();
   }

   public void setText(String text) {
      this.field.setValue(text);
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

      if (x2 > this.x + this.width) {
         x2 = this.x + this.width;
      }

      if (x1 > this.x + this.width) {
         x1 = this.x + this.width;
      }

      extractor.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, x1, y1, x2, y2, -16776961);
   }
}
