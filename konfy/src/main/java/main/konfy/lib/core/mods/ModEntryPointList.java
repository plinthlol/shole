package main.konfy.lib.core.mods;

import java.util.ArrayList;
import main.konfy.lib.api.KonfyLibApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModEntryPointList {
   private final ArrayList<Mod> entries = new ArrayList<>();

   public void retrieve() {
      this.entries.clear();
      FabricLoader.getInstance().getEntrypointContainers("konfy", KonfyLibApi.class).forEach(entry -> {
         KonfyLibApi api = (KonfyLibApi)entry.getEntrypoint();
         this.entries.add(new Mod(entry.getProvider(), api.getConfig(), api::getOverridableScreen, api.getConflictedConfigButtonTitles()));
      });
   }

   public ArrayList<Mod> get() {
      return this.entries;
   }

   public void loadModConfigs() {
      for (Mod mod : this.entries) {
         if (mod.hasConfig()) {
            mod.getConfig().onLoad();
         }
      }
   }
}
