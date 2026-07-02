package main.konfy.lib.core.config.local;

import java.util.List;
import java.util.Objects;
import main.konfy.lib.core.config.local.builders.CategoryBuilder;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;

public record Category(String name, List<OptionGroup> optionGroups, List<Option<?>> options) {
   public static CategoryBuilder createBuilder(String name) {
      return new CategoryBuilder(name);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Category category = (Category)o;
         return Objects.equals(this.name(), category.name());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.name());
   }
}
