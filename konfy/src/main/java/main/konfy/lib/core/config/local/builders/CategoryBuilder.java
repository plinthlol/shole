package main.konfy.lib.core.config.local.builders;

import java.util.ArrayList;
import java.util.List;
import main.konfy.lib.core.config.local.Category;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;

public class CategoryBuilder {
   private final String name;
   private final List<OptionGroup> optionGroups = new ArrayList<>();
   private final List<Option<?>> options = new ArrayList<>();

   public CategoryBuilder(String name) {
      this.name = name;
   }

   public CategoryBuilder group(OptionGroup group) {
      this.optionGroups.add(group);
      return this;
   }

   public Category build() {
      return new Category(this.name, this.optionGroups, this.options);
   }
}
