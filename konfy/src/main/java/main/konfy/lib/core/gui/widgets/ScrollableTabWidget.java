package main.konfy.lib.core.gui.widgets;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.gui.utils.CategoryTab;
import main.konfy.lib.core.gui.utils.TabLocation;
import main.konfy.lib.core.mixin.ScreenAccessor;
import main.konfy.lib.core.utils.MainColors;
import main.konfy.lib.core.utils.MarqueeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class ScrollableTabWidget extends AbstractWidget {
   public static final Identifier ARROW = Identifier.fromNamespaceAndPath("konfy", "gui/arrow.png");
   private List<CategoryTab> tabs;
   private final TabManager tabManager;
   private final TabLocation location;
   private final KonfyLibConfigScreen parent;
   private static final int TAB_WIDTH = 100;
   private static final int TAB_HEIGHT = 20;
   private float currentScrollOffset = 0.0F;
   private float targetScrollOffset = 0.0F;

   public ScrollableTabWidget(
      int x, int y, int width, int height, List<CategoryTab> tabs, TabManager tabManager, TabLocation location, KonfyLibConfigScreen parent
   ) {
      super(x, y, width, height, Component.empty());
      this.tabs = tabs;
      this.tabManager = tabManager;
      this.location = location;
      this.parent = parent;
   }

   protected void extractWidgetRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float a) {
      Minecraft client = Minecraft.getInstance();
      this.currentScrollOffset = Mth.lerp(0.2F, this.currentScrollOffset, this.targetScrollOffset);
      if (this.location == TabLocation.TOP || this.location == TabLocation.BOTTOM) {
         if (this.tabs.isEmpty()) {
            return;
         }

         ctx.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
         int tabCount = this.tabs.size();
         int totalWidth = tabCount * 104 - 4;
         int baseX = totalWidth <= this.width ? this.getX() + (this.width - totalWidth) / 2 : this.getX() - (int)this.currentScrollOffset;
         int[] tabXs = new int[tabCount];
         boolean[] hoveredTabs = new boolean[tabCount];
         boolean[] selectedTabs = new boolean[tabCount];

         for (int i = 0; i < tabCount; i++) {
            int tabX = baseX + i * 104;
            tabXs[i] = tabX;
            boolean hovered = mouseX >= tabX && mouseX <= tabX + 100 && mouseY >= this.getY() && mouseY <= this.getY() + 20;
            hoveredTabs[i] = hovered;
            boolean selected = this.tabs.get(i).equals(this.tabManager.getCurrentTab());
            selectedTabs[i] = selected;
         }

         for (int i = 0; i < tabCount; i++) {
            int tabX = tabXs[i];
            if (tabX + 100 >= this.getX() && tabX <= this.getX() + this.width) {
               String fullText = this.tabs.get(i).getTabTitle().getString();
               String text = MarqueeUtil.get(fullText, 90, 10);
               int color = selectedTabs[i] ? -1 : (hoveredTabs[i] ? -3355444 : -7829368);
               ctx.text(client.font, text, tabX + (100 - client.font.width(text)) / 2, this.getY() + 6, color, true);
            }
         }

         int y = this.getY() + 20 - 1;
         int startX = baseX;
         int endX = baseX + totalWidth;
         ctx.horizontalLine(startX, endX, y, MainColors.OUTLINE_WHITE.getRGB());
         ctx.horizontalLine(startX - 2, endX + 2, y + 1, MainColors.OUTLINE_BLACK.getRGB());
         int leftAlpha;
         if (selectedTabs[0]) {
            leftAlpha = 255;
         } else if (hoveredTabs[0]) {
            leftAlpha = 204;
         } else {
            leftAlpha = 51;
         }

         int rightAlpha;
         if (selectedTabs[tabCount - 1]) {
            rightAlpha = 255;
         } else if (hoveredTabs[tabCount - 1]) {
            rightAlpha = 204;
         } else {
            rightAlpha = 51;
         }

         ctx.verticalLine(startX - 1, y + 1, y - 20, new Color(255, 255, 255, leftAlpha).getRGB());
         ctx.verticalLine(startX - 2, y + 1, y - 20 - 1, new Color(0, 0, 0, 191).getRGB());
         if ((tabCount - 1) * 100 <= this.parent.width) {
            ctx.horizontalLine(0, startX - 3, y + 1, new Color(0, 0, 0, 191).getRGB());
         }

         ctx.verticalLine(endX + 1, y + 1, y - 20, new Color(255, 255, 255, rightAlpha).getRGB());
         ctx.verticalLine(endX + 2, y + 1, y - 20 - 1, new Color(0, 0, 0, 191).getRGB());
         ctx.horizontalLine(this.getWidth(), endX + 3, y + 1, new Color(0, 0, 0, 191).getRGB());

         for (int i = 0; i < tabCount; i++) {
            int tabX = tabXs[i];
            if (tabX + 100 >= this.getX() && tabX <= this.getX() + this.width && (selectedTabs[i] || hoveredTabs[i])) {
               int fillRight = tabX + 100;
               if (i == tabCount - 1) {
                  fillRight++;
               }

               ctx.fill(tabX, this.getY() + 20 - 1, fillRight, this.getY() + 20, selectedTabs[i] ? -1 : -3355444);
            }
         }

         this.renderArrowIndicator(ctx);
         ctx.disableScissor();
      }
   }

   protected void updateWidgetNarration(NarrationElementOutput output) {
   }

   private void renderArrowIndicator(GuiGraphicsExtractor ctx) {
      ScrollableTabWidget.MaxOffset offset = this.getMaxOffsetDirection();
      if (offset != null) {
         ctx.pose().pushMatrix();
         double offsetX = 0.0;
         int fadeAlpha = (int)((Math.sin(this.parent.getUpTime() % 60 / 60.0 * 2.0 * Math.PI) * 0.5 + 0.5) * 255.0);
         fadeAlpha = Math.min(Math.max(fadeAlpha, 0), 255);
         if (offset != ScrollableTabWidget.MaxOffset.RIGHT) {
            ctx.pose().pushMatrix();
            ctx.pose().translate((float)(this.getX() + this.getWidth() - 25 + 0.0), (float)((this.getY() + this.getHeight()) / 2.0 + 2.5));
            ctx.pose().scale(2.0F, 2.0F);
            ctx.pose().translate(1.0F, 1.0F);
            ctx.blit(RenderPipelines.GUI_TEXTURED, ARROW, -1, -1, 0.0F, 0.0F, 8, 8, 8, 16, new Color(255, 255, 255, fadeAlpha).getRGB());
            ctx.pose().popMatrix();
         }

         if (offset != ScrollableTabWidget.MaxOffset.LEFT) {
            ctx.pose().pushMatrix();
            ctx.pose().translate((float)(this.getX() + 25 - 0.0), (float)((this.getY() + this.getHeight()) / 2.0 + 2.5));
            ctx.pose().scale(2.0F, 2.0F);
            ctx.pose().translate(-9.0F, 1.0F);
            ctx.blit(RenderPipelines.GUI_TEXTURED, ARROW, 1, -1, 0.0F, 8.0F, 8, 8, 8, 16, new Color(255, 255, 255, fadeAlpha).getRGB());
            ctx.pose().popMatrix();
         }

         ctx.pose().popMatrix();
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.location != TabLocation.TOP && this.location != TabLocation.BOTTOM) {
         return false;
      }

      int totalWidth = this.tabs.size() * 104 - 4;
      if (totalWidth <= this.width) {
         return false;
      }

      this.targetScrollOffset -= (float)(verticalAmount * 20.0);
      this.targetScrollOffset = Math.max(0.0F, Math.min(this.targetScrollOffset, totalWidth - this.width));
      return true;
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      double mouseX = click.x();
      double mouseY = click.y();
      if (this.location == TabLocation.TOP || this.location == TabLocation.BOTTOM) {
         for (int i = 0; i < this.tabs.size(); i++) {
            int tabX = (
                  this.tabs.size() * 104 - 4 <= this.width
                     ? this.getX() + (this.width - (this.tabs.size() * 104 - 4)) / 2
                     : this.getX() - (int)this.currentScrollOffset
               )
               + i * 104;
            if (mouseX >= tabX && mouseX <= tabX + 100 && mouseY >= this.getY() && mouseY <= this.getY() + 20) {
               if (this.tabManager.getCurrentTab() != this.tabs.get(i)) {
                  this.selectTab(i, true);
               }

               return true;
            }
         }
      }

      return false;
   }

   public void updateVisibleWidgetsForTab(CategoryTab tab) {
      this.parent.children().removeIf(w -> w instanceof OptionGroupWidget || w instanceof OptionWidget);
      ((ScreenAccessor)this.parent).getDrawables().removeIf(w -> w instanceof OptionGroupWidget || w instanceof OptionWidget);

      for (OptionGroupWidget groupWidget : tab.getOptionGroupWidgets()) {
         this.parent.addWidget(groupWidget);

         for (OptionWidget optionWidget : groupWidget.getChildren()) {
            this.parent.addWidget(optionWidget);
         }
      }

      this.parent.layoutGroupWidgets();
   }

   public void setTabs(List<CategoryTab> tabs) {
      this.tabs = new ArrayList<>(tabs);
   }

   public int tabSize() {
      return this.tabs.size();
   }

   public ScrollableTabWidget.MaxOffset getMaxOffsetDirection() {
      int totalWidth = this.tabs.size() * 104 - 4;
      if (totalWidth <= this.width) {
         return null;
      } else if (this.targetScrollOffset <= 0.0F) {
         return ScrollableTabWidget.MaxOffset.LEFT;
      } else {
         return this.targetScrollOffset >= totalWidth - this.width ? ScrollableTabWidget.MaxOffset.RIGHT : ScrollableTabWidget.MaxOffset.MID;
      }
   }

   public void selectTab(int index, boolean bl) {
      if (index >= 0 && index < this.tabs.size()) {
         this.tabManager.setCurrentTab((Tab)this.tabs.get(index), true);
         if (bl) {
            this.parent.showWidgetsForCategory(this.tabs.get(index).getCategory());
         }

         this.parent.setFocusedOption(null);
      }
   }

   public boolean isHoveringOverAnyTab(double mouseX, double mouseY) {
      if ((this.location == TabLocation.TOP || this.location == TabLocation.BOTTOM) && !this.tabs.isEmpty()) {
         int totalWidth = this.tabs.size() * 104 - 4;
         int baseX = totalWidth <= this.width ? this.getX() + (this.width - totalWidth) / 2 : this.getX() - (int)this.currentScrollOffset;

         for (int i = 0; i < this.tabs.size(); i++) {
            int tabX = baseX + i * 104;
            if (mouseX >= tabX && mouseX <= tabX + 100 && mouseY >= this.getY() && mouseY <= this.getY() + 20) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public enum MaxOffset {
      LEFT,
      RIGHT,
      MID;
   }
}
