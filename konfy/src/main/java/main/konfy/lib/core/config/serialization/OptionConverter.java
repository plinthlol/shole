package main.konfy.lib.core.config.serialization;

import com.google.gson.JsonElement;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.manager.KonfyLibConfigManager;

public class OptionConverter {
   public static SerializableOption fromOption(Option<?> opt) {
      SerializableOption s = new SerializableOption();
      s.name = opt.getName();
      s.type = opt.getType().getSimpleName().toLowerCase();
      s.value = KonfyLibConfigManager.GSON.toJsonTree(opt.getValue(), opt.getType());
      s.min = KonfyLibConfigManager.GSON.toJsonTree(opt.getMin());
      s.max = KonfyLibConfigManager.GSON.toJsonTree(opt.getMax());
      s.increment = KonfyLibConfigManager.GSON.toJsonTree(opt.getIncrement());
      return s;
   }

   public static void setOptionValue(Option<?> option, JsonElement valueElement) {
      Class<?> type = option.getType();

      try {
         Object value = KonfyLibConfigManager.GSON.fromJson(valueElement, type);
         if (value != null) {
            option.setValue(value);
         }
      } catch (Exception e) {
         System.err.println("Failed to deserialize value for option " + option.getName() + " to type " + type.getSimpleName() + ": " + e.getMessage());
      }
   }
}
