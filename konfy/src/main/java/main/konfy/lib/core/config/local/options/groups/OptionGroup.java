package main.konfy.lib.core.config.local.options.groups;

import java.util.ArrayList;
import java.util.List;
import main.konfy.lib.core.config.local.Option;

public class OptionGroup {
   private final String name;
   private final List<Option<?>> options;
   private boolean isExpanded;

   private OptionGroup(String name, List<Option<?>> options, boolean expanded) {
      this.name = name;
      this.options = options;
      this.isExpanded = expanded;
   }

   public String getName() {
      return this.name;
   }

   public List<Option<?>> getOptions() {
      return this.options;
   }

   public boolean isExpanded() {
      return this.isExpanded;
   }

   public void setExpanded(boolean expanded) {
      this.isExpanded = expanded;
   }

   public void toggleExpanded() {
      this.isExpanded = !this.isExpanded;
   }

   public static OptionGroup.Builder createBuilder(String name) {
      return new OptionGroup.Builder(name);
   }

   public static class Builder {
      private final String name;
      private final List<Option<?>> options = new ArrayList<>();
      private boolean expanded = true;

      public Builder(String name) {
         this.name = name;
      }

      public OptionGroup.Builder addOption(Option<?> option) {
         this.options.add(option);
         return this;
      }

      public OptionGroup.Builder setExpanded(boolean expanded) {
         this.expanded = expanded;
         return this;
      }

      public OptionGroup build() {
         return new OptionGroup(this.name, this.options, this.expanded);
      }
   }
}
