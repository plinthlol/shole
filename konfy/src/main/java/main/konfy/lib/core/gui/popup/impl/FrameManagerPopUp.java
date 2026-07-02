package main.konfy.lib.core.gui.popup.impl;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.options.type.PixelGrid;
import main.konfy.lib.core.config.local.options.type.PixelGridAnimation;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.gui.popup.PopUp;
import main.konfy.lib.core.gui.widgets.ButtonWidget;
import main.konfy.lib.core.utils.MainColors;
import main.konfy.lib.core.utils.Scroller;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class FrameManagerPopUp extends PopUp {
   private Option<PixelGridAnimation> option;
   private final CopyOnWriteArrayList<ButtonWidget> buttons = new CopyOnWriteArrayList<>();
   private final Map<Integer, Point> framePreviewPositions = new HashMap<>();
   private final List<List<PixelGrid>> undoStack = new ArrayList<>();
   private final ButtonWidget undoButton;
   private final ButtonWidget undoAllButton;
   private final ButtonWidget doneButton;
   private final Scroller scroller;
   private final Consumer<PixelGrid> onGridRemoval;

   public FrameManagerPopUp(KonfyLibConfigScreen screen, Option<PixelGridAnimation> option, Runnable onDone, Consumer<PixelGrid> onGridRemoval) {
      super(screen, "Edit Frames", 200, 250);
      this.option = option;
      this.onGridRemoval = onGridRemoval;
      this.scroller = new Scroller(0.0, 2.0);
      this.undoButton = new ButtonWidget(this.x + 5, this.y + this.height - 21, 40, 16, false, "Undo", this::undo);
      this.undoAllButton = new ButtonWidget(this.x + 50, this.y + this.height - 21, 60, 16, false, "Undo All", this::undoAll);
      this.doneButton = new ButtonWidget(this.x + this.width - 51, this.y + this.height - 21, 40, 16, false, "Done", () -> {
         onDone.run();
         screen.popUp.close();
      });
      this.rebuildButtons();
   }

   private void rebuildButtons() {
      this.buttons.clear();
      this.framePreviewPositions.clear();
      List<PixelGrid> frames = this.option.getValue().getFrames();
      int centerX = this.x + this.width / 2 - 50;
      int startY = this.y + 32;
      int spacing = 50;

      for (int i = 0; i < frames.size(); i++) {
         int frameY = startY + i * 50;
         int finalI = i;
         ButtonWidget addBefore = new ButtonWidget(centerX + 30, frameY - 25, 40, 20, false, "+", () -> this.addFrameAt(finalI));
         this.buttons.add(addBefore);
         PixelGrid frame = frames.get(i);
         ButtonWidget removeBtn = new ButtonWidget(centerX - 25, frameY, 20, 20, false, "-", () -> this.removeFrame(frame));
         removeBtn.setOutlineColor(new Color(255, 0, 0, 130).getRGB(), new Color(255, 0, 0, 160).getRGB());
         this.buttons.add(removeBtn);
         ButtonWidget frameBtn = new ButtonWidget(centerX, frameY, 100, 20, false, "Frame " + (i + 1), null);
         frameBtn.overrideHover = true;
         this.buttons.add(frameBtn);
         this.framePreviewPositions.put(i, new Point(centerX + 110, frameY + 2));
      }

      int yAfterLast = startY + frames.size() * 50;
      ButtonWidget addLast = new ButtonWidget(centerX + 30, yAfterLast - 25, 40, 20, false, "+", () -> this.addFrameAt(frames.size()));
      this.buttons.add(addLast);
   }

   private List<PixelGrid> deepCopyFrames(List<PixelGrid> original) {
      List<PixelGrid> copy = new ArrayList<>();

      for (PixelGrid frame : original) {
         copy.add(frame.copy());
      }

      return copy;
   }

   private void addFrameAt(int index) {
      PixelGridAnimation animation = this.option.getValue();
      this.undoStack.add(this.deepCopyFrames(animation.getFrames()));
      PixelGrid newFrame = PixelGrid.create(15, 15).build();
      animation.getFrames().add(index, newFrame);
      this.updateFrameNumbers();
      this.rebuildButtons();
   }

   private void removeFrame(PixelGrid frame) {
      if (this.option.getValue().getFrames().size() != 1) {
         PixelGridAnimation animation = this.option.getValue();
         this.undoStack.add(this.deepCopyFrames(animation.getFrames()));
         animation.getFrames().remove(frame);
         if (this.onGridRemoval != null) {
            this.onGridRemoval.accept(frame);
         }

         this.updateFrameNumbers();
         this.rebuildButtons();
         this.updateScrollerBounds();
      }
   }

   private void updateFrameNumbers() {
   }

   @Override
   protected void onClose() {
   }

   private void undo() {
      PixelGridAnimation animation = this.option.getValue();
      if (!this.undoStack.isEmpty()) {
         List<PixelGrid> lastState = this.undoStack.remove(this.undoStack.size() - 1);
         animation.getFrames().clear();
         animation.getFrames().addAll(lastState);
         this.updateFrameNumbers();
         this.rebuildButtons();
      }
   }

   private void undoAll() {
      PixelGridAnimation animation = this.option.getValue();
      if (!this.undoStack.isEmpty()) {
         List<PixelGrid> firstState = this.undoStack.get(0);
         this.undoStack.clear();
         animation.getFrames().clear();
         animation.getFrames().addAll(firstState);
         this.updateFrameNumbers();
         this.rebuildButtons();
      }
   }

   @Override
   public void extract(Graphics graphics, double mouseX, double mouseY, float delta) {
      super.extract(graphics, mouseX, mouseY, delta);
      GuiGraphicsExtractor extractor = graphics.extractor();
      extractor.enableScissor(this.x, this.y + 2, this.x + this.width, this.y + this.height - 25);

      for (ButtonWidget btn : this.buttons) {
         btn.scrollY = (float)this.scroller.getValue();
         if (btn.getMessage().getString().equals("-")) {
            btn.setEnabled(this.option.getValue().getFrames().size() != 1);
         }

         btn.extractRenderState(extractor, (int)mouseX, (int)mouseY, delta);
      }

      List<PixelGrid> frames = this.option.getValue().getFrames();

      for (Entry<Integer, Point> entry : this.framePreviewPositions.entrySet()) {
         int index = entry.getKey();
         if (index >= 0 && index < frames.size()) {
            PixelGrid grid = frames.get(index);
            Point pos = entry.getValue();
            graphics.renderGridTexture(grid, pos.x - 1, (int)(pos.y - 6 - this.scroller.getValue()), 2, 0, false);
         }
      }

      extractor.disableScissor();
      extractor.horizontalLine(this.x + 2, this.x + this.width - 3, this.y + this.height - 25, MainColors.OUTLINE_WHITE.getRGB());
      this.doneButton.extractRenderState(extractor, (int)mouseX, (int)mouseY, delta);
      this.undoButton.extractRenderState(extractor, (int)mouseX, (int)mouseY, delta);
      this.undoAllButton.extractRenderState(extractor, (int)mouseX, (int)mouseY, delta);
   }

   @Override
   public void onClick(MouseButtonEvent click, boolean doubled) {
      for (ButtonWidget btn : this.buttons) {
         btn.onClick(click, doubled);
      }

      this.undoButton.onClick(click, doubled);
      this.undoAllButton.onClick(click, doubled);
      this.doneButton.onClick(click, doubled);
   }

   @Override
   public void onScroll(double mouseX, double mouseY, double verticalAmount) {
      super.onScroll(mouseX, mouseY, verticalAmount);
      this.scroller.onScroll(verticalAmount);
      this.updateScrollerBounds();
   }

   @Override
   public void layout(int x1, int y1) {
      super.layout(x1, y1);
      if (this.buttons != null && this.loaded) {
         this.rebuildButtons();
         this.undoButton.setPosition(this.x + 5, this.y + this.height - 21);
         this.undoAllButton.setPosition(this.x + 50, this.y + this.height - 21);
         this.doneButton.setPosition(this.x + this.width - 51, this.y + this.height - 21);
      }
   }

   private void updateScrollerBounds() {
      if (this.buttons.isEmpty()) {
         this.scroller.setBounds(0.0, 0.0);
      } else {
         this.scroller.setBounds(0.0, Math.max(0, this.y + 32 + this.buttons.size() / 3 * 50 - (this.y + this.height - 25)));
      }
   }
}
