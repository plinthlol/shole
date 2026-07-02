package main.konfy.lib.core.gui.widgets.sub;

import java.awt.Point;
import main.konfy.lib.core.gui.Graphics;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public abstract class SubWidget {
   protected int x;
   protected int y;
   protected int width;
   protected int height;

   public SubWidget(int x, int y, int width, int height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public abstract void extract(Graphics var1, int var2, int var3, float var4);

   public abstract void onClick(MouseButtonEvent var1, boolean var2);

   public abstract void onDrag(int var1);

   public void onKeyPress(KeyEvent input) {
   }

   public void onCharTyped(CharacterEvent input) {
   }

   public void setPos(Point pos) {
      this.x = pos.x;
      this.y = pos.y;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public Point getPos() {
      return new Point(this.x, this.y);
   }

   public int getHeight() {
      return this.height;
   }
}
