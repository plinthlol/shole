package main.konfy.lib.api;

import main.konfy.lib.core.config.impl.ModConfig;
import main.konfy.lib.core.gui.impl.BaseScreen;
import net.minecraft.client.gui.screens.Screen;

public interface KonfyLibApi {
   default ModConfig getConfig() {
      return null;
   }

   default BaseScreen getOverridableScreen(Screen parent) {
      return null;
   }

   default String[] getConflictedConfigButtonTitles() {
      return new String[]{"Screen", "Config"};
   }
}
