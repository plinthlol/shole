package main.konfy.lib.core;

import java.util.ArrayList;
import java.util.List;
import main.konfy.lib.core.config.local.Category;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.config.local.options.type.Tickable;
import main.konfy.lib.core.mods.Mod;
import main.konfy.lib.core.mods.ModEntryPointList;

public class KonfyLib {
   private static KonfyLib instance;
   private ModEntryPointList modEntryPointList;
   private List<Tickable> tickableOptions;

   public void setup() {
      this.modEntryPointList = new ModEntryPointList();
      this.tickableOptions = new ArrayList<>();
   }

   public void load() {
      this.modEntryPointList.retrieve();
      this.modEntryPointList.loadModConfigs();
      this.retrieveTickableOptions();
   }

   public void tick() {
      for (Tickable tickable : this.tickableOptions) {
         tickable.tick();
      }
   }

   public void retrieveTickableOptions() {
      for (Mod mod : this.modEntryPointList.get()) {
         if (mod.hasConfig()) {
            for (Category category : mod.getConfig().categories()) {
               for (OptionGroup group : category.optionGroups()) {
                  for (Option<?> option : group.getOptions()) {
                     if (option.getValue() instanceof Tickable tickable) {
                        this.tickableOptions.add(tickable);
                     }
                  }
               }
            }
         }
      }
   }

   public static KonfyLib getInstance() {
      if (instance == null) {
         instance = new KonfyLib();
      }

      return instance;
   }

   private KonfyLib() {
   }
}
