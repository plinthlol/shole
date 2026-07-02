package main.konfy.lib.core.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class MarqueeUtil {
   public static int tickCount = 0;

   public static String get(String full, int maxWidth, int interval) {
      Font textRenderer = Minecraft.getInstance().font;
      if (textRenderer.width(full) <= maxWidth) {
         return full;
      }

      String ellipsis = "...";
      int ellipsisWidth = textRenderer.width("...");
      int visibleWidth = maxWidth - ellipsisWidth;
      int[] widths = new int[full.length() + 1];

      for (int i = 0; i < full.length(); i++) {
         widths[i + 1] = widths[i] + textRenderer.width(full.substring(i, i + 1));
      }

      int mChars = 0;
      int i = 1;

      while (i <= full.length() && widths[i] <= visibleWidth) {
         mChars = i++;
      }

      i = full.length() - mChars + 1;
      if (i <= 1) {
         return full;
      }

      int cycle = i * 2 - 2;
      int pos = tickCount / interval % cycle;
      if (pos >= i) {
         pos = cycle - pos;
      }

      String visiblePart = full.substring(pos, pos + mChars);
      return pos == i - 1 ? visiblePart : visiblePart + "...";
   }
}
