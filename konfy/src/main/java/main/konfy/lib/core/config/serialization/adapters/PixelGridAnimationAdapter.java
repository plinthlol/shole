package main.konfy.lib.core.config.serialization.adapters;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import main.konfy.lib.core.config.local.options.type.PixelGrid;
import main.konfy.lib.core.config.local.options.type.PixelGridAnimation;

public class PixelGridAnimationAdapter implements JsonSerializer<PixelGridAnimation>, JsonDeserializer<PixelGridAnimation> {
   public JsonElement serialize(PixelGridAnimation src, Type typeOfSrc, JsonSerializationContext extractor) {
      JsonObject obj = new JsonObject();
      JsonArray frames = new JsonArray();

      for (PixelGrid frame : src.getFrames()) {
         frames.add(extractor.serialize(frame));
      }

      obj.add("frames", frames);
      obj.addProperty("animationSpeed", src.getAnimationSpeed());
      obj.addProperty("size", src.getSize());
      JsonObject pos = new JsonObject();
      pos.addProperty("x", src.getOffsetX());
      pos.addProperty("y", src.getOffsetY());
      obj.add("position", pos);
      return obj;
   }

   public PixelGridAnimation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext extractor) throws JsonParseException {
      JsonObject obj = json.getAsJsonObject();
      JsonArray frameArray = obj.getAsJsonArray("frames");
      List<PixelGrid> frames = new ArrayList<>();

      for (JsonElement elem : frameArray) {
         frames.add((PixelGrid)extractor.deserialize(elem, PixelGrid.class));
      }

      PixelGridAnimation animation = new PixelGridAnimation(frames);
      if (obj.has("animationSpeed")) {
         animation.setAnimationSpeed(obj.get("animationSpeed").getAsInt());
      }

      if (obj.has("size")) {
         animation.setSize(obj.get("size").getAsFloat());
      }

      if (obj.has("position")) {
         JsonObject posObj = obj.getAsJsonObject("position");
         animation.setOffset(posObj.get("x").getAsDouble(), posObj.get("y").getAsDouble());
      }

      return animation;
   }
}
