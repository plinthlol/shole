package main.konfy.lib.core.config.local.options.type;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class PixelGridAnimation implements Tickable {
   private final List<PixelGrid> frames = new ArrayList<>();
   private int currentFrame = 0;
   private int tickCounter = 0;
   private int animationSpeed = 10;
   private double offsetX = 0.0;
   private double offsetY = 0.0;
   private float size = 1.0F;
   private boolean hasPlayedOnce = false;

   public PixelGridAnimation(PixelGrid... grids) {
      List<PixelGrid> grids1 = new ArrayList<>(List.of(grids));
      if (grids1.isEmpty()) {
         grids1.add(PixelGrid.create().build());
      }

      this.frames.addAll(grids1);
   }

   public PixelGridAnimation(List<PixelGrid> grids) {
      this.frames.addAll(grids);
   }

   public PixelGridAnimation(PixelGridAnimation original, PixelGrid replacement, int index) {
      for (int i = 0; i < original.frames.size(); i++) {
         this.frames.add(i == index - 1 ? replacement : original.frames.get(i));
      }

      this.offsetX = original.offsetX;
      this.offsetY = original.offsetY;
      this.size = original.size;
   }

   public static PixelGridAnimation replace(PixelGridAnimation original, PixelGrid replacement, int index) {
      PixelGridAnimation result = new PixelGridAnimation(original, replacement, index);
      result.setCurrentFrame(original.currentFrame);
      result.setAnimationSpeed(original.animationSpeed);
      return result;
   }

   public void resetAnimation() {
      this.currentFrame = 0;
      this.tickCounter = 0;
      this.hasPlayedOnce = false;
   }

   public void render(GuiGraphicsExtractor extractor, boolean blend) {
      Vec2 pos = this.getAbsolutePosition();
      this.render(extractor, pos.x, pos.y, blend);
   }

   public void render(GuiGraphicsExtractor extractor, float x, float y, boolean blend) {
      PixelGrid frame = this.getCurrentFrame();
      if (frame != null) {
         extractor.pose().pushMatrix();
         extractor.pose().scale(this.size, this.size);
         frame.render(extractor, x / this.size, y / this.size, blend);
         extractor.pose().popMatrix();
      }
   }

   @Override
   public void tick() {
      if (!this.frames.isEmpty() && this.animationSpeed > 0) {
         this.tickCounter++;
         int frameDelay = Math.max(1, 21 - this.animationSpeed);
         if (this.tickCounter >= frameDelay) {
            this.tickCounter = 0;
            int prevFrame = this.currentFrame;
            this.currentFrame = (this.currentFrame + 1) % this.frames.size();
            if (this.currentFrame == 0 && prevFrame == this.frames.size() - 1) {
               this.hasPlayedOnce = true;
            }
         }
      }
   }

   public boolean hasPlayedOnce() {
      return this.hasPlayedOnce;
   }

   public void setAnimationSpeed(int speed) {
      this.animationSpeed = Mth.clamp(speed, 1, 100);
   }

   public int getAnimationSpeed() {
      return this.animationSpeed;
   }

   public PixelGrid getCurrentFrame() {
      return this.frames.isEmpty() ? null : this.frames.get(this.currentFrame);
   }

   public List<PixelGrid> getFrames() {
      return this.frames;
   }

   public PixelGrid getFrame(int index) {
      int adjusted = index - 1;
      return adjusted >= 0 && adjusted < this.frames.size() ? this.frames.get(adjusted) : null;
   }

   public void setFrame(int index, PixelGrid grid) {
      this.frames.set(index - 1, grid);
   }

   public void addFrame(PixelGrid grid) {
      this.frames.add(grid);
   }

   public void setCurrentFrame(int index) {
      this.currentFrame = Mth.clamp(index, 0, this.frames.size() - 1);
   }

   public Vec2 getAbsolutePosition() {
      Minecraft client = Minecraft.getInstance();
      int windowWidth = client.getWindow().getGuiScaledWidth();
      int windowHeight = client.getWindow().getGuiScaledHeight();
      PixelGrid frame = this.getCurrentFrame();
      int frameW = frame == null ? 0 : frame.getWidth();
      int frameH = frame == null ? 0 : frame.getHeight();
      int renderedW = Math.round(frameW * this.size);
      int renderedH = Math.round(frameH * this.size);
      int baseX = (windowWidth - renderedW) / 2;
      int baseY = (windowHeight - renderedH) / 2;
      double rawX = baseX + this.offsetX;
      double rawY = baseY + this.offsetY;
      float x = (float)(Math.round(rawX * 2.0) / 2.0);
      float y = (float)(Math.round(rawY * 2.0) / 2.0);
      return new Vec2(x, y);
   }

   public PixelGridAnimation offset(double offsetX, double offsetY) {
      this.offsetX = offsetX;
      this.offsetY = offsetY;
      return this;
   }

   public void setOffset(double offsetX, double offsetY) {
      this.offsetX = offsetX;
      this.offsetY = offsetY;
   }

   public void addOffset(double dx, double dy) {
      this.offsetX += dx;
      this.offsetY += dy;
   }

   public double getOffsetX() {
      return this.offsetX;
   }

   public double getOffsetY() {
      return this.offsetY;
   }

   public PixelGridAnimation animationSpeed(int speed) {
      this.animationSpeed = speed;
      return this;
   }

   public void setSize(float size) {
      this.size = Mth.clamp(size, 0.1F, 10.0F);
   }

   public float getSize() {
      return this.size;
   }

   public PixelGridAnimation copy() {
      List<PixelGrid> copiedFrames = this.frames.stream().map(PixelGrid::copy).toList();
      PixelGridAnimation copy = new PixelGridAnimation(copiedFrames);
      copy.currentFrame = this.currentFrame;
      copy.animationSpeed = this.animationSpeed;
      copy.offsetX = this.offsetX;
      copy.offsetY = this.offsetY;
      copy.size = this.size;
      return copy;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof PixelGridAnimation other) {
         if (this.animationSpeed != other.animationSpeed) {
            return false;
         }

         if (this.offsetX == other.offsetX && this.offsetY == other.offsetY) {
            if (this.size != other.size) {
               return false;
            }

            if (this.frames.size() != other.frames.size()) {
               return false;
            }

            for (int i = 0; i < this.frames.size(); i++) {
               if (!this.frames.get(i).equals(other.frames.get(i))) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
