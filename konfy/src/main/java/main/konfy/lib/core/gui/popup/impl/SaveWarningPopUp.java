package main.konfy.lib.core.gui.popup.impl;

import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.gui.popup.PopUp;
import main.konfy.lib.core.gui.widgets.ButtonWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class SaveWarningPopUp extends PopUp {
   public final ButtonWidget yesButton;
   public final ButtonWidget noButton;
   public final ButtonWidget cancelButton;

   public SaveWarningPopUp(KonfyLibConfigScreen parent, Runnable yesAction, Runnable noAction, Runnable cancelAction) {
      super(parent, "", 220, 70);
      this.cancelButton = new ButtonWidget(this.x + 10, this.y + this.height - 24, 48, 16, false, "Cancel", cancelAction);
      this.noButton = new ButtonWidget(this.x + this.width / 2 - 24, this.y + this.height - 24, 48, 16, false, "No", noAction);
      this.yesButton = new ButtonWidget(this.x + this.width - 58, this.y + this.height - 24, 48, 16, false, "Yes", yesAction);
   }

   @Override
   public void extract(Graphics graphics, double mouseX, double mouseY, float delta) {
      super.extract(graphics, mouseX, mouseY, delta);
      GuiGraphicsExtractor extractor = graphics.extractor();
      
      String text = "Do you wanna save the changes?";
      extractor.centeredText(this.parent.getFont(), text, this.x + this.width / 2, this.y + 16, -1);

      this.yesButton.extractRenderState(extractor, (int)mouseX, (int)mouseY, delta);
      this.noButton.extractRenderState(extractor, (int)mouseX, (int)mouseY, delta);
      this.cancelButton.extractRenderState(extractor, (int)mouseX, (int)mouseY, delta);
   }

   @Override
   public void onClick(MouseButtonEvent click, boolean doubled) {
      this.yesButton.onClick(click, doubled);
      this.noButton.onClick(click, doubled);
      this.cancelButton.onClick(click, doubled);
   }

   @Override
   protected void onClose() {
   }
}
