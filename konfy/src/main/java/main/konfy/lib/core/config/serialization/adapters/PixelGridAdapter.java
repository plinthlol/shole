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
import main.konfy.lib.core.config.local.options.type.PixelGrid;

public class PixelGridAdapter implements JsonSerializer<PixelGrid>, JsonDeserializer<PixelGrid> {
   public JsonElement serialize(PixelGrid grid, Type type, JsonSerializationContext jsonSerializationextractor) {
      JsonObject obj = new JsonObject();
      obj.addProperty("width", grid.getWidth());
      obj.addProperty("height", grid.getHeight());
      JsonArray rows = new JsonArray();

      for (boolean[] row : grid.getPixels()) {
         JsonArray jsonRow = new JsonArray();

         for (boolean pixel : row) {
            jsonRow.add(pixel);
         }

         rows.add(jsonRow);
      }

      obj.add("pixels", rows);
      return obj;
   }

   public PixelGrid deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationextractor) throws JsonParseException {
      JsonObject obj = jsonElement.getAsJsonObject();
      int width = obj.get("width").getAsInt();
      int height = obj.get("height").getAsInt();
      JsonArray rows = obj.getAsJsonArray("pixels");
      boolean[][] pixels = new boolean[height][width];

      for (int y = 0; y < rows.size(); y++) {
         JsonArray row = rows.get(y).getAsJsonArray();

         for (int x = 0; x < row.size(); x++) {
            pixels[y][x] = row.get(x).getAsBoolean();
         }
      }

      return new PixelGrid(width, height, pixels);
   }
}
