package main.konfy.lib.core.gui.widgets;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.config.local.options.type.PixelGrid;
import main.konfy.lib.core.config.local.options.type.PixelGridAnimation;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.HudEditorScreen;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.gui.popup.impl.FrameManagerPopUp;
import main.konfy.lib.core.gui.popup.impl.GridEditorPopUp;
import main.konfy.lib.core.gui.widgets.sub.SliderSubWidget;
import main.konfy.lib.core.gui.widgets.sub.adaptor.FloatSliderAdapter;
import main.konfy.lib.core.gui.widgets.sub.adaptor.IntSliderAdapter;
import main.konfy.lib.core.utils.MainColors;
import main.konfy.lib.core.utils.ScreenGlobals;
import main.konfy.lib.core.utils.Scroller;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;

public class PixelGridAnimationWidget extends OpenableWidget {
   public final ButtonWidget editHudButton;
   public final ButtonWidget editFrameButton;
   public final ButtonWidget viewFrames;
   private final Option<PixelGridAnimation> option;
   private final SliderSubWidget<Integer> animationSpeedSlider;
   private final SliderSubWidget<Float> frameSize;
   private final List<ButtonWidget> buttonFrames = new ArrayList<>();
   private PixelGrid viewingGrid;
   private final Scroller scroller;
   private int frameToReplace = -1;
   private boolean draggingScroller = false;
   private int dragOffsetY = 0;

   public PixelGridAnimationWidget(OptionGroup parent, KonfyLibConfigScreen screen, int x, int y, int width, int height, Option<PixelGridAnimation> option) {
      super(parent, screen, option, x, y, width, height, option.getName(), ScreenGlobals.OPTION_HEIGHT * 6);
      this.option = option;
      this.scroller = new Scroller(0.0, 2.0);
      this.editHudButton = new ButtonWidget(this.getX() + this.getWidth() - 92, this.getY() + 3, 50, 14, false, "Edit Hud", () -> this.handleEditHudButtonClick(screen));
      this.editFrameButton = new ButtonWidget(this.getX() + this.getWidth() - 142, this.getY() + 101, 60, 14, false, "Edit Frame", this::handleEditFrameButtonClick);
      this.viewFrames = new ButtonWidget(this.getX() + 70, this.getY() + 101, 77, 14, false, "View Frames", this::handleViewFramesButtonClick);
      this.setupFrames(-1, true);
      List<PixelGrid> frames = option.getValue().getFrames();
      if (!frames.isEmpty()) {
         this.viewingGrid = frames.get(0).copy();
      }

      this.animationSpeedSlider = new SliderSubWidget<>(
         this.getX() + 75,
         this.getY() + 38,
         100,
         ScreenGlobals.OPTION_HEIGHT - 12,
         new IntSliderAdapter(0, 20, option.getValue().getAnimationSpeed()),
         option.getValue().getAnimationSpeed(),
         option.getValue()::setAnimationSpeed,
         true
      );
      this.frameSize = new SliderSubWidget<>(
         this.getX() + 75,
         this.getY() + 68,
         100,
         ScreenGlobals.OPTION_HEIGHT - 12,
         new FloatSliderAdapter(0.0F, 10.0F, option.getValue().getSize()),
         option.getValue().getSize(),
         option.getValue()::setSize,
         true
      );
   }

   @Override
   public void extract(Graphics graphics, int mouseX, int mouseY, float delta) {
      GuiGraphicsExtractor extractor = graphics.extractor();
      super.extract(graphics, mouseX, mouseY, delta);
      this.animationSpeedSlider.setOnChange(this.option.getValue()::setAnimationSpeed);
      this.frameSize.setOnChange(this.option.getValue()::setSize);
      if (this.option.getValue().getOffsetX() != -1.0 && this.option.getValue().getOffsetY() != -1.0) {
         this.editHudButton.extractWidgetRenderState(extractor, mouseX, mouseY, delta);
      }

      extractor.verticalLine(
         this.getX() + this.getWidth() - 38,
         this.getY(),
         this.getY() + ScreenGlobals.OPTION_HEIGHT - 1,
         this.isHovered() ? MainColors.OUTLINE_WHITE_HOVERED.getRGB() : MainColors.OUTLINE_WHITE.getRGB()
      );
      if (!this.fullyClosed()) {
         this.viewFrames.extractWidgetRenderState(extractor, mouseX, mouseY, delta);
         this.editFrameButton.extractWidgetRenderState(extractor, mouseX, mouseY, delta);
         this.animationSpeedSlider.extract(graphics, mouseX, mouseY, delta);
         this.frameSize.extract(graphics, mouseX, mouseY, delta);
         this.screen.scroll = !this.isHoveredFrameSelector();
         extractor.horizontalLine(
            this.getX() + 1,
            this.getX() + this.getWidth() - 2,
            this.getY() + ScreenGlobals.OPTION_HEIGHT - 1,
            this.isHovered() ? MainColors.OUTLINE_WHITE_HOVERED.getRGB() : MainColors.OUTLINE_WHITE.getRGB()
         );
         extractor.centeredText(this.screen.getFont(), "Frame " + this.frameToReplace + " Grid", this.getX() + this.getWidth() - 53, this.getY() + 23, -1);
         extractor.text(this.screen.getFont(), "Animation Speed", this.getX() + 75, this.getY() + 28, Color.LIGHT_GRAY.getRGB(), true);
         extractor.text(this.screen.getFont(), "Size", this.getX() + 75, this.getY() + 58, Color.LIGHT_GRAY.getRGB(), true);
         extractor.pose().pushMatrix();
         float scale = 0.6F;
         extractor.pose().scale(0.6F, 0.6F);
         if (this.viewingGrid != null) {
            graphics.renderGridOutline(
               this.viewingGrid, (int)((this.getX() + this.getWidth() - 96) / 0.6F), (int)((this.getY() + 37) / 0.6F), 7, 2, MainColors.OUTLINE_WHITE.getRGB(), true
            );
         }

         extractor.pose().popMatrix();
         this.drawScrollableFrameSelector(extractor, mouseX, mouseY, delta);
      }

      if (this.option.getValue().getCurrentFrame() != null) {
         graphics.renderGridTexture(this.option.getValue().getCurrentFrame(), this.getX() + this.getWidth() - 26, this.getY() + 3, 1, 0, false);
      }
   }

   private void drawScrollableFrameSelector(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
      extractor.verticalLine(
         this.getX() + 60, this.getY() + ScreenGlobals.OPTION_HEIGHT - 1, this.getY() + this.getHeight() - 1, MainColors.OUTLINE_WHITE.getRGB()
      );
      extractor.verticalLine(
         this.getX() + 65, this.getY() + ScreenGlobals.OPTION_HEIGHT - 1, this.getY() + this.getHeight() - 1, MainColors.OUTLINE_WHITE.getRGB()
      );
      int trackHeight = this.getHeight() - ScreenGlobals.OPTION_HEIGHT;
      int contentHeight = this.buttonFrames.size() * 23;
      int handleHeight = Mth.clamp(trackHeight * trackHeight / Math.max(trackHeight, contentHeight), 10, trackHeight);
      int handleY = this.getY()
         + ScreenGlobals.OPTION_HEIGHT
         - 1
         + (int)(this.scroller.getValue() * (trackHeight - handleHeight) / Math.max(1, contentHeight - trackHeight));
      extractor.fill(this.getX() + 61, handleY + 1, this.getX() + 65, handleY + handleHeight, new Color(210, 210, 210).getRGB());
      extractor.enableScissor(this.getX(), this.getY() + ScreenGlobals.OPTION_HEIGHT, this.getX() + 60, this.getY() + this.OPEN_HEIGHT - 1);

      for (ButtonWidget btn : this.buttonFrames) {
         btn.hovered = this.isHoveredFrameSelector();
         btn.scrollY = (float)this.scroller.getValue();
         btn.extractWidgetRenderState(extractor, mouseX, mouseY, delta);
      }

      extractor.disableScissor();
   }

   @Override
   public void onMouseClick(MouseButtonEvent click, boolean doubled) {
      super.onMouseClick(click, doubled);
      if (this.option.getValue().getOffsetX() != -1.0 && this.option.getValue().getOffsetY() != -1.0) {
         this.editHudButton.onClick(click, doubled);
      }

      this.editFrameButton.onClick(click, doubled);
      this.viewFrames.onClick(click, doubled);
      int handleY = this.getY() + ScreenGlobals.OPTION_HEIGHT + (int)this.scroller.getValue();
      if (this.isHoveringScroller()) {
         this.draggingScroller = true;
         this.dragOffsetY = this.mouseY - handleY;
      }

      this.animationSpeedSlider.onClick(click, doubled);
      this.frameSize.onClick(click, doubled);

      for (ButtonWidget btn : this.buttonFrames) {
         btn.onClick(click, doubled);
      }
   }

   @Override
   public void onMouseRelease(MouseButtonEvent click) {
      super.onMouseRelease(click);
      this.animationSpeedSlider.release();
      this.frameSize.release();
      this.draggingScroller = false;
   }

   @Override
   public void onMouseDrag(MouseButtonEvent click, double deltaX, double deltaY) {
      super.onMouseDrag(click, deltaX, deltaY);
      if (this.draggingScroller) {
         int trackStart = this.getY() + ScreenGlobals.OPTION_HEIGHT - 1;
         int trackHeight = this.getHeight() - ScreenGlobals.OPTION_HEIGHT;
         int contentHeight = this.buttonFrames.size() * 23;
         int handleHeight = Mth.clamp(trackHeight * trackHeight / Math.max(trackHeight, contentHeight), 10, trackHeight);
         int newValue = (int)((float)((this.mouseY - this.dragOffsetY - trackStart) * (contentHeight - trackHeight)) / (trackHeight - handleHeight));
         this.scroller.setValue(Mth.clamp(newValue, 0, Math.max(0, contentHeight - trackHeight)));
      }

      this.animationSpeedSlider.onDrag(this.mouseX);
      this.frameSize.onDrag(this.mouseX);
   }

   @Override
   public void onMouseScroll(double mouseX, double mouseY, double verticalAmount) {
      super.onMouseScroll(mouseX, mouseY, verticalAmount);
      if (this.isHoveredFrameSelector()) {
         this.scroller.onScroll(verticalAmount);
         this.scroller.setBounds(0.0, Math.max(0, this.buttonFrames.size() * 23 - (this.getHeight() - ScreenGlobals.OPTION_HEIGHT)));
      }
   }

   @Override
   public void onWidgetUpdate() {
      this.editHudButton.setPosition(this.getX() + this.getWidth() - 92, this.getY() + 3);
      this.viewFrames.setPosition(this.getX() + 70, this.getY() + 101);
      this.editFrameButton.setPosition(this.getX() + this.getWidth() - 142, this.getY() + 101);
      this.updateButtons();
      this.scroller.setBounds(0.0, Math.max(0, this.buttonFrames.size() * 23 - (this.getHeight() - ScreenGlobals.OPTION_HEIGHT)));
      this.animationSpeedSlider.setPos(new Point(this.getX() + 75, this.getY() + 38));
      this.frameSize.setPos(new Point(this.getX() + 75, this.getY() + 68));
   }

   private void handleEditHudButtonClick(KonfyLibConfigScreen parent) {
      Minecraft.getInstance().setScreenAndShow(new HudEditorScreen(parent, this.option));
   }

   private void handleEditFrameButtonClick() {
      if (this.viewingGrid != null) {
         this.screen.popUp = new GridEditorPopUp(this.screen, this.viewingGrid.copy(), newGrid -> {
            this.option.setValue(PixelGridAnimation.replace(this.option.getValue(), newGrid, this.frameToReplace));
            this.viewingGrid = newGrid.copy();
            this.setupFrames(this.frameToReplace, false);
         }, this.frameToReplace);
      }
   }

   private void handleViewFramesButtonClick() {
      this.screen.popUp = new FrameManagerPopUp(this.screen, this.option, () -> {
         this.setupFrames(this.frameToReplace, false);
         PixelGrid frame = this.option.getValue().getFrame(this.frameToReplace);
         PixelGrid grid;
         if (frame != null) {
            grid = frame.copy();
         } else {
            grid = this.option.getValue().getFrames().get(0).copy();
         }

         this.viewingGrid = grid;
      }, grid -> this.option.getValue().setCurrentFrame(0));
   }

   @Override
   public boolean isHovered() {
      return this.mouseX >= this.getX()
         && this.mouseX < this.getX() + this.getWidth() - 6
         && this.mouseY >= this.getY()
         && this.mouseY < this.getY() + ScreenGlobals.OPTION_HEIGHT
         && !this.editHudButton.isHovered();
   }

   @Override
   protected void onOpen(boolean prev) {
      if (prev) {
         this.screen.scroll = true;
      }
   }

   public boolean isHoveredFrameSelector() {
      int xStart = this.getX();
      int xEnd = this.getX() + 65;
      int yStart = this.getY();
      int yEnd = this.getY() + this.getHeight() - 1;
      return this.mouseX >= xStart && this.mouseX <= xEnd && this.mouseY >= yStart && this.mouseY <= yEnd;
   }

   private boolean isHoveringScroller() {
      int trackHeight = this.getHeight() - ScreenGlobals.OPTION_HEIGHT;
      int contentHeight = this.buttonFrames.size() * 23;
      int handleHeight = Mth.clamp(trackHeight * trackHeight / Math.max(trackHeight, contentHeight), 10, trackHeight);
      int handleY = this.getY()
         + ScreenGlobals.OPTION_HEIGHT
         - 1
         + (int)(this.scroller.getValue() * (trackHeight - handleHeight) / Math.max(1, contentHeight - trackHeight));
      int scrollerX = this.getX() + 61;
      return this.mouseX >= scrollerX && this.mouseX <= scrollerX + 4 && this.mouseY >= handleY + 1 && this.mouseY <= handleY + handleHeight;
   }

   public void updateButtons() {
      int yOffset = 0;

      for (ButtonWidget btn : this.buttonFrames) {
         btn.setPosition(this.getX() + 5, this.getY() + 23 + yOffset);
         yOffset += 23;
      }
   }

   public void setupFrames(int hoverFrame, boolean reset) {
      this.buttonFrames.clear();
      if (reset) {
         this.resetViewingGrid();
      }

      List<PixelGrid> frames = this.option.getValue().getFrames();

      for (int i = 0; i < frames.size(); i++) {
         int frameIndex = i;
         ButtonWidget btn = new ButtonWidget(this.getX() + 5, this.getY() + 23 + i * 23, 51, 18, false, "Frame " + (frameIndex + 1), null);
         if (frameIndex == 0 && hoverFrame == -1) {
            btn.overrideHover = true;
            this.frameToReplace = 1;
         } else if (hoverFrame - 1 == frameIndex) {
            btn.overrideHover = true;
         }

         btn.setListener(() -> {
            this.buttonFrames.forEach(b -> b.overrideHover = b.isHovered());
            this.viewingGrid = frames.get(frameIndex).copy();
            this.frameToReplace = frameIndex + 1;
         });
         this.buttonFrames.add(btn);
      }
   }

   @Override
   protected void handleResetButtonClick() {
      super.handleResetButtonClick();
      this.reset();
   }

   @Override
   public <V> void onThirdPartyChange(V value) {
      super.onThirdPartyChange(value);
      this.animationSpeedSlider.setValue(this.option.getValue().getAnimationSpeed());
      this.frameSize.setValue(this.option.getValue().getSize());
   }

   public void reset() {
      this.resetViewingGrid();
      this.setupFrames(-1, true);
   }

   private void resetViewingGrid() {
      if (this.frameToReplace > 0) {
         this.viewingGrid = this.option.getValue().getFrame(1).copy();
      }
   }
}
