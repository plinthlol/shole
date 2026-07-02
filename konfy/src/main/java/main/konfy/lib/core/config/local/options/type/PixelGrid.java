package main.konfy.lib.core.config.local.options.type;

import java.awt.Point;
import java.util.Arrays;
import java.util.function.Supplier;
import main.konfy.lib.core.gui.Graphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class PixelGrid {
   private final int width;
   private final int height;
   private boolean[][] pixels;

   public PixelGrid(int width, int height, boolean[][] pixels) {
      this.width = width;
      this.height = height;
      this.pixels = new boolean[height][width];

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            this.pixels[y][x] = y < pixels.length && x < pixels[y].length && pixels[y][x];
         }
      }
   }

   public void render(GuiGraphicsExtractor extractor, Supplier<Point> position, boolean blend) {
      if (position != null && (position.get().x != -1 || position.get().y != -1)) {
         Graphics g = new Graphics(extractor);
         g.renderGridTexture(this, position.get().x, position.get().y, 1, 0, blend);
      }
   }

   public void render(GuiGraphicsExtractor extractor, float x, float y, boolean blend) {
      Graphics g = new Graphics(extractor);
      g.renderGridTexture(this, x, y, 1, 0, blend);
   }

   public boolean getPixel(int x, int y) {
      return this.pixels[y][x];
   }

   public void setPixel(int x, int y, boolean val) {
      this.pixels[y][x] = val;
   }

   public void setPixels(boolean[][] pixels) {
      this.pixels = pixels;
   }

   public boolean[][] getPixels() {
      return this.pixels;
   }

   public PixelGrid copy() {
      return new PixelGrid(this.width, this.height, this.pixels);
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public static PixelGrid.Builder create(int width, int height) {
      return new PixelGrid.Builder(width, height);
   }

   public static PixelGrid.Builder create() {
      return create(15, 15);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj instanceof PixelGrid other) {
         return this.width == other.width && this.height == other.height ? Arrays.deepEquals(this.pixels, other.pixels) : false;
      } else {
         return false;
      }
   }

   public static class Builder {
      private final int width;
      private final int height;
      private final boolean[][] pixels;

      public Builder(int width, int height) {
         this.width = width;
         this.height = height;
         this.pixels = new boolean[height][width];
      }

      public PixelGrid.Builder set(int x, int y) {
         if (x >= 0 && x < this.width && y >= 0 && y < this.height) {
            this.pixels[y][x] = true;
         }

         return this;
      }

      public PixelGrid build() {
         return new PixelGrid(this.width, this.height, this.pixels);
      }
   }
}
