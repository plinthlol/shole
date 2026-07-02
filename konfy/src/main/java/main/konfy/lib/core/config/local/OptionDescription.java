package main.konfy.lib.core.config.local;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class OptionDescription {
   private final OptionDescription.OptionType type;
   private final BiConsumer<GuiGraphicsExtractor, OptionDescription.OptionPanel> renderConsumer;
   private final Supplier<String> textSupplier;

   private OptionDescription(
      OptionDescription.OptionType type, BiConsumer<GuiGraphicsExtractor, OptionDescription.OptionPanel> renderConsumer, Supplier<String> textSupplier
   ) {
      this.type = type;
      this.renderConsumer = renderConsumer;
      this.textSupplier = textSupplier;
   }

   public static OptionDescription ofRender2D(BiConsumer<GuiGraphicsExtractor, OptionDescription.OptionPanel> renderConsumer) {
      return new OptionDescription(OptionDescription.OptionType.RENDER, renderConsumer, null);
   }

   public static OptionDescription ofOrderedString(Supplier<String> textSupplier) {
      return new OptionDescription(OptionDescription.OptionType.TEXT, null, textSupplier);
   }

   public OptionDescription.OptionType getType() {
      return this.type;
   }

   public BiConsumer<GuiGraphicsExtractor, OptionDescription.OptionPanel> getRenderConsumer() {
      return this.renderConsumer;
   }

   public Supplier<String> getStringSupplier() {
      return this.textSupplier;
   }

   public record OptionPanel(int x, int y, int width, int height) {
      public int endX() {
         return this.x + this.width;
      }

      public int endY() {
         return this.y + this.height;
      }
   }

   public enum OptionType {
      RENDER,
      TEXT;
   }
}
