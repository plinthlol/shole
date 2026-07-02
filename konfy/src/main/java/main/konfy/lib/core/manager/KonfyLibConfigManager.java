package main.konfy.lib.core.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import main.konfy.lib.core.config.impl.ModConfig;
import main.konfy.lib.core.config.local.Category;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.config.local.options.type.PixelGrid;
import main.konfy.lib.core.config.local.options.type.PixelGridAnimation;
import main.konfy.lib.core.config.local.options.type.KonfyLibColor;
import main.konfy.lib.core.config.serialization.OptionConverter;
import main.konfy.lib.core.config.serialization.SerializableCategory;
import main.konfy.lib.core.config.serialization.SerializableGroup;
import main.konfy.lib.core.config.serialization.SerializableOption;
import main.konfy.lib.core.config.serialization.adapters.ColorTypeAdapter;
import main.konfy.lib.core.config.serialization.adapters.PixelGridAdapter;
import main.konfy.lib.core.config.serialization.adapters.PixelGridAnimationAdapter;

public class KonfyLibConfigManager {
   public static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(KonfyLibColor.class, new ColorTypeAdapter())
      .registerTypeAdapter(PixelGrid.class, new PixelGridAdapter())
      .registerTypeAdapter(PixelGridAnimation.class, new PixelGridAnimationAdapter())
      .serializeNulls()
      .setPrettyPrinting()
      .create();
   private final ModConfig localConfig;

   public KonfyLibConfigManager(ModConfig localConfig) {
      this.localConfig = localConfig;
   }

   public ModConfig get() {
      return this.localConfig;
   }

   public static SerializableCategory serializeCategory(Category category) {
      SerializableCategory serialized = new SerializableCategory();
      serialized.name = category.name();
      serialized.options = new ArrayList<>();
      serialized.groups = new ArrayList<>();

      for (Option<?> option : category.options()) {
         if (option.getType() != Runnable.class) {
            serialized.options.add(OptionConverter.fromOption(option));
         }
      }

      for (OptionGroup group : category.optionGroups()) {
         SerializableGroup serializedGroup = new SerializableGroup();
         serializedGroup.name = group.getName();
         serializedGroup.expanded = group.isExpanded();
         serializedGroup.options = new ArrayList<>();

         for (Option<?> option : group.getOptions()) {
            if (option.getType() != Runnable.class) {
               serializedGroup.options.add(OptionConverter.fromOption(option));
            }
         }

         serialized.groups.add(serializedGroup);
      }

      return serialized;
   }

   public static void applyCategoryValues(Category category, SerializableCategory serializedCategory) {
      for (Option<?> option : category.options()) {
         serializedCategory.options
            .stream()
            .filter(serialized -> serialized.name.equals(option.getName()))
            .findFirst()
            .ifPresent(serialized -> applyOptionValues(option, serialized));
      }

      for (OptionGroup group : category.optionGroups()) {
         serializedCategory.groups
            .stream()
            .filter(serialized -> serialized.name.equals(group.getName()))
            .findFirst()
            .ifPresent(
               serializedGroup -> {
                  group.setExpanded(serializedGroup.expanded);

                  for (Option<?> option : group.getOptions()) {
                     serializedGroup.options
                        .stream()
                        .filter(serialized -> serialized.name.equals(option.getName()))
                        .findFirst()
                        .ifPresent(serialized -> applyOptionValues(option, serialized));
                  }
               }
            );
      }
   }

   public static void applyOptionValues(Option<?> option, SerializableOption serialized) {
      try {
         OptionConverter.setOptionValue(option, serialized.value);
      } catch (Exception e) {
         System.err.println("Failed to set value for option '" + option.getName() + "': " + e.getMessage());
      }
   }
}
