package main.konfy.lib.core.gui.utils;

import java.util.List;
import main.konfy.lib.core.config.local.Category;
import main.konfy.lib.core.gui.widgets.OptionGroupWidget;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.network.chat.Component;

public class CategoryTab extends GridLayoutTab {
   private final List<OptionGroupWidget> optionGroupWidgets;
   private final Category category;

   public CategoryTab(Category category, List<OptionGroupWidget> optionGroupWidgets) {
      super(Component.literal(category.name()));
      this.optionGroupWidgets = optionGroupWidgets;
      this.category = category;
   }

   public List<OptionGroupWidget> getOptionGroupWidgets() {
      return this.optionGroupWidgets;
   }

   public Category getCategory() {
      return this.category;
   }
}
