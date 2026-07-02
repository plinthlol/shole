package main.konfy.lib.core.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import main.konfy.lib.api.KonfyLibApi;
import main.konfy.lib.core.config.impl.ModConfig;
import main.konfy.lib.core.gui.impl.APIScreen;
import main.konfy.lib.core.gui.impl.BaseScreen;
import main.konfy.lib.core.gui.impl.ConflictedConfigScreen;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuIntegration implements ModMenuApi {
   public ConfigScreenFactory<?> getModConfigScreenFactory() {
      return APIScreen::new;
   }

   @Override
   @SuppressWarnings("unchecked")
   public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
      return FabricLoader.getInstance().getEntrypointContainers("konfy", KonfyLibApi.class).stream()
         .map(c -> {
            KonfyLibApi entryPoint = (KonfyLibApi) c.getEntrypoint();
            BaseScreen overridableScreen = entryPoint.getOverridableScreen(null);
            ModConfig config = entryPoint.getConfig();
            if (overridableScreen == null && config == null) return null;
            Map.Entry<String, ConfigScreenFactory<?>> entry = Map.entry(
               c.getProvider().getMetadata().getId(),
               (ConfigScreenFactory<?>) parent -> {
                  BaseScreen screen = entryPoint.getOverridableScreen(parent);
                  if (screen != null && config != null) {
                     KonfyLibConfigScreen configScreen = new KonfyLibConfigScreen(parent, config, c.getProvider().getMetadata().getName());
                     return new ConflictedConfigScreen("Choose Screen", parent, screen, configScreen, entryPoint.getConflictedConfigButtonTitles());
                  } else {
                     return screen == null && config != null
                        ? new KonfyLibConfigScreen(parent, config, c.getProvider().getMetadata().getName())
                        : screen;
                  }
               }
            );
            return entry;
         })
         .filter(Objects::nonNull)
         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
   }
}
