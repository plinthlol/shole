package main.konfy.lib.core.config.serialization;

import com.google.gson.JsonElement;

public class SerializableOption {
   public String name;
   public String type;
   public JsonElement value;
   public JsonElement min;
   public JsonElement max;
   public JsonElement increment;
}
