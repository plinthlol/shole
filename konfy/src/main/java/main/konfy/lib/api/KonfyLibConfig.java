package main.konfy.lib.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import main.konfy.lib.core.config.impl.ModConfig;

public interface KonfyLibConfig {
   Map<Class<? extends KonfyLibConfig>, ModConfig> CACHE = new ConcurrentHashMap<>();

   ModConfig define();

   default ModConfig getOrCreateConfig() {
      return CACHE.computeIfAbsent((Class<? extends KonfyLibConfig>)this.getClass(), clazz -> this.define());
   }
}
