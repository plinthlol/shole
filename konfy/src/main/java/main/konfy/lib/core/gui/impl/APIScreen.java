package main.konfy.lib.core.gui.impl;

import main.konfy.lib.core.config.impl.DemoConfig;
import net.minecraft.client.gui.screens.Screen;

public class APIScreen extends KonfyLibConfigScreen {
   public APIScreen(Screen parent) {
      super(parent, DemoConfig.createDemoConfig(), "Demo Showcase");
      this.showModsTab = true;
   }
}
