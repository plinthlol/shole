package main.konfy.lib.core.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.utils.ScreenGlobals;
import main.konfy.lib.core.utils.SearchUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class OptionGroupWidget extends AbstractWidget {
   private final OptionGroup group;
   private final List<OptionWidget> children = new ArrayList<>();
   private final KonfyLibConfigScreen parent;
   private String searchQuery = "";
   public boolean isHovered;

   public OptionGroupWidget(int x, int y, int width, int height, OptionGroup group, KonfyLibConfigScreen parent) {
      super(x, y, width, height, Component.literal(group.getName()));
      this.parent = parent;
      this.group = group;
      this.isHovered = false;
      int yOff = y + 20;

      for (Option<?> option : group.getOptions()) {
         OptionWidget optionWidget = option.createWidget(group, parent, 15, yOff, ScreenGlobals.OPTION_WIDTH, ScreenGlobals.OPTION_HEIGHT);
         this.children.add(optionWidget);
         yOff += 30;
      }
   }

   protected void extractWidgetRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float a) {
      extractor.enableScissor(0, 24, this.parent.width, this.parent.height - 28);
      Minecraft client = Minecraft.getInstance();
      Font textRenderer = client.font;
      String text = this.group.getName();
      int textWidth = textRenderer.width(text);
      int fontHeight = 9;
      int centerX = this.getX() + textWidth / 2;
      int hoverPadding = 58;
      int hoverHeight = fontHeight + 4;
      this.isHovered = extractor.containsPointInScissor(mouseX, mouseY)
         && mouseY >= this.getY() - 2
         && mouseY < this.getY() - 2 + hoverHeight
         && mouseX >= centerX - (textWidth / 2 + 58)
         && mouseX < centerX + textWidth / 2 + 58;
      int bgColor;
      if (this.group.isExpanded()) {
         bgColor = -1;
      } else if (this.isHovered) {
         bgColor = -2434342;
      } else {
         bgColor = -5592406;
      }

      int midY = this.getY() + fontHeight / 2;
      int textCenterX = this.getX();
      int textStartX = textCenterX - textWidth / 2;
      int textEndX = textCenterX + textWidth / 2;
      Graphics graphics = this.parent.currentGraphicsContext();
      extractor.horizontalLine(textStartX - 50, textStartX - 8, midY - 1, bgColor);
      extractor.horizontalLine(textStartX - 50, textStartX - 8, midY, bgColor);
      graphics.renderMiniArrow(
         textStartX - 50 - 5,
         midY - (this.group.isExpanded() ? 1 : 0),
         1.0F,
         this.group.isExpanded() ? Graphics.ArrowDirection.DOWN : Graphics.ArrowDirection.RIGHT,
         bgColor
      );
      extractor.horizontalLine(textEndX + 5, textEndX + 50, midY - 1, bgColor);
      extractor.horizontalLine(textEndX + 5, textEndX + 50, midY, bgColor);
      graphics.renderMiniArrow(
         textEndX + 50 + 6,
         midY - (this.group.isExpanded() ? 1 : 0),
         1.0F,
         this.group.isExpanded() ? Graphics.ArrowDirection.DOWN : Graphics.ArrowDirection.LEFT,
         bgColor
      );
      if (ScreenGlobals.DEBUG) {
         this.renderDebug(extractor, centerX - (textWidth / 2 + 58), this.getY() - 2, centerX + textWidth / 2 + 58, this.getY() - 2 + hoverHeight);
      }

      extractor.text(textRenderer, text, this.getX() - textWidth / 2, this.getY(), bgColor);
      extractor.disableScissor();
   }

   protected void updateWidgetNarration(NarrationElementOutput output) {
   }

   private void renderDebug(GuiGraphicsExtractor extractor, int x1, int y1, int x2, int y2) {
      extractor.fill(x1, y1, x2, y2, -1426063361);
   }

   public void onMouseClick(MouseButtonEvent click, boolean doubled) {
      if (this.isHovered && click.button() == 0) {
         this.group.toggleExpanded();
         this.parent.layoutGroupWidgets();
         this.updateVisibility();
      }
   }

   public void updateVisibility() {
      boolean expanded = this.group.isExpanded();

      for (OptionWidget child : this.children) {
         child.visible = expanded;
      }
   }

   public boolean searched(boolean shouldLevenshtein) {
      if (this.searchQuery.isEmpty()) {
         return true;
      }

      String[] queryWords = this.searchQuery.toLowerCase().trim().split("\\s+");
      String[] nameWords = this.group.getName().toLowerCase().trim().split("\\s+");

      label37:
      for (String qWord : queryWords) {
         for (String nameWord : nameWords) {
            if (nameWord.contains(qWord) || nameWord.startsWith(qWord) || shouldLevenshtein && SearchUtils.levenshteinDistance(nameWord, qWord) <= 2) {
               continue label37;
            }
         }

         return false;
      }

      return true;
   }

   public void updateSearchQuery(String query) {
      this.searchQuery = query;
   }

   public List<OptionWidget> getChildren() {
      return this.children;
   }

   public OptionGroup getGroup() {
      return this.group;
   }
}
