package main.konfy.lib.core.gui.widgets;

import java.awt.Color;
import java.awt.Point;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.config.local.options.type.KonfyLibColor;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.gui.widgets.sub.SliderSubWidget;
import main.konfy.lib.core.gui.widgets.sub.TextboxSubWidget;
import main.konfy.lib.core.gui.widgets.sub.adaptor.IntSliderAdapter;
import main.konfy.lib.core.utils.MainColors;
import main.konfy.lib.core.utils.ScreenGlobals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class ColorWidget extends OpenableWidget {
   private final Option<KonfyLibColor> option;
   public int COLOR_PICKER_STARTX;
   private final Identifier TRANSPARENT_BACKGROUND = Identifier.fromNamespaceAndPath("konfy", "gui/widget/transparent.png");
   private final Identifier RAINBOW_ICON = Identifier.fromNamespaceAndPath("konfy", "gui/widget/rainbow.png");
   private final Identifier PULSE_ICON = Identifier.fromNamespaceAndPath("konfy", "gui/widget/pulse.png");
   private final ButtonWidget chromaButton;
   private final ButtonWidget pulseButton;
   private final SliderSubWidget<Integer> chromaSpeedSlider;
   private final SliderSubWidget<Integer> pulseSpeedSlider;
   private float satThumbX;
   private float satThumbY;
   private float hueThumbY;
   private float opacityThumbY;
   private final TextboxSubWidget hexInput;
   private boolean updatingHex = false;
   private ColorWidget.DragTarget activeDrag = ColorWidget.DragTarget.NONE;

   public ColorWidget(OptionGroup parent, KonfyLibConfigScreen screen, int x, int y, int width, int height, Option<KonfyLibColor> option) {
      super(parent, screen, option, x, y, width, height, option.getName(), ScreenGlobals.OPTION_HEIGHT * 5);
      this.option = option;
      KonfyLibColor initial = option.getValue();
      float[] hsb = KonfyLibColor.RGBtoHSB(initial.getRed(), initial.getGreen(), initial.getBlue(), null);
      option.getValue().setHue(hsb[0]);
      option.getValue().setSaturation(hsb[1]);
      option.getValue().setBrightness(hsb[2]);
      option.getValue().setAlpha(initial.getAlpha());
      this.COLOR_PICKER_STARTX = this.getX() + this.getWidth() / 2;
      this.chromaButton = new ButtonWidget(
         x + 5, y + 19 + 10, 17, 17, false, this.RAINBOW_ICON, () -> this.option.getValue().setRainbow(!this.option.getValue().isRainbow()), -3, -3
      );
      this.pulseButton = new ButtonWidget(
         x + 5, y + 45 + 10, 17, 17, false, this.PULSE_ICON, () -> this.option.getValue().setPulse(!this.option.getValue().isPulse()), -3, -3
      );
      int sliderWidth = this.getWidth() / 2 - 38;
      this.chromaSpeedSlider = new SliderSubWidget<>(
         x + 28,
         y + 23 + 10,
         sliderWidth,
         ScreenGlobals.OPTION_HEIGHT - 12,
         new IntSliderAdapter(1, 20, this.option.getValue().getRainbowSpeed()),
         this.option.getValue().getRainbowSpeed(),
         rainbowSpeed -> {
            this.option.getValue().setRainbowSpeed(rainbowSpeed);
            this.updateThumbTargets(false);
         },
         true
      );
      this.pulseSpeedSlider = new SliderSubWidget<>(
         x + 28,
         y + 49 + 10,
         sliderWidth,
         ScreenGlobals.OPTION_HEIGHT - 12,
         new IntSliderAdapter(1, 20, this.option.getValue().getPulseSpeed()),
         this.option.getValue().getPulseSpeed(),
         pulseSpeed -> {
            this.option.getValue().setPulseSpeed(pulseSpeed);
            this.updateThumbTargets(false);
         },
         true
      );
      String initialHex = String.format("#%02X%02X%02X%02X", initial.getRed(), initial.getGreen(), initial.getBlue(), initial.getAlpha());
      this.hexInput = new TextboxSubWidget(screen, 0, 0, 78, 78, 14, initialHex, str -> {}, false);
      this.hexInput.setOnFocusLost(() -> {
         String hexStr = this.hexInput.getText();
         String clean = hexStr.startsWith("#") ? hexStr.substring(1) : hexStr;
         if (clean.length() != 6 && clean.length() != 8) {
            this.syncHexInput();
         } else {
            try {
               int r = Integer.parseInt(clean.substring(0, 2), 16);
               int g = Integer.parseInt(clean.substring(2, 4), 16);
               int b = Integer.parseInt(clean.substring(4, 6), 16);
               int a = clean.length() == 8 ? Integer.parseInt(clean.substring(6, 8), 16) : option.getValue().getAlpha();
               float[] parsedHsb = KonfyLibColor.RGBtoHSB(r, g, b, null);
               option.getValue().setHue(parsedHsb[0]);
               option.getValue().setSaturation(parsedHsb[1]);
               option.getValue().setBrightness(parsedHsb[2]);
               option.getValue().setAlpha(a);
               KonfyLibColor updated = new KonfyLibColor(r, g, b, a);
               updated.setAdditions(option.getValue().getAdditions());
               option.setValue(updated);
               this.updateThumbTargets(false);
               this.syncHexInput();
            } catch (NumberFormatException ignored) {
               this.syncHexInput();
            }
         }
      });
   }

   @Override
   public void extract(Graphics graphics, int mouseX, int mouseY, float delta) {
      GuiGraphicsExtractor extractor = graphics.extractor();
      super.extract(graphics, mouseX, mouseY, delta);
      this.pulseSpeedSlider.setOnChange(this.option.getValue()::setPulseSpeed);
      this.chromaSpeedSlider.setOnChange(this.option.getValue()::setRainbowSpeed);
      this.COLOR_PICKER_STARTX = this.getX() + this.getWidth() / 2;
      int baseHeight = ScreenGlobals.OPTION_HEIGHT;
      graphics.drawRoundedTexture(RenderPipelines.GUI_TEXTURED, this.TRANSPARENT_BACKGROUND, this.getX() + this.getWidth() - 30, this.getY() + 4, 23, baseHeight - 8, 2, 4, 4);
      graphics.fillRoundedRectOutline(this.getX() + this.getWidth() - 31, this.getY() + 3, 25, baseHeight - 6, 2, 1, MainColors.OUTLINE_BLACK.getRGB());
      graphics.fillRoundedRect(this.getX() + this.getWidth() - 30, this.getY() + 4, 23.0F, baseHeight - 8, 2, this.option.getValue().getRGB());
      this.hexInput.extract(graphics, mouseX, mouseY, delta);
      if (this.fullyClosed()) {
         extractor.verticalLine(
            this.getX() + this.getWidth() - 38,
            this.getY(),
            this.getY() + ScreenGlobals.OPTION_HEIGHT - 1,
            this.isHovered() ? MainColors.OUTLINE_WHITE_HOVERED.getRGB() : MainColors.OUTLINE_WHITE.getRGB()
         );
      } else if (this.getCurrentHeight() > 34.0F) {
         this.drawHueSlider(graphics);
         this.drawSaturationBox(graphics);
         this.drawOpacitySlider(graphics);
         this.chromaButton.overrideHover = this.option.getValue().isRainbow();
         this.pulseButton.overrideHover = this.option.getValue().isPulse();
         this.chromaButton.extractWidgetRenderState(extractor, mouseX, mouseY, delta);
         this.pulseButton.extractWidgetRenderState(extractor, mouseX, mouseY, delta);
         this.chromaSpeedSlider.extract(graphics, mouseX, mouseY, delta);
         this.pulseSpeedSlider.extract(graphics, mouseX, mouseY, delta);
         extractor.text(Minecraft.getInstance().font, "Rainbow Speed", this.getX() + 32, this.getY() + 23, Color.LIGHT_GRAY.getRGB(), true);
         extractor.text(Minecraft.getInstance().font, "Pulse Speed", this.getX() + 32, this.getY() + 49, Color.LIGHT_GRAY.getRGB(), true);
      }
   }

   public void drawSatThumb(Graphics graphics, int x, int y) {
      graphics.fillRoundedRectOutline(x, y, 6, 6, 1, 1, Color.BLACK.getRGB());
      graphics.fillRoundedRectOutline(x + 1, y + 1, 4, 4, 1, 1, MainColors.OUTLINE_WHITE.getRGB());
   }

   public void drawSliderThumb(Graphics graphics, int x, int y) {
      graphics.fillRoundedRectOutline(x, y, 9, 3, 1, 1, Color.BLACK.getRGB());
   }

   public void drawOpacitySlider(Graphics graphics) {
      int opacityHeight = this.getHeight() - 30 + 8;
      graphics.fillRoundedRectOutline(this.COLOR_PICKER_STARTX - 34, this.getY() + 19, 15, opacityHeight, 2, 1, MainColors.OUTLINE_BLACK.getRGB());
      graphics.fillRoundedRectOutline(
         this.COLOR_PICKER_STARTX - 33,
         this.getY() + 20,
         13,
         opacityHeight - 2,
         2,
         1,
         this.isHoveringOpacitySlider(this.mouseX, this.mouseY) ? MainColors.OUTLINE_WHITE_HOVERED.getRGB() : MainColors.OUTLINE_WHITE.getRGB()
      );
      graphics.drawRoundedTexture(
         RenderPipelines.GUI_TEXTURED, this.TRANSPARENT_BACKGROUND, this.COLOR_PICKER_STARTX - 32, this.getY() + 21, 11, opacityHeight - 4, 2, 4, 4
      );
      KonfyLibColor color = new KonfyLibColor(this.option.getValue().getRed(), this.option.getValue().getGreen(), this.option.getValue().getBlue(), 255);
      KonfyLibColor colorG = new KonfyLibColor(this.option.getValue().getRed(), this.option.getValue().getGreen(), this.option.getValue().getBlue(), 0);
      graphics.fillRoundedRectGradient(this.COLOR_PICKER_STARTX - 32, this.getY() + 21, 11, opacityHeight - 4, 2, color.getRGB(), colorG.getRGB());
      int opacitySliderX = this.COLOR_PICKER_STARTX - 34;
      this.drawSliderThumb(graphics, opacitySliderX + 3, (int)this.opacityThumbY + 1);
   }

   public void drawHueSlider(Graphics graphics) {
      int hueHeight = this.getHeight() - 30 + 8;
      graphics.fillRoundedRectOutline(this.COLOR_PICKER_STARTX - 17, this.getY() + 19, 15, hueHeight, 2, 1, MainColors.OUTLINE_BLACK.getRGB());
      graphics.fillRoundedRectOutline(
         this.COLOR_PICKER_STARTX - 16,
         this.getY() + 20,
         13,
         hueHeight - 2,
         2,
         1,
         this.isHoveringHueSlider(this.mouseX, this.mouseY) ? MainColors.OUTLINE_WHITE_HOVERED.getRGB() : MainColors.OUTLINE_WHITE.getRGB()
      );
      graphics.drawRoundedHueSlider(this.COLOR_PICKER_STARTX - 15, this.getY() + 21, 11, hueHeight - 4, 2);
      int hueSliderX = this.COLOR_PICKER_STARTX - 17;
      this.drawSliderThumb(graphics, hueSliderX + 3, (int)this.hueThumbY + 1);
   }

   public void drawSaturationBox(Graphics graphics) {
      graphics.fillRoundedRectOutline(
         this.COLOR_PICKER_STARTX,
         this.getY() + 19,
         this.getWidth() / 2 - 10,
         this.getHeight() - 28 + 6,
         2,
         1,
         MainColors.OUTLINE_BLACK.getRGB()
      );
      graphics.fillRoundedRectOutline(
         this.COLOR_PICKER_STARTX + 1,
         this.getY() + 20,
         this.getWidth() / 2 - 12,
         this.getHeight() - 28 + 6 - 2,
         2,
         1,
         this.isHoveringSaturationValueBox(this.mouseX, this.mouseY) ? MainColors.OUTLINE_WHITE_HOVERED.getRGB() : MainColors.OUTLINE_WHITE.getRGB()
      );
      graphics.drawHueSaturationValueBox(
         this.COLOR_PICKER_STARTX + 2,
         this.getY() + 21,
         this.getWidth() / 2 - 14,
         this.getHeight() - 28 + 6 - 4,
         2,
         this.option.getValue().getHue()
      );
      this.drawSatThumb(graphics, (int)this.satThumbX + 1, (int)this.satThumbY);
   }

   public boolean isHoveringHueSlider(double mouseX, double mouseY) {
      int x = this.COLOR_PICKER_STARTX - 17;
      int y = this.getY() + 19;
      int width = 15;
      int height = this.getHeight() - 30 + 8;
      return mouseX >= x && mouseX < x + 15 && mouseY >= y && mouseY < y + height;
   }

   public boolean isHoveringSaturationValueBox(double mouseX, double mouseY) {
      int x = this.COLOR_PICKER_STARTX;
      int y = this.getY() + 19;
      int width = this.getWidth() / 2 - 10;
      int height = this.getHeight() - 28 + 6;
      return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
   }

   public boolean isHoveringOpacitySlider(double mouseX, double mouseY) {
      int x = this.COLOR_PICKER_STARTX - 34;
      int y = this.getY() + 19;
      int width = 15;
      int height = this.getHeight() - 30 + 8;
      return mouseX >= x && mouseX < x + 15 && mouseY >= y && mouseY < y + height;
   }

   public boolean isHoveringRainbowButton(double mouseX, double mouseY) {
      int x = this.COLOR_PICKER_STARTX - 54;
      int y = this.getY() + 19;
      int width = 18;
      int height = this.getHeight() - 85 + 3;
      return mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + height;
   }

   @Override
   public void onMouseClick(MouseButtonEvent click, boolean doubled) {
      super.onMouseClick(click, doubled);
      double mouseX = click.x();
      double mouseY = click.y();
      int button = click.button();
      this.chromaSpeedSlider.onClick(click, doubled);
      this.pulseSpeedSlider.onClick(click, doubled);
      this.chromaButton.onClick(click, doubled);
      this.pulseButton.onClick(click, doubled);
      this.hexInput.onClick(click, doubled);
      if (button == 0) {
         if (this.open) {
            if (this.isHoveringHueSlider(mouseX, mouseY)) {
               this.activeDrag = ColorWidget.DragTarget.HUE_SLIDER;
               this.handleHueSliderClick(mouseY);
               this.onChange();
               return;
            }

            if (this.isHoveringSaturationValueBox(mouseX, mouseY)) {
               this.activeDrag = ColorWidget.DragTarget.SATURATION_VALUE_BOX;
               this.handleSatValBoxClick(mouseX, mouseY);
               this.onChange();
               return;
            }

            if (this.isHoveringOpacitySlider(mouseX, mouseY)) {
               this.activeDrag = ColorWidget.DragTarget.OPACITY_SLIDER;
               this.handleOpacitySliderClick(mouseY);
               this.onChange();
               return;
            }
         }
      }
   }

   @Override
   public void onKeyPress(KeyEvent input) {
      this.hexInput.onKeyPress(input);
   }

   @Override
   public void onCharTyped(CharacterEvent input) {
      this.hexInput.onCharTyped(input);
   }

   @Override
   protected void onOpen(boolean prev) {
   }

   @Override
   public boolean isHovered() {
      return this.mouseX >= this.getX()
         && this.mouseX < this.getX() + this.getWidth() - 6
         && this.mouseY >= this.getY()
         && this.mouseY < this.getY() + ScreenGlobals.OPTION_HEIGHT
         && this.isVisible()
         && this.isAvailable()
         && !this.hexInput.hovered;
   }

   @Override
   public void onMouseRelease(MouseButtonEvent click) {
      if (click.button() == 0) {
         this.activeDrag = ColorWidget.DragTarget.NONE;
      }
   }

   @Override
   public void onMouseDrag(MouseButtonEvent click, double offsetX, double offsetY) {
      this.chromaSpeedSlider.onDrag(this.mouseX);
      this.pulseSpeedSlider.onDrag(this.mouseX);
      if (click.button() == 0) {
         switch (this.activeDrag) {
            case HUE_SLIDER:
               this.handleHueSliderDrag(this.mouseY);
               break;
            case SATURATION_VALUE_BOX:
               this.handleSatValBoxDrag(this.mouseX, this.mouseY);
               break;
            case OPACITY_SLIDER:
               this.handleOpacitySliderDrag(this.mouseY);
         }

         this.onChange();
      }
   }

   @Override
   public void onChange() {
      super.onChange();
      this.updateThumbTargets(false);
   }

   public void onRelease(MouseButtonEvent click) {
      this.chromaSpeedSlider.release();
      this.pulseSpeedSlider.release();
   }

    @Override
    public void onWidgetUpdate() {
       this.COLOR_PICKER_STARTX = this.getX() + this.getWidth() / 2;
       this.chromaSpeedSlider.setPos(new Point(this.getX() + 28, this.getY() + 23 + 10));
       this.pulseSpeedSlider.setPos(new Point(this.getX() + 28, this.getY() + 49 + 10));
       this.chromaButton.setX(this.getX() + 5);
       this.chromaButton.setY(this.getY() + 29);
       this.pulseButton.setX(this.getX() + 5);
       this.pulseButton.setY(this.getY() + 55);
       this.hexInput.setPos(new Point(this.getX() + this.getWidth() - 98, this.getTextYCentered() - 3));
       int width = this.getWidth() / 2 - 80;
       this.chromaSpeedSlider.setWidth(width);
       this.pulseSpeedSlider.setWidth(width);
       this.updateThumbTargets(true);
    }

   @Override
   public <V> void onThirdPartyChange(V value) {
      super.onThirdPartyChange(value);
      this.pulseSpeedSlider.setValue(this.option.getValue().getPulseSpeed());
      this.chromaSpeedSlider.setValue(this.option.getValue().getRainbowSpeed());
      this.syncHexInput();
   }

   private void updateThumbTargets(boolean jump) {
      int hueSliderY = this.getY() + 21;
      int hueSliderHeight = this.getHeight() - 30 + 8 - 4;
      int rawHueThumbY = hueSliderY + (int)((1.0F - this.option.getValue().getHue()) * hueSliderHeight) - 2;
      int targetHueY = Math.max(hueSliderY - 1, Math.min(rawHueThumbY - 1, hueSliderY + hueSliderHeight - 2));
      int opacitySliderY = this.getY() + 21;
      int opacitySliderHeight = this.getHeight() - 30 + 8 - 4;
      int rawOpacityThumbY = opacitySliderY + (int)((1.0F - this.option.getValue().getAlpha() / 255.0F) * opacitySliderHeight) - 2;
      int targetOpacityY = Math.max(opacitySliderY - 1, Math.min(rawOpacityThumbY - 1, opacitySliderY + opacitySliderHeight - 2));
      int boxX = this.COLOR_PICKER_STARTX;
      int boxY = this.getY() + 21;
      int boxWidth = this.getWidth() / 2 - 10;
      int boxHeight = this.getHeight() - 28 + 6 - 4;
      int rawThumbX = boxX + (int)(this.option.getValue().getSaturation() * boxWidth) + 3;
      int rawThumbY = boxY + (int)((1.0F - this.option.getValue().getBrightness()) * boxHeight) + 1;
      this.hueThumbY = targetHueY;
      this.satThumbX = Math.max(boxX + 1, Math.min(rawThumbX - 6, boxX + boxWidth - 8));
      this.satThumbY = Math.max(boxY, Math.min(rawThumbY - 4, boxY + boxHeight - 6));
      this.opacityThumbY = targetOpacityY;
   }

   private void handleHueSliderClick(double mouseY) {
      float newHue = 1.0F - (float)((mouseY - (this.getY() + 20)) / (this.getHeight() - 30 + 6));
      this.option.getValue().setHue(Mth.clamp(newHue, 0.0F, 1.0F));
      this.updateColor();
   }

   private void handleHueSliderDrag(double mouseY) {
      this.handleHueSliderClick(mouseY);
   }

   private void handleSatValBoxClick(double mouseX, double mouseY) {
      int boxX = this.COLOR_PICKER_STARTX;
      int boxY = this.getY() + 20;
      int boxWidth = this.getWidth() / 2 - 10;
      int boxHeight = this.getHeight() - 29 + 6;
      float newSaturation = (float)((mouseX - boxX) / boxWidth);
      float newBrightness = 1.0F - (float)((mouseY - boxY) / boxHeight);
      if (mouseX < boxX) {
         this.option.getValue().setSaturation(0.0F);
      } else if (mouseX > boxX + boxWidth) {
         this.option.getValue().setSaturation(1.0F);
      } else {
         this.option.getValue().setSaturation(Mth.clamp(newSaturation, 0.0F, 1.0F));
      }

      if (mouseY < boxY) {
         this.option.getValue().setBrightness(1.0F);
      } else if (mouseY > boxY + boxHeight) {
         this.option.getValue().setBrightness(0.0F);
      } else {
         this.option.getValue().setBrightness(Mth.clamp(newBrightness, 0.0F, 1.0F));
      }

      this.updateColor();
   }

   private void handleSatValBoxDrag(double mouseX, double mouseY) {
      this.handleSatValBoxClick(mouseX, mouseY);
   }

   private void handleOpacitySliderClick(double mouseY) {
      int sliderY = this.getY() + 20;
      int sliderHeight = this.getHeight() - 30 + 6;
      float newOpacity = 1.0F - (float)((mouseY - sliderY) / sliderHeight);
      this.option.getValue().setAlpha((int)(Mth.clamp(newOpacity, 0.0F, 1.0F) * 255.0F));
      this.updateColor();
   }

   private void handleOpacitySliderDrag(double mouseY) {
      this.handleOpacitySliderClick(mouseY);
   }

   private void updateColor() {
      int rgb = KonfyLibColor.HSBtoRGB(this.option.getValue().getHue(), this.option.getValue().getSaturation(), this.option.getValue().getBrightness());
      KonfyLibColor updated = new KonfyLibColor(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF, this.option.getValue().getAlpha());
      updated.setAdditions(this.option.getValue().getAdditions());
      this.option.setValue(updated);
      this.syncHexInput();
   }

   private void syncHexInput() {
      this.updatingHex = true;
      this.hexInput
         .setText(
            String.format(
               "#%02X%02X%02X%02X",
               this.option.getValue().getRed(),
               this.option.getValue().getGreen(),
               this.option.getValue().getBlue(),
               this.option.getValue().getAlpha()
            )
         );
      this.updatingHex = false;
   }

   private enum DragTarget {
      NONE,
      HUE_SLIDER,
      SATURATION_VALUE_BOX,
      OPACITY_SLIDER;
   }
}
