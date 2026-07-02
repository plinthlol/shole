# Setup and Usage Guide

This guide explains how to integrate KonfyLib configs in your mod.

> [!TIP]
> For a complete, self-contained implementation demonstrating all available widgets (such as toggles, sliders, text inputs, color pickers, and pixel grid editors), check out the [Example Mod Directory](https://github.com/plinthlol/Konfy/tree/26.1/example).

## Step 1. Define Option Fields

Create fields to store your setting values.

```java
public class MyModConfig {
    public static boolean enableLagFix = true;
    public static int viewDistance = 8;
    public static String serverIp = "127.0.0.1";
}
```

## Step 2. Build the Configuration Structure

Configure your categories, groups, and options using builders.

```java
import main.konfy.lib.core.config.impl.ModConfig;
import main.konfy.lib.core.config.local.Category;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.OptionDescription;
import main.konfy.lib.core.config.local.options.BooleanOption;
import main.konfy.lib.core.config.local.options.NumericalOption;
import main.konfy.lib.core.config.local.options.StringOption;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import java.nio.file.Paths;
import java.util.List;

public class MyModConfig {
    // Fields
    public static boolean enableLagFix = true;
    public static int viewDistance = 8;
    public static String serverIp = "127.0.0.1";

    public static ModConfig createConfig() {
        // 1. Boolean Toggle
        Option<Boolean> lagFixOption = BooleanOption.createBuilder(
            "Enable Lag Fix",
            () -> enableLagFix,
            true, // Default value
            val -> enableLagFix = val
        ).description(OptionDescription.ofOrderedString(() -> "Reduces rendering lag.")).build();

        // 2. Numeric Slider
        Option<Integer> distanceOption = NumericalOption.createBuilder(
            "View Distance",
            () -> viewDistance,
            8, // Default value
            val -> viewDistance = val
        ).values(2, 32, 1) // Min, Max, Step
         .description(OptionDescription.ofOrderedString(() -> "Adjust rendering distance.")).build();

        // 3. Text Box
        Option<String> ipOption = StringOption.createBuilder(
            "Server IP Address",
            () -> serverIp,
            "127.0.0.1", // Default value
            val -> serverIp = val
        ).description(OptionDescription.ofOrderedString(() -> "IP address for multiplayer.")).build();

        // 4. Create Option Group
        OptionGroup generalGroup = OptionGroup.createBuilder("General Settings")
            .addOption(lagFixOption)
            .addOption(distanceOption)
            .addOption(ipOption)
            .setExpanded(true)
            .build();

        // 5. Create Category Tab
        Category category = new Category("My Mod settings", List.of(generalGroup), List.of());

        // 6. Return ModConfig specifying save location
        return new ModConfig(Paths.get("config/mymod.json"), List.of(category), () -> {
            // Save callback logic goes here
        });
    }
}
```

## Step 3. Register your Configuration Entrypoint

Register your config so loaders can fetch it. Add a class implementing `KonfyLibApi`.

```java
import main.konfy.lib.api.KonfyLibApi;
import main.konfy.lib.core.config.impl.ModConfig;

public class MyModConfigEntrypoint implements KonfyLibApi {
    @Override
    public ModConfig getConfig() {
        return MyModConfig.createConfig();
    }
}
```

Add the entrypoint declaration inside your `fabric.mod.json` file.

```json
"entrypoints": {
  "konfy": [
    "com.example.mymod.MyModConfigEntrypoint"
  ]
}
```

## Step 4. Display the Screen Programmatically

To open the settings screen manually, construct and show it.

```java
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public void openConfigScreen(Screen parent) {
    ModConfig config = MyModConfig.createConfig();
    KonfyLibConfigScreen screen = new KonfyLibConfigScreen(parent, config, "My Mod Configuration");
    Minecraft.getInstance().setScreenAndShow(screen);
}
```

## Step 5. Display the Mods Hub Screen Programmatically

To open the unified Mods Hub screen (containing the Mods directory grid and Showcase configuration), construct `APIScreen` and show it.

```java
import main.konfy.lib.core.gui.impl.APIScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public void openModsHub(Screen parent) {
    APIScreen screen = new APIScreen(parent);
    Minecraft.getInstance().setScreenAndShow(screen);
}
```
