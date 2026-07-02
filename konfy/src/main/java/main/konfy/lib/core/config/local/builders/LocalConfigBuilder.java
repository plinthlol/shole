package main.konfy.lib.core.config.local.builders;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import main.konfy.lib.core.config.impl.ModConfig;
import main.konfy.lib.core.config.local.Category;

public class LocalConfigBuilder {
   private final List<Category> categories = new ArrayList<>();
   private Path path;
   private Runnable onSave;

   public LocalConfigBuilder path(Path path) {
      this.path = path;
      return this;
   }

   public LocalConfigBuilder category(Category categoryBuilder) {
      this.categories.add(categoryBuilder);
      return this;
   }

   public LocalConfigBuilder onSave(Runnable onSave) {
      this.onSave = onSave;
      return this;
   }

   public ModConfig build() {
      if (this.path == null) {
         throw new IllegalStateException("Missing required .path()");
      } else {
         return new ModConfig(this.path, this.categories, this.onSave);
      }
   }
}
