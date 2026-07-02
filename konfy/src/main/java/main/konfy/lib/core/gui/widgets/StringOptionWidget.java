package main.konfy.lib.core.gui.widgets;

import java.awt.Point;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.gui.Graphics;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import main.konfy.lib.core.gui.widgets.sub.TextboxSubWidget;
import main.konfy.lib.core.utils.MainColors;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public class StringOptionWidget extends OptionWidget {
   private final Option<String> option;
   private final TextboxSubWidget textbox;

   public StringOptionWidget(OptionGroup parent, KonfyLibConfigScreen screen, int x, int y, int width, int height, Option<String> option) {
      super(parent, screen, option, x, y, width, height, option.getName());
      this.option = option;
      this.textbox = new TextboxSubWidget(
         screen, this.getX() + this.getWidth() - 100, this.getTextYCentered() - 4, 90, 75, 10, option.getValue(), option::setValue, false
      );
   }

   @Override
   public void extract(Graphics graphics, int mouseX, int mouseY, float delta) {
      GuiGraphicsExtractor extractor = graphics.extractor();
      extractor.verticalLine(
         this.getX() + this.getWidth() - 103 - this.textbox.getScrollOffset(),
         this.getY(),
         this.getY() + this.height - 1,
         this.isHovered() ? MainColors.OUTLINE_WHITE_HOVERED.getRGB() : MainColors.OUTLINE_WHITE.getRGB()
      );
      this.textbox.extract(graphics, mouseX, mouseY, delta);
      this.textbox.hovered = this.isHovered();
   }

   @Override
   public void onMouseClick(MouseButtonEvent click, boolean doubled) {
      super.onMouseClick(click, doubled);
      this.textbox.onClick(click, doubled);
   }

   @Override
   public void onKeyPress(KeyEvent input) {
      super.onKeyPress(input);
      this.textbox.onKeyPress(input);
   }

   @Override
   public void onCharTyped(CharacterEvent input) {
      super.onCharTyped(input);
      this.textbox.onCharTyped(input);
   }

   @Override
   public void onWidgetUpdate() {
      this.textbox.setPos(new Point(this.getX() + this.getWidth() - 100, this.getTextYCentered() - 4));
   }

   @Override
   public <V> void onThirdPartyChange(V value) {
      super.onThirdPartyChange(value);
      this.textbox.setText(this.option.getValue());
   }
}
