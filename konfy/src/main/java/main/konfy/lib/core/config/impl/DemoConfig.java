package main.konfy.lib.core.config.impl;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import main.konfy.lib.core.config.impl.ModConfig;
import main.konfy.lib.core.config.local.Category;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.OptionDescription;
import main.konfy.lib.core.config.local.options.*;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.config.local.options.type.*;

public class DemoConfig {

   // Stored memory values for the showcase options
   public static boolean booleanVal = true;
   public static int intVal = 42;
   public static double doubleVal = 3.14;
   public static float floatVal = 1.5F;
   public static KonfyLibColor colorVal = new KonfyLibColor(0, 120, 255, 255);
   public static PixelGridAnimation animVal = new PixelGridAnimation(PixelGrid.create().set(7, 7).build());
   public static List<String> listVal = new ArrayList<>(Arrays.asList("Item A", "Item B", "Item C"));
   public static String stringVal = "Hello, Konfy!";
   public static DemoEnum enumVal = DemoEnum.MEDIUM;

   public enum DemoEnum {
      EASY,
      MEDIUM,
      HARD,
      IMPOSSIBLE
   }

   public static ModConfig createDemoConfig() {

      // Define different types of configuration options
      Option<Boolean> booleanOption = BooleanOption.createBuilder(
            "Boolean Option",
            () -> booleanVal,
            true,
            val -> booleanVal = val
         )
         .description(OptionDescription.ofOrderedString(() -> "Toggle boolean settings."))
         .build();

      Option<Integer> intOption = NumericalOption.createBuilder(
            "Integer Option",
            () -> intVal,
            42,
            val -> intVal = val
         )
         .values(0, 100, 1)
         .description(OptionDescription.ofOrderedString(() -> "Adjust integer values with a slider."))
         .build();

      Option<Double> doubleOption = NumericalOption.createBuilder(
            "Double Option",
            () -> doubleVal,
            3.14,
            val -> doubleVal = val
         )
         .values(0.0, 10.0, 0.1)
         .description(OptionDescription.ofOrderedString(() -> "Adjust double values with a slider."))
         .build();

      Option<Float> floatOption = NumericalOption.createBuilder(
            "Float Option",
            () -> floatVal,
            1.5F,
            val -> floatVal = val
         )
         .values(0.0F, 5.0F, 0.05F)
         .description(OptionDescription.ofOrderedString(() -> "Adjust float values with a slider."))
         .build();

      Option<KonfyLibColor> colorOption = ColorOption.createBuilder(
            "Color Option",
            () -> colorVal,
            new KonfyLibColor(0, 120, 255, 255),
            val -> colorVal = val
         )
         .description(OptionDescription.ofOrderedString(() -> "Pick colors, adjust opacity, or enable color animations."))
         .build();

      Option<PixelGridAnimation> animOption = PixelGridAnimationOption.createBuilder(
            "Pixel Grid Option",
            () -> animVal,
            new PixelGridAnimation(PixelGrid.create().set(7, 7).build()),
            val -> animVal = val
         )
         .description(OptionDescription.ofOrderedString(() -> "Draw pixel grids or create animated sprites."))
         .build();

      Option<List<String>> listOption = StringListOption.createBuilder(
            "String List Option",
            () -> listVal,
            new ArrayList<>(Arrays.asList("Item A", "Item B", "Item C")),
            val -> listVal = val
         )
         .description(OptionDescription.ofOrderedString(() -> "Manage list of string values."))
         .build();

      Option<String> stringOption = StringOption.createBuilder(
            "String Option",
            () -> stringVal,
            "Hello, Konfy!",
            val -> stringVal = val
         )
         .description(OptionDescription.ofOrderedString(() -> "Enter text values."))
         .build();

      Option<DemoEnum> enumOption = EnumOption.createBuilder(
            "Enum Option",
            () -> enumVal,
            DemoEnum.MEDIUM,
            val -> enumVal = val,
            DemoEnum.class
         )
         .description(OptionDescription.ofOrderedString(() -> "Select from enum constants."))
         .build();

      Option<Runnable> buttonOption = ButtonOption.createBuilder(
            "Button Option Action",
            () -> {
               // Trigger custom action when clicked
            }
         )
         .description(OptionDescription.ofOrderedString(() -> "Click to trigger actions."))
         .build();

      // Organize the options into distinct widget groups
      OptionGroup basicGroup = OptionGroup.createBuilder("Basic Options")
         .addOption(booleanOption)
         .addOption(stringOption)
         .addOption(enumOption)
         .addOption(buttonOption)
         .setExpanded(true)
         .build();

      OptionGroup numericGroup = OptionGroup.createBuilder("Numerical Sliders")
         .addOption(intOption)
         .addOption(doubleOption)
         .addOption(floatOption)
         .setExpanded(true)
         .build();

      OptionGroup advancedGroup = OptionGroup.createBuilder("Advanced Widgets")
         .addOption(colorOption)
         .addOption(animOption)
         .addOption(listOption)
         .setExpanded(true)
         .build();

      // Bundle all option groups under a single category
      List<OptionGroup> groups = new ArrayList<>(Arrays.asList(basicGroup, numericGroup, advancedGroup));
      Category category = new Category("Showcase", groups, new ArrayList<>());

      List<Category> categories = new ArrayList<>(List.of(category));

      // Save showcase config path
      return new ModConfig(Paths.get("config/konfy-showcase.json"), categories, () -> {});
   }
}
