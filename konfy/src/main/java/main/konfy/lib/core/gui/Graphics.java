package main.konfy.lib.core.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.awt.Color;
import main.konfy.lib.core.config.local.options.type.PixelGrid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.lwjgl.opengl.GL11;

public record Graphics(GuiGraphicsExtractor extractor) {
   public void drawRoundedTexture(
      RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, int radius, int textureWidth, int textureHeight
   ) {
      TextureManager manager = Minecraft.getInstance().getTextureManager();
      manager.getTexture(sprite);
      AbstractTexture texture = manager.getTexture(sprite);
      TextureSetup textureSetup = TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler());
      ScreenRectangle current = this.extractor.scissorStack.peek();
      this.extractor
         .guiRenderState
         .addGuiElement(
            new Graphics.RoundedTextureGuiElementRenderState(
               pipeline, textureSetup, this.extractor.pose(), sprite, x, y, x + width, y + height, radius, textureWidth, textureHeight, current
            )
         );
   }

   public void renderMiniArrow(float x, float y, float scale, Graphics.ArrowDirection direction, int color) {
      int[][] lines = new int[][]{{-3, 0, 3, 1}, {-2, 1, 2, 2}, {-1, 2, 1, 3}};

      for (int[] line : lines) {
         float x1 = line[0] * scale;
         float y1 = line[1] * scale;
         float x2 = line[2] * scale;
         float y2 = line[3] * scale;
         switch (direction) {
            case UP:
               this.extractor.fill((int)(x + x1), (int)(y - y2), (int)(x + x2), (int)(y - y1), color);
               break;
            case DOWN:
               this.extractor.fill((int)(x + x1), (int)(y + y1), (int)(x + x2), (int)(y + y2), color);
               break;
            case LEFT:
               this.extractor.fill((int)(x - y2), (int)(y + x1), (int)(x - y1), (int)(y + x2), color);
               break;
            case RIGHT:
               this.extractor.fill((int)(x + y1), (int)(y + x1), (int)(x + y2), (int)(y + x2), color);
         }
      }
   }

   public void verticalLine(float x, float y1, float y2, int color) {
      if (y2 < y1) {
         float i = y1;
         y1 = y2;
         y2 = i;
      }

      this.fill(x, y1 + 1.0F, (float)(x + 0.8), y2, color);
   }

   public void fillRoundedRectGradient(int x, int y, int width, int height, int radius, int colorTop, int colorBottom) {
      this.extractor
         .guiRenderState
         .addGuiElement(
            new Graphics.RoundedGradientRectGuiElementRenderState(
               RenderPipelines.GUI,
               TextureSetup.noTexture(),
               this.extractor.pose(),
               x,
               y,
               x + width,
               y + height,
               radius,
               colorTop,
               colorBottom,
               this.extractor.scissorStack.peek()
            )
         );
   }

   public void fillRoundedRect(float x, float y, float width, float height, int radius, int color) {
      float right = x + width;
      float bottom = y + height;
      this.fill(x + radius + 1.0F, y, right - radius - 1.0F, y + radius, color);
      this.fill(x + radius + 1.0F, bottom - radius, right - radius - 1.0F, bottom, color);
      this.fill(x, y + radius + 1.0F, x + radius, bottom - radius - 1.0F, color);
      this.fill(right - radius, y + radius + 1.0F, right, bottom - radius - 1.0F, color);
      this.fill(x + radius + 1.0F, y + radius, right - radius - 1.0F, bottom - radius, color);
      this.fill(x + radius, y + radius + 1.0F, x + radius + 1.0F, bottom - radius - 1.0F, color);
      this.fill(right - radius - 1.0F, y + radius + 1.0F, right - radius, bottom - radius - 1.0F, color);
      this.fillCircleQuarter(x + radius, y + radius, radius, color, Graphics.Corner.TOP_LEFT);
      this.fillCircleQuarter(right - radius - 1.0F, y + radius, radius, color, Graphics.Corner.TOP_RIGHT);
      this.fillCircleQuarter(x + radius, bottom - radius - 1.0F, radius, color, Graphics.Corner.BOTTOM_LEFT);
      this.fillCircleQuarter(right - radius - 1.0F, bottom - radius - 1.0F, radius, color, Graphics.Corner.BOTTOM_RIGHT);
   }

   public void fillRoundedRectOutline(int x, int y, int width, int height, int radius, int thickness, int color) {
      int right = x + width;
      int bottom = y + height;
      this.extractor.fill(x + radius + 1, y, right - radius - 1, y + thickness, color);
      this.extractor.fill(x + radius + 1, bottom - thickness, right - radius - 1, bottom, color);
      this.extractor.fill(x, y + radius + 1, x + thickness, bottom - radius - 1, color);
      this.extractor.fill(right - thickness, y + radius + 1, right, bottom - radius - 1, color);
      this.drawCircleQuarterOutline(x + radius, y + radius, radius, thickness, color, Graphics.Corner.TOP_LEFT);
      this.drawCircleQuarterOutline(right - radius - 1, y + radius, radius, thickness, color, Graphics.Corner.TOP_RIGHT);
      this.drawCircleQuarterOutline(x + radius, bottom - radius - 1, radius, thickness, color, Graphics.Corner.BOTTOM_LEFT);
      this.drawCircleQuarterOutline(right - radius - 1, bottom - radius - 1, radius, thickness, color, Graphics.Corner.BOTTOM_RIGHT);
   }

   public void fillRoundedRectOutline_ModWidget(int x, int y, int width, int height, int radius, int thickness, int color) {
      int right = x + width;
      int bottom = y + height;
      this.extractor.fill(x + 1, y, right - radius - 1, y + thickness, color);
      this.extractor.fill(x + 1, bottom - thickness, right - radius - 1, bottom, color);
      this.extractor.fill(x, y, x + thickness, bottom, color);
      this.extractor.fill(right - thickness, y + radius + 1, right, bottom - radius - 1, color);
      this.drawCircleQuarterOutline(right - radius - 1, y + radius, radius, thickness, color, Graphics.Corner.TOP_RIGHT);
      this.drawCircleQuarterOutline(right - radius - 1, bottom - radius - 1, radius, thickness, color, Graphics.Corner.BOTTOM_RIGHT);
   }

   public void drawHueSaturationValueBox(int x, int y, int width, int height, int radius, float hue) {
      this.extractor
         .guiRenderState
         .addGuiElement(
            new Graphics.SaturationBoxGuiElementRenderState(
               RenderPipelines.GUI,
               TextureSetup.noTexture(),
               new Matrix3x2f(this.extractor.pose()),
               x,
               y,
               x + width,
               y + height,
               radius,
               hue,
               this.extractor.scissorStack.peek()
            )
         );
   }

   public void drawRoundedHueSlider(int x, int y, int width, int height, int radius) {
      this.extractor
         .guiRenderState
         .addGuiElement(
            new Graphics.HueSliderGuiElementRenderState(
               RenderPipelines.GUI,
               TextureSetup.noTexture(),
               new Matrix3x2f(this.extractor.pose()),
               x,
               y,
               x + width,
               y + height,
               radius,
               this.extractor.scissorStack.peek()
            )
         );
   }

   public void fillCircleQuarter(float centerX, float centerY, int radius, int color, Graphics.Corner corner) {
      this.extractor
         .guiRenderState
         .addGuiElement(
            new Graphics.CircleQuarterRenderState(
               RenderPipelines.GUI,
               TextureSetup.noTexture(),
               this.extractor.pose(),
               centerX,
               centerY,
               radius,
               color,
               corner,
               this.extractor.scissorStack.peek()
            )
         );
   }

   private void drawCircleQuarterOutline(int centerX, int centerY, int radius, int thickness, int color, Graphics.Corner corner) {
      this.extractor
         .guiRenderState
         .addGuiElement(
            new Graphics.CircleQuarterOutlineRenderState(
               RenderPipelines.GUI,
               TextureSetup.noTexture(),
               this.extractor.pose(),
               centerX,
               centerY,
               radius,
               thickness,
               color,
               corner,
               this.extractor.scissorStack.peek()
            )
         );
   }

   public void renderGridTexture(PixelGrid grid, float x1, float y1, int pixelSize, int gapSize, boolean blend) {
      for (int y = 0; y < grid.getHeight(); y++) {
         for (int x = 0; x < grid.getWidth(); x++) {
            if (grid.getPixel(x, y)) {
               float px = x1 + x * (pixelSize + gapSize);
               float py = y1 + y * (pixelSize + gapSize);
               this.drawFilledRectangle(px, py, px + pixelSize, py + pixelSize, Color.WHITE, blend);
            }
         }
      }
   }

   public void drawFilledRectangle(float x1, float y1, float x2, float y2, Color color, boolean blend) {
      ScreenRectangle current = this.extractor.scissorStack.peek();
      this.extractor.pose().pushMatrix();
      this.setGlProperty(2848, false);
      this.extractor
         .guiRenderState
         .addGuiElement(
            new Graphics.ColoredFloatQuadGuiElementRenderState(
               blend ? RenderPipelines.GUI_INVERT : RenderPipelines.GUI,
               TextureSetup.noTexture(),
               new Matrix3x2f(this.extractor.pose()),
               x1,
               y1,
               x2,
               y2,
               color.getRGB(),
               color.getRGB(),
               current
            )
         );
      this.extractor.pose().popMatrix();
   }

   private void setGlProperty(int property, boolean isEnabled) {
      if (isEnabled) {
         GL11.glEnable(property);
      } else {
         GL11.glDisable(property);
      }
   }

   public void renderGridOutline(PixelGrid grid, int x1, int y1, int pixelSize, int gapSize) {
      int gridWidthPixels = grid.getWidth() * pixelSize + (grid.getWidth() - 1) * gapSize;
      int gridHeightPixels = grid.getHeight() * pixelSize + (grid.getHeight() - 1) * gapSize;
      int x2 = x1 + gridWidthPixels;
      int y2 = y1 + gridHeightPixels;
      float borderWidth = 0.3F;
      int blue = new Color(0, 100, 255).getRGB();
      this.fill(x1 - 0.3F, y1 - 0.3F, x2 + 0.3F, y1, blue);
      this.fill(x1 - 0.3F, y2, x2 + 0.3F, y2 + 0.3F, blue);
      this.fill(x1 - 0.3F, y1, x1, y2, blue);
      this.fill(x2, y1, x2 + 0.3F, y2, blue);
   }

   public void renderGridOutline(PixelGrid grid, int x1, int y1, int pixelSize, int gapSize, int outlineColor, boolean markCenter) {
      if (markCenter) {
         int px = x1 + grid.getWidth() / 2 * (pixelSize + gapSize);
         int py = y1 + grid.getHeight() / 2 * (pixelSize + gapSize);
         this.extractor.fill(px + 1, py + 1, px + pixelSize - 1, py + pixelSize - 1, new Color(255, 100, 100, 100).getRGB());
      }

      for (int y = 0; y < grid.getHeight(); y++) {
         for (int x = 0; x < grid.getWidth(); x++) {
            int px = x1 + x * (pixelSize + gapSize);
            int py = y1 + y * (pixelSize + gapSize);
            if (grid.getPixel(x, y)) {
               this.extractor.fill(px + 1, py + 1, px + pixelSize - 1, py + pixelSize - 1, Color.WHITE.getRGB());
            }

            this.extractor.fill(px + 1, py, px + pixelSize - 1, py + 1, outlineColor);
            this.extractor.fill(px + 1, py + pixelSize - 1, px + pixelSize - 1, py + pixelSize, outlineColor);
            this.extractor.fill(px, py, px + 1, py + pixelSize, outlineColor);
            this.extractor.fill(px + pixelSize - 1, py, px + pixelSize, py + pixelSize, outlineColor);
         }
      }
   }

   public void fill(float x1, float y1, float x2, float y2, int color) {
      ScreenRectangle current = this.extractor.scissorStack.peek();
      if (x1 < x2) {
         float i = x1;
         x1 = x2;
         x2 = i;
      }

      if (y1 < y2) {
         float i = y1;
         y1 = y2;
         y2 = i;
      }

      this.extractor
         .guiRenderState
         .addGuiElement(
            new Graphics.ColoredFloatQuadGuiElementRenderState(
               RenderPipelines.GUI, TextureSetup.noTexture(), new Matrix3x2f(this.extractor.pose()), x1, y1, x2, y2, color, color, current
            )
         );
   }

   public enum ArrowDirection {
      UP,
      DOWN,
      LEFT,
      RIGHT;
   }

   @Environment(EnvType.CLIENT)
   public record CircleQuarterOutlineRenderState(
      RenderPipeline pipeline,
      TextureSetup textureSetup,
      Matrix3x2f pose,
      float centerX,
      float centerY,
      int radius,
      int thickness,
      int color,
      Graphics.Corner corner,
      @Nullable ScreenRectangle scissorArea,
      @Nullable ScreenRectangle bounds
   ) implements GuiElementRenderState {
      public CircleQuarterOutlineRenderState(
         RenderPipeline pipeline,
         TextureSetup textureSetup,
         Matrix3x2f pose,
         float centerX,
         float centerY,
         int radius,
         int thickness,
         int color,
         Graphics.Corner corner,
         @Nullable ScreenRectangle scissorArea
      ) {
         this(
            pipeline,
            textureSetup,
            pose,
            centerX,
            centerY,
            radius,
            thickness,
            color,
            corner,
            scissorArea,
            createBounds(centerX, centerY, radius, pose, scissorArea)
         );
      }

      @Nullable
      private static ScreenRectangle createBounds(float cx, float cy, int radius, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
         ScreenRectangle rect = new ScreenRectangle((int)(cx - radius), (int)(cy - radius), radius * 2, radius * 2).transformMaxBounds(pose);
         return scissorArea != null ? scissorArea.intersection(rect) : rect;
      }

      public void buildVertices(@NotNull VertexConsumer vertices) {
         int outerRadiusSq = this.radius * this.radius;
         int innerRadiusSq = (this.radius - this.thickness) * (this.radius - this.thickness);

         for (int y = 0; y <= this.radius; y++) {
            for (int x = 0; x <= this.radius; x++) {
               int distSq = x * x + y * y;
               if (distSq <= outerRadiusSq && distSq >= innerRadiusSq) {
                  float px0;
                  float px1;
                  float py0;
                  float py1;
                  switch (this.corner) {
                     case TOP_LEFT:
                        px0 = this.centerX - x;
                        px1 = this.centerX - x + 1.0F;
                        py0 = this.centerY - y;
                        py1 = this.centerY - y + 1.0F;
                        break;
                     case TOP_RIGHT:
                        px0 = this.centerX + x;
                        px1 = this.centerX + x + 1.0F;
                        py0 = this.centerY - y;
                        py1 = this.centerY - y + 1.0F;
                        break;
                     case BOTTOM_LEFT:
                        px0 = this.centerX - x;
                        px1 = this.centerX - x + 1.0F;
                        py0 = this.centerY + y;
                        py1 = this.centerY + y + 1.0F;
                        break;
                     default:
                        px0 = this.centerX + x;
                        px1 = this.centerX + x + 1.0F;
                        py0 = this.centerY + y;
                        py1 = this.centerY + y + 1.0F;
                  }

                  vertices.addVertexWith2DPose(this.pose, px0, py0).setColor(this.color);
                  vertices.addVertexWith2DPose(this.pose, px0, py1).setColor(this.color);
                  vertices.addVertexWith2DPose(this.pose, px1, py1).setColor(this.color);
                  vertices.addVertexWith2DPose(this.pose, px1, py0).setColor(this.color);
               }
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public record CircleQuarterRenderState(
      RenderPipeline pipeline,
      TextureSetup textureSetup,
      Matrix3x2f pose,
      float centerX,
      float centerY,
      int radius,
      int color,
      Graphics.Corner corner,
      @Nullable ScreenRectangle scissorArea,
      @Nullable ScreenRectangle bounds
   ) implements GuiElementRenderState {
      public CircleQuarterRenderState(
         RenderPipeline pipeline,
         TextureSetup textureSetup,
         Matrix3x2f pose,
         float centerX,
         float centerY,
         int radius,
         int color,
         Graphics.Corner corner,
         @Nullable ScreenRectangle scissorArea
      ) {
         this(pipeline, textureSetup, pose, centerX, centerY, radius, color, corner, scissorArea, createBounds(centerX, centerY, radius, pose, scissorArea));
      }

      @Nullable
      private static ScreenRectangle createBounds(float cx, float cy, int radius, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
         ScreenRectangle rect = new ScreenRectangle((int)(cx - radius), (int)(cy - radius), radius * 2, radius * 2).transformMaxBounds(pose);
         return scissorArea != null ? scissorArea.intersection(rect) : rect;
      }

      public void buildVertices(@NotNull VertexConsumer vertices) {
         for (int y = 0; y <= this.radius; y++) {
            for (int x = 0; x <= this.radius; x++) {
               if (x * x + y * y <= this.radius * this.radius) {
                  float px0;
                  float px1;
                  float py0;
                  float py1;
                  switch (this.corner) {
                     case TOP_LEFT:
                        px0 = this.centerX - x;
                        px1 = this.centerX - x + 1.0F;
                        py0 = this.centerY - y;
                        py1 = this.centerY - y + 1.0F;
                        break;
                     case TOP_RIGHT:
                        px0 = this.centerX + x;
                        px1 = this.centerX + x + 1.0F;
                        py0 = this.centerY - y;
                        py1 = this.centerY - y + 1.0F;
                        break;
                     case BOTTOM_LEFT:
                        px0 = this.centerX - x;
                        px1 = this.centerX - x + 1.0F;
                        py0 = this.centerY + y;
                        py1 = this.centerY + y + 1.0F;
                        break;
                     default:
                        px0 = this.centerX + x;
                        px1 = this.centerX + x + 1.0F;
                        py0 = this.centerY + y;
                        py1 = this.centerY + y + 1.0F;
                  }

                  vertices.addVertexWith2DPose(this.pose, px0, py0).setColor(this.color);
                  vertices.addVertexWith2DPose(this.pose, px0, py1).setColor(this.color);
                  vertices.addVertexWith2DPose(this.pose, px1, py1).setColor(this.color);
                  vertices.addVertexWith2DPose(this.pose, px1, py0).setColor(this.color);
               }
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public record ColoredFloatQuadGuiElementRenderState(
      RenderPipeline pipeline,
      TextureSetup textureSetup,
      Matrix3x2f pose,
      float x0,
      float y0,
      float x1,
      float y1,
      int col1,
      int col2,
      @Nullable ScreenRectangle scissorArea,
      @Nullable ScreenRectangle bounds
   ) implements GuiElementRenderState {
      public ColoredFloatQuadGuiElementRenderState(
         RenderPipeline pipeline,
         TextureSetup textureSetup,
         Matrix3x2f pose,
         float x0,
         float y0,
         float x1,
         float y1,
         int col1,
         int col2,
         @Nullable ScreenRectangle scissorArea
      ) {
         this(pipeline, textureSetup, pose, x0, y0, x1, y1, col1, col2, scissorArea, createBounds(x0, y0, x1, y1, pose, scissorArea));
      }

      public void buildVertices(@NotNull VertexConsumer vertices) {
         vertices.addVertexWith2DPose(this.pose(), this.x0(), this.y0()).setColor(this.col1());
         vertices.addVertexWith2DPose(this.pose(), this.x0(), this.y1()).setColor(this.col2());
         vertices.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setColor(this.col2());
         vertices.addVertexWith2DPose(this.pose(), this.x1(), this.y0()).setColor(this.col1());
      }

      @Nullable
      private static ScreenRectangle createBounds(float x0, float y0, float x1, float y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
         ScreenRectangle bounds = new ScreenRectangle((int)x0, (int)y0, (int)(x1 - x0), (int)(y1 - y0)).transformMaxBounds(pose);
         return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
      }
   }

   public enum Corner {
      TOP_LEFT,
      TOP_RIGHT,
      BOTTOM_LEFT,
      BOTTOM_RIGHT;
   }

   @Environment(EnvType.CLIENT)
   public record HueSliderGuiElementRenderState(
      RenderPipeline pipeline,
      TextureSetup textureSetup,
      Matrix3x2f pose,
      int x0,
      int y0,
      int x1,
      int y1,
      int radius,
      @Nullable ScreenRectangle scissorArea,
      @Nullable ScreenRectangle bounds
   ) implements GuiElementRenderState {
      public HueSliderGuiElementRenderState(
         RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, int radius, @Nullable ScreenRectangle scissorArea
      ) {
         this(pipeline, textureSetup, pose, x0, y0, x1, y1, radius, scissorArea, createBounds(x0, y0, x1, y1, pose, scissorArea));
      }

      @Nullable
      private static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
         ScreenRectangle bounds = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
         return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
      }

      public void buildVertices(@NotNull VertexConsumer vertices) {
         int width = this.x1 - this.x0;
         int height = this.y1 - this.y0;
         if (width > 0 && height > 0) {
            int steps = 64;

            for (int i = 0; i < 64; i++) {
               float t0 = i / 64.0F;
               float t1 = (i + 1) / 64.0F;
               int color0 = Color.HSBtoRGB(1.0F - t0, 1.0F, 1.0F) | 0xFF000000;
               int color1 = Color.HSBtoRGB(1.0F - t1, 1.0F, 1.0F) | 0xFF000000;
               float yStart = this.y0 + t0 * height;
               float yEnd = this.y0 + t1 * height;
               int dy0 = (int)(yStart - this.y0);
               int dy1 = (int)(yEnd - this.y0);
               int leftX0 = this.x0;
               int rightX0 = this.x1 - 1;
               int leftX1 = this.x0;
               int rightX1 = this.x1 - 1;
               if (dy0 < this.radius) {
                  int dd = this.radius - dy0;
                  int offsetX = (int)Math.sqrt((double)this.radius * this.radius - (double)dd * dd);
                  leftX0 = this.x0 + this.radius - offsetX;
                  rightX0 = this.x1 - this.radius + offsetX - 1;
               } else if (dy0 >= height - this.radius) {
                  int dd = dy0 - (height - this.radius - 1);
                  int offsetX = (int)Math.sqrt((double)this.radius * this.radius - (double)dd * dd);
                  leftX0 = this.x0 + this.radius - offsetX;
                  rightX0 = this.x1 - this.radius + offsetX - 1;
               }

               if (dy1 < this.radius) {
                  int dd = this.radius - dy1;
                  int offsetX = (int)Math.sqrt((double)this.radius * this.radius - (double)dd * dd);
                  leftX1 = this.x0 + this.radius - offsetX;
                  rightX1 = this.x1 - this.radius + offsetX - 1;
               } else if (dy1 >= height - this.radius) {
                  int dd = dy1 - (height - this.radius - 1);
                  int offsetX = (int)Math.sqrt((double)this.radius * this.radius - (double)dd * dd);
                  leftX1 = this.x0 + this.radius - offsetX;
                  rightX1 = this.x1 - this.radius + offsetX - 1;
               }

               int leftX = Math.max(leftX0, leftX1);
               int rightX = Math.min(rightX0, rightX1);
               if (rightX > leftX) {
                  vertices.addVertexWith2DPose(this.pose, leftX, yStart).setColor(color0);
                  vertices.addVertexWith2DPose(this.pose, leftX, yEnd).setColor(color1);
                  vertices.addVertexWith2DPose(this.pose, rightX + 1, yEnd).setColor(color1);
                  vertices.addVertexWith2DPose(this.pose, rightX + 1, yStart).setColor(color0);
               }
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public record RoundedGradientRectGuiElementRenderState(
      RenderPipeline pipeline,
      TextureSetup textureSetup,
      Matrix3x2f pose,
      int x0,
      int y0,
      int x1,
      int y1,
      int radius,
      int colorTop,
      int colorBottom,
      @Nullable ScreenRectangle scissorArea,
      @Nullable ScreenRectangle bounds
   ) implements GuiElementRenderState {
      public RoundedGradientRectGuiElementRenderState(
         RenderPipeline pipeline,
         TextureSetup textureSetup,
         Matrix3x2f pose,
         int x0,
         int y0,
         int x1,
         int y1,
         int radius,
         int colorTop,
         int colorBottom,
         @Nullable ScreenRectangle scissorArea
      ) {
         this(pipeline, textureSetup, pose, x0, y0, x1, y1, radius, colorTop, colorBottom, scissorArea, createBounds(x0, y0, x1, y1, pose, scissorArea));
      }

      @Nullable
      private static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
         ScreenRectangle bounds = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
         return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
      }

      public void buildVertices(@NotNull VertexConsumer vertices) {
         int width = this.x1 - this.x0;
         int height = this.y1 - this.y0;
         if (width > 0 && height > 0) {
            int steps = Math.max(2, height);

            for (int i = 0; i < steps; i++) {
               float t0 = (float)i / steps;
               float t1 = (float)(i + 1) / steps;
               int color0 = lerpColor(this.colorTop, this.colorBottom, t0);
               int color1 = lerpColor(this.colorTop, this.colorBottom, t1);
               float yStart = this.y0 + t0 * height;
               float yEnd = this.y0 + t1 * height;
               int dy0 = (int)(yStart - this.y0);
               int dy1 = (int)(yEnd - this.y0);
               int leftX0 = this.x0;
               int rightX0 = this.x1 - 1;
               int leftX1 = this.x0;
               int rightX1 = this.x1 - 1;
               if (dy0 < this.radius) {
                  int dd = this.radius - dy0;
                  int offsetX = (int)Math.sqrt((double)this.radius * this.radius - (double)dd * dd);
                  leftX0 = this.x0 + this.radius - offsetX;
                  rightX0 = this.x1 - this.radius + offsetX - 1;
               } else if (dy0 >= height - this.radius) {
                  int dd = dy0 - (height - this.radius - 1);
                  int offsetX = (int)Math.sqrt((double)this.radius * this.radius - (double)dd * dd);
                  leftX0 = this.x0 + this.radius - offsetX;
                  rightX0 = this.x1 - this.radius + offsetX - 1;
               }

               if (dy1 < this.radius) {
                  int dd = this.radius - dy1;
                  int offsetX = (int)Math.sqrt((double)this.radius * this.radius - (double)dd * dd);
                  leftX1 = this.x0 + this.radius - offsetX;
                  rightX1 = this.x1 - this.radius + offsetX - 1;
               } else if (dy1 >= height - this.radius) {
                  int dd = dy1 - (height - this.radius - 1);
                  int offsetX = (int)Math.sqrt((double)this.radius * this.radius - (double)dd * dd);
                  leftX1 = this.x0 + this.radius - offsetX;
                  rightX1 = this.x1 - this.radius + offsetX - 1;
               }

               int leftX = Math.max(leftX0, leftX1);
               int rightX = Math.min(rightX0, rightX1);
               if (rightX > leftX) {
                  vertices.addVertexWith2DPose(this.pose, leftX, yStart).setColor(color0);
                  vertices.addVertexWith2DPose(this.pose, leftX, yEnd).setColor(color1);
                  vertices.addVertexWith2DPose(this.pose, rightX + 1, yEnd).setColor(color1);
                  vertices.addVertexWith2DPose(this.pose, rightX + 1, yStart).setColor(color0);
               }
            }
         }
      }

      private static int lerpColor(int top, int bottom, float t) {
         int aTop = top >> 24 & 0xFF;
         int rTop = top >> 16 & 0xFF;
         int gTop = top >> 8 & 0xFF;
         int bTop = top & 0xFF;
         int aBot = bottom >> 24 & 0xFF;
         int rBot = bottom >> 16 & 0xFF;
         int gBot = bottom >> 8 & 0xFF;
         int bBot = bottom & 0xFF;
         return (int)(aTop + t * (aBot - aTop)) << 24
            | (int)(rTop + t * (rBot - rTop)) << 16
            | (int)(gTop + t * (gBot - gTop)) << 8
            | (int)(bTop + t * (bBot - bTop));
      }
   }

   @Environment(EnvType.CLIENT)
   public record RoundedTextureGuiElementRenderState(
      RenderPipeline pipeline,
      TextureSetup textureSetup,
      Matrix3x2f pose,
      Identifier texture,
      int x0,
      int y0,
      int x1,
      int y1,
      int radius,
      int textureWidth,
      int textureHeight,
      @Nullable ScreenRectangle scissorArea,
      @Nullable ScreenRectangle bounds
   ) implements GuiElementRenderState {
      public RoundedTextureGuiElementRenderState(
         RenderPipeline pipeline,
         TextureSetup textureSetup,
         Matrix3x2f pose,
         Identifier texture,
         int x0,
         int y0,
         int x1,
         int y1,
         int radius,
         int textureWidth,
         int textureHeight,
         @Nullable ScreenRectangle scissorArea
      ) {
         this(
            pipeline,
            textureSetup,
            pose,
            texture,
            x0,
            y0,
            x1,
            y1,
            radius,
            textureWidth,
            textureHeight,
            scissorArea,
            createBounds(x0, y0, x1, y1, pose, scissorArea)
         );
      }

      @Nullable
      private static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
         ScreenRectangle bounds = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
         return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
      }

      public void buildVertices(@NotNull VertexConsumer vertices) {
         int width = this.x1 - this.x0;
         int height = this.y1 - this.y0;
         if (width > 0 && height > 0) {
            for (int dy = 0; dy < height; dy++) {
               int currentY = this.y0 + dy;
               int leftX = this.x0;
               int rightX = this.x1 - 1;
               if (dy < this.radius) {
                  int dd = this.radius - dy;
                  int offsetX = (int)Math.sqrt((double)this.radius * this.radius - (double)dd * dd);
                  leftX = this.x0 + this.radius - offsetX;
                  rightX = this.x1 - this.radius + offsetX - 1;
               } else if (dy >= height - this.radius) {
                  int dd = dy - (height - this.radius - 1);
                  int offsetX = (int)Math.sqrt((double)this.radius * this.radius - (double)dd * dd);
                  leftX = this.x0 + this.radius - offsetX;
                  rightX = this.x1 - this.radius + offsetX - 1;
               }

               if (rightX >= leftX) {
                  float u0 = (float)(leftX - this.x0) / width;
                  float u1 = (float)(rightX + 1 - this.x0) / width;
                  float v0 = (float)dy / height;
                  float v1 = (float)(dy + 1) / height;
                  vertices.addVertexWith2DPose(this.pose, leftX, currentY).setUv(u0, v0).setColor(-1);
                  vertices.addVertexWith2DPose(this.pose, leftX, currentY + 1).setUv(u0, v1).setColor(-1);
                  vertices.addVertexWith2DPose(this.pose, rightX + 1, currentY + 1).setUv(u1, v1).setColor(-1);
                  vertices.addVertexWith2DPose(this.pose, rightX + 1, currentY).setUv(u1, v0).setColor(-1);
               }
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public record SaturationBoxGuiElementRenderState(
      RenderPipeline pipeline,
      TextureSetup textureSetup,
      Matrix3x2f pose,
      int x0,
      int y0,
      int x1,
      int y1,
      int radius,
      float hue,
      @Nullable ScreenRectangle scissorArea,
      @Nullable ScreenRectangle bounds
   ) implements GuiElementRenderState {
      public SaturationBoxGuiElementRenderState(
         RenderPipeline pipeline,
         TextureSetup textureSetup,
         Matrix3x2f pose,
         int x0,
         int y0,
         int x1,
         int y1,
         int radius,
         float hue,
         @Nullable ScreenRectangle scissorArea
      ) {
         this(pipeline, textureSetup, pose, x0, y0, x1, y1, radius, hue, scissorArea, createBounds(x0, y0, x1, y1, pose, scissorArea));
      }

      @Nullable
      private static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
         ScreenRectangle bounds = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
         return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
      }

      public void buildVertices(@NotNull VertexConsumer vertices) {
         int width = this.x1 - this.x0;
         int height = this.y1 - this.y0;
         if (width > 0 && height > 0) {
            int steps = Math.max(2, height);

            for (int i = 0; i < steps; i++) {
               float t0 = (float)i / steps;
               float t1 = (float)(i + 1) / steps;
               float value0 = 1.0F - t0;
               float value1 = 1.0F - t1;
               int leftColor0 = Color.HSBtoRGB(this.hue, 0.0F, value0) | 0xFF000000;
               int rightColor0 = Color.HSBtoRGB(this.hue, 1.0F, value0) | 0xFF000000;
               int leftColor1 = Color.HSBtoRGB(this.hue, 0.0F, value1) | 0xFF000000;
               int rightColor1 = Color.HSBtoRGB(this.hue, 1.0F, value1) | 0xFF000000;
               float yStart = this.y0 + t0 * height;
               float yEnd = this.y0 + t1 * height;
               int dy0 = (int)(yStart - this.y0);
               int dy1 = (int)(yEnd - this.y0);
               int leftX0 = this.x0;
               int rightX0 = this.x1 - 1;
               int leftX1 = this.x0;
               int rightX1 = this.x1 - 1;
               if (this.radius > 0) {
                  if (dy0 < this.radius) {
                     int dd = this.radius - dy0;
                     int offsetX = (int)Math.sqrt((double)this.radius * this.radius - (double)dd * dd);
                     leftX0 = this.x0 + this.radius - offsetX;
                     rightX0 = this.x1 - this.radius + offsetX - 1;
                  } else if (dy0 >= height - this.radius) {
                     int dd = dy0 - (height - this.radius - 1);
                     int offsetX = (int)Math.sqrt((double)this.radius * this.radius - (double)dd * dd);
                     leftX0 = this.x0 + this.radius - offsetX;
                     rightX0 = this.x1 - this.radius + offsetX - 1;
                  }

                  if (dy1 < this.radius) {
                     int dd = this.radius - dy1;
                     int offsetX = (int)Math.sqrt((double)this.radius * this.radius - (double)dd * dd);
                     leftX1 = this.x0 + this.radius - offsetX;
                     rightX1 = this.x1 - this.radius + offsetX - 1;
                  } else if (dy1 >= height - this.radius) {
                     int dd = dy1 - (height - this.radius - 1);
                     int offsetX = (int)Math.sqrt((double)this.radius * this.radius - (double)dd * dd);
                     leftX1 = this.x0 + this.radius - offsetX;
                     rightX1 = this.x1 - this.radius + offsetX - 1;
                  }
               }

               int leftX = Math.max(leftX0, leftX1);
               int rightX = Math.min(rightX0, rightX1);
               if (rightX > leftX) {
                  vertices.addVertexWith2DPose(this.pose, leftX, yStart).setColor(leftColor0);
                  vertices.addVertexWith2DPose(this.pose, leftX, yEnd).setColor(leftColor1);
                  vertices.addVertexWith2DPose(this.pose, rightX + 1, yEnd).setColor(rightColor1);
                  vertices.addVertexWith2DPose(this.pose, rightX + 1, yStart).setColor(rightColor0);
               }
            }
         }
      }
   }
}
