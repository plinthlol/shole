package main.konfy.lib.core.config.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import main.konfy.lib.core.config.Config;
import main.konfy.lib.core.config.local.Category;
import main.konfy.lib.core.config.local.builders.LocalConfigBuilder;
import main.konfy.lib.core.config.serialization.SerializableCategory;
import main.konfy.lib.core.manager.KonfyLibConfigManager;

public record ModConfig(Path getPath, List<Category> categories, Runnable saveCallback) implements Config {
   @Override
   public void onLoad() {
      Path path = this.getPath();
      if (!Files.exists(path)) {
         this.onSave();
      } else {
         List<SerializableCategory> loadedCategories;
         try {
            String json = Files.readString(path);
            Type type = (new TypeToken<List<SerializableCategory>>() {}).getType();
            loadedCategories = (List<SerializableCategory>)KonfyLibConfigManager.GSON.fromJson(json, type);
         } catch (IOException | JsonParseException e) {
            System.err.println("Failed to read or parse config from " + path + ": " + e.getMessage());
            return;
         }

         for (Category existingCategory : this.categories()) {
            loadedCategories.stream()
               .filter(serialized -> serialized.name.equals(existingCategory.name()))
               .findFirst()
               .ifPresent(serialized -> KonfyLibConfigManager.applyCategoryValues(existingCategory, serialized));
         }
      }
   }

   @Override
   public void onSave() {
      Path path = this.getPath();
      List<SerializableCategory> serializedCategories = new ArrayList<>();

      for (Category category : this.categories()) {
         serializedCategories.add(KonfyLibConfigManager.serializeCategory(category));
      }

      try {
         Files.createDirectories(path.getParent());
         String json = KonfyLibConfigManager.GSON.toJson(serializedCategories);
         Files.writeString(path, json);
      } catch (IOException e) {
         System.err.println("Failed to save config to " + path + ": " + e.getMessage());
      }
   }

   public void runSave() {
      if (this.saveCallback != null) {
         this.saveCallback.run();
      }
   }

   public static LocalConfigBuilder createBuilder() {
      return new LocalConfigBuilder();
   }

   @Deprecated
   public static LocalConfigBuilder createBuilder(String ignored) {
      return new LocalConfigBuilder();
   }
}
