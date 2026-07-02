package main.konfy.lib.core.gui.popup;

import java.awt.Color;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.utils.MainColors;
import net.minecraft.client.input.MouseButtonEvent;

public abstract class PopUp {
   private static final int DEFAULT_WIDTH = 300;
   private static final int DEFAULT_HEIGHT = 100;
   protected final String subText;
   protected KonfyLibConfigScreen parent;
   public boolean visible;
   public int x;
   public int y;
   public int width;
   public int height;
   protected boolean canClose = true;
   protected boolean loaded = false;

   public PopUp(KonfyLibConfigScreen parent, String subText) {
      this.parent = parent;
      this.subText = subText;
      this.visible = false;
      this.layout(300, 100);
      this.loaded = true;
   }

   public PopUp(KonfyLibConfigScreen parent, String subText, int width, int height) {
      this.parent = parent;
      this.subText = subText;
      this.layout(width, height);
      this.loaded = true;
   }

   public void extract(Graphics graphics, double mouseX, double mouseY, float delta) {
      graphics.fillRoundedRectOutline(
         this.parent.width / 2 - this.width / 2, this.parent.height / 2 - this.height / 2, this.width, this.height, 2, 1, MainColors.OUTLINE_BLACK.getRGB()
      );
      graphics.fillRoundedRectOutline(
         this.parent.width / 2 - this.width / 2 + 1,
         this.parent.height / 2 - this.height / 2 + 1,
         this.width - 2,
         this.height - 2,
         2,
         1,
         MainColors.OUTLINE_WHITE.getRGB()
      );
      graphics.fillRoundedRect(
         this.parent.width / 2 - this.width / 2 + 2, this.parent.height / 2 - this.height / 2 + 2, this.width - 4, this.height - 4, 2, Color.BLACK.getRGB()
      );
   }

   public abstract void onClick(MouseButtonEvent var1, boolean var2);

   public void onScroll(double mouseX, double mouseY, double verticalAmount) {
   }

   public void onMouseRelease(MouseButtonEvent click) {
   }

   public void layout(int requestedWidth, int requestedHeight) {
      int maxWidth = (int)(this.parent.width * 0.98);
      int maxHeight = (int)(this.parent.height * 0.98);
      this.width = Math.min(requestedWidth, maxWidth);
      this.height = Math.min(requestedHeight, maxHeight);
      this.x = this.parent.width / 2 - this.width / 2;
      this.y = this.parent.height / 2 - this.height / 2;
   }

   public void close() {
      this.parent.popUp = null;
      this.onClose();
   }

   protected abstract void onClose();

   public boolean canClose() {
      return this.canClose;
   }

   public void setParentScreen(KonfyLibConfigScreen screen) {
      this.parent = screen;
   }
}
