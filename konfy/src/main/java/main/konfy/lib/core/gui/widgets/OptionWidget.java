package main.konfy.lib.core.gui.widgets;

import java.awt.Color;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.utils.MainColors;
import main.konfy.lib.core.utils.ScreenGlobals;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public abstract class OptionWidget extends AbstractWidget {
   private final OptionGroup parent;
   private final Option<?> option;
   private boolean isHovered;
   public KonfyLibConfigScreen screen;
   public ButtonWidget resetButton;
   public boolean changesMade;
   public int mouseX;
   public int mouseY;

   public OptionWidget(OptionGroup parent, KonfyLibConfigScreen screen, Option<?> option, int x, int y, int width, int height, String name) {
      super(x, y, width, height, Component.literal(name));
      this.setPosition(x, y);
      this.parent = parent;
      this.option = option;
      this.screen = screen;
      this.changesMade = false;
      this.isHovered = false;
      int size = ScreenGlobals.OPTION_HEIGHT;
      this.resetButton = new ButtonWidget(
         this.getX() + this.getWidth() - size + 15,
         this.getY(),
         size,
         size,
         false,
         Identifier.fromNamespaceAndPath("konfy", "gui/widget/reset.png"),
         this::handleResetButtonClick,
         -1,
         0
      );
      this.resetButton.setEnabled(option.hasChanged());
   }

   protected void extractWidgetRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
      if (this.isVisible()) {
         this.mouseX = mouseX;
         this.mouseY = mouseY;
         this.isHovered = mouseX >= this.getX() && mouseX < this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();
         if (this.isHovered) {
            this.screen.setFocusedOption(this.option);
         }

         int scissorX1 = 0;
         int scissorY1 = 24;
         int scissorX2 = this.screen.width;
         int scissorY2 = this.screen.height - 28;
         if (this.getX() + this.getWidth() >= 0 && this.getX() <= scissorX2 && this.getY() + this.getHeight() >= 24 && this.getY() <= scissorY2) {
            Graphics graphics = this.screen.currentGraphicsContext();
            extractor.enableScissor(0, 24, scissorX2, scissorY2);
            graphics.fillRoundedRectOutline(
               this.getX(),
               this.getY(),
               this.getWidth(),
               this.getHeight(),
               2,
               1,
               this.isHovered() ? MainColors.OUTLINE_WHITE_HOVERED.getRGB() : MainColors.OUTLINE_WHITE.getRGB()
            );
            graphics.fillRoundedRectOutline(
               this.getX() - 1, this.getY() - 1, this.getWidth() + 2, this.getHeight() + 2, 2, 1, MainColors.OUTLINE_BLACK.getRGB()
            );
            extractor.text(this.screen.getFont(), this.option.getName(), this.getX() + 5, this.getTextYCentered() + 1, -1);
            this.resetButton.setEnabled(this.option.hasChanged() && this.isAvailable());
            this.resetButton.extractWidgetRenderState(extractor, mouseX, mouseY, delta);
            extractor.enableScissor(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight() - 1);
            this.extract(graphics, this.isAvailable() ? mouseX : 0, this.isAvailable() ? mouseY : 0, delta);
            extractor.disableScissor();
            if (!this.isAvailable()) {
               graphics.fillRoundedRect(this.getX() - 1, this.getY() - 1, this.getWidth() + 2, this.getHeight() + 2, 2, new Color(0, 0, 0, 180).getRGB());
               if (this.isHovered) {
                  this.screen.setActiveTooltip(this.option.getAvailabilityHelper());
               }
            } else if (this.isHovered && this.option.getDescription() != null && this.option.getDescription().getStringSupplier() != null) {
               this.screen.setActiveTooltip(this.option.getDescription().getStringSupplier().get());
            }

            extractor.disableScissor();
         }
      }
   }

   public void onMouseClick(MouseButtonEvent click, boolean doubled) {
   }

   public void onMouseRelease(MouseButtonEvent click) {
   }

   public void onMouseDrag(MouseButtonEvent click, double offsetX, double offsetY) {
   }

   public void onMouseMove(double mouseX, double mouseY) {
   }

   public void onMouseScroll(double mouseX, double mouseY, double verticalAmount) {
   }

   public void tick() {
   }

   public void onKeyPress(KeyEvent input) {
   }

   public void onCharTyped(CharacterEvent input) {
   }

   public void onWidgetUpdate(int x, int y) {
      this.resetButton.setPosition(x, y);
      this.onWidgetUpdate();
   }

   public abstract void onWidgetUpdate();

   public abstract void extract(Graphics var1, int var2, int var3, float var4);

   public boolean isAvailable() {
      return this.option.isAvailable();
   }

   protected void handleResetButtonClick() {
      this.option.reset();
      this.onThirdPartyChange(this.option.getDefaultValue());
   }

   protected int getTextYCentered() {
      int textHeight = 9;
      return this.getY() + (ScreenGlobals.OPTION_HEIGHT - textHeight) / 2;
   }

   public boolean isHovered() {
      return this.isAvailable() && this.isHovered && this.isVisible();
   }

   public boolean isVisible() {
      return this.parent.isExpanded() && this.option.searched();
   }

   public boolean isInScissor(int scissorX, int scissorY, int scissorWidth, int scissorHeight) {
      return this.getX() + this.getWidth() > scissorX
         && this.getX() < scissorX + scissorWidth
         && this.getY() + this.getHeight() - 2 > scissorY
         && this.getY() < scissorY + scissorHeight;
   }

   public void updateSearchQuery(String searchQuery) {
      this.option.updateSearchQ(searchQuery);
   }

   public void update() {
      this.screen.layoutGroupWidgets();
   }

   public void onChange() {
      this.screen.onChangesMade(this.option);
   }

   public <V> void onThirdPartyChange(V value) {
   }

   public OptionGroup getParent() {
      return this.parent;
   }

   public Option<?> getOption() {
      return this.option;
   }

   protected void updateWidgetNarration(NarrationElementOutput output) {
   }
}
