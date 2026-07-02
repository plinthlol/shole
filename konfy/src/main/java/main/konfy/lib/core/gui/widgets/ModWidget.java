package main.konfy.lib.core.gui.widgets;

import java.awt.Color;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.BaseScreen;
import main.konfy.lib.core.gui.impl.ConflictedConfigScreen;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.mods.Mod;
import main.konfy.lib.core.utils.MainColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

public class ModWidget extends AbstractWidget {
   private final Mod mod;
   private final KonfyLibConfigScreen parent;

   public ModWidget(Mod mod, KonfyLibConfigScreen parent, int x, int y) {
      super(x, y, 140, 34, Component.empty());
      this.mod = mod;
      this.parent = parent;
   }

   protected void extractWidgetRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float a) {
      boolean bl = this.mod.hasConfig();
      boolean bl2 = this.mod.getOverridableConfigScreen(this.parent) != null;
      int outlineColor;
      if (!bl && !bl2) {
         outlineColor = new Color(220, 60, 60, 180).getRGB();
      } else {
         outlineColor = this.isHovered() ? MainColors.OUTLINE_WHITE_HOVERED.getRGB() : MainColors.OUTLINE_WHITE.getRGB();
      }

      Graphics graphics = new Graphics(extractor);
      graphics.fillRoundedRectOutline_ModWidget(this.getX(), this.getY(), this.width, this.height, 2, 1, outlineColor);
      graphics.fillRoundedRectOutline_ModWidget(
         this.getX() - 1,
         this.getY() - 1,
         this.width + 2,
         this.height + 2,
         2,
         1,
         this.active ? new Color(0, 0, 0, 191).getRGB() : new Color(30, 30, 30, 120).getRGB()
      );
      extractor.text(
         Minecraft.getInstance().font, this.mod.getContainer().getMetadata().getName(), this.getX() + 38, this.getY() + this.height / 2 - 9 / 2, -1, true
      );
      extractor.blit(RenderPipelines.GUI_TEXTURED, this.mod.getModIcon(), this.getX() + 1, this.getY() + 1, 0.0F, 0.0F, 32, 32, 32, 32);
      if (this.isHovered()) {
         String modDescription = this.mod.getContainer().getMetadata().getDescription();
         String tooltipText;
         if (bl || bl2) {
            tooltipText = modDescription;
         } else if (modDescription != null && !modDescription.isEmpty()) {
            tooltipText = modDescription + "\nNo config screen";
         } else {
            tooltipText = "\nNo config screen";
         }

         if (tooltipText != null && !tooltipText.isEmpty()) {
            this.parent.setActiveTooltip(tooltipText);
         }
      }
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      BaseScreen overridable = this.mod.getOverridableConfigScreen(this.parent);
      boolean hasConfig = this.mod.hasConfig();
      if (this.isHovered() && (hasConfig || overridable != null)) {
         Screen currentParent = this.parent;

         while (currentParent instanceof KonfyLibConfigScreen) {
            currentParent = ((KonfyLibConfigScreen)currentParent).parent;
         }

         Screen nextScreen;
         if (overridable != null && hasConfig) {
            KonfyLibConfigScreen configScreen = new KonfyLibConfigScreen(currentParent, this.mod.getConfig(), this.mod.getContainer().getMetadata().getName());
            nextScreen = new ConflictedConfigScreen("Choose Screen", currentParent, overridable, configScreen, this.mod.getConflictedButtonTitles());
         } else if (hasConfig) {
            nextScreen = new KonfyLibConfigScreen(currentParent, this.mod.getConfig(), this.mod.getContainer().getMetadata().getName());
         } else {
            nextScreen = overridable;
         }

         Minecraft.getInstance().setScreenAndShow(nextScreen);
         return super.mouseClicked(click, doubled);
      } else {
         return super.mouseClicked(click, doubled);
      }
   }

   public void playDownSound(SoundManager soundManager) {
   }

   protected void updateWidgetNarration(NarrationElementOutput output) {
   }
}
