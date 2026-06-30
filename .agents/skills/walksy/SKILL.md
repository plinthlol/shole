---
name: walksy
description: Guides on how to use and integrate WalksyLib, a builder-based config library for Minecraft Fabric mods, using the reference implementation from ShieldStatus.
---

# WalksyLib Configuration Guide

WalksyLib is a developer-focused, builder-based configuration and utility library for Minecraft Fabric mods. It handles config file serialization/deserialization, in-game config menu layout generation, and supports features like color editing (HSB/rainbow/pulse animations), drag-and-drop custom sprite textures, and pixel art grid animations.

This guide details the integration pattern based on the implementation found in [ShieldStatus](file:///home/plinth/WalksyLib/ShieldStatus).

---

## 1. Setup & Registration

### Step A: Declare entrypoint in `fabric.mod.json`
Register your API implementation class under the `"walksylib"` entrypoint key:

```json
"entrypoints": {
  "walksylib": [
    "walksy.shieldstatus.config.WalksyLibIntegration"
  ]
}
```

### Step B: Implement `WalksyLibApi`
Create a class implementing [WalksyLibApi](file:///home/plinth/WalksyLib/src/main/java/main/walksy/lib/api/WalksyLibApi.java). This class will serve as the entrypoint hook.

```java
package walksy.shieldstatus.config;

import main.walksy.lib.api.WalksyLibApi;
import main.walksy.lib.api.WalksyLibConfig;
import main.walksy.lib.core.config.impl.ModConfig;

public class WalksyLibIntegration implements WalksyLibApi {

    @Override
    public ModConfig getConfig() {
        WalksyLibConfig config = new Config();
        return config.getOrCreateConfig(); // Uses WalksyLibConfig's built-in static cache map
    }
}
```

---

## 2. Implementing the Config Class

The config implementation should implement [WalksyLibConfig](file:///home/plinth/WalksyLib/src/main/java/main/walksy/lib/api/WalksyLibConfig.java). In this class:
1. Define `public static` fields to store the active configuration state.
2. Build [Option](file:///home/plinth/WalksyLib/src/main/java/main/walksy/lib/core/config/local/Option.java) instances mapping the getters and setters to these static fields.
3. Organize options into [OptionGroups](file:///home/plinth/WalksyLib/src/main/java/main/walksy/lib/core/config/local/options/groups/OptionGroup.java) and [Categories](file:///home/plinth/WalksyLib/src/main/java/main/walksy/lib/core/config/local/Category.java).
4. Implement the `define()` method to build and return the [ModConfig](file:///home/plinth/WalksyLib/src/main/java/main/walksy/lib/core/config/impl/ModConfig.java).

### Full Reference Example (from ShieldStatus config)

```java
package walksy.shieldstatus.config;

import main.walksy.lib.api.WalksyLibConfig;
import main.walksy.lib.core.config.impl.ModConfig;
import main.walksy.lib.core.config.local.Category;
import main.walksy.lib.core.config.local.Option;
import main.walksy.lib.core.config.local.OptionDescription;
import main.walksy.lib.core.config.local.options.BooleanOption;
import main.walksy.lib.core.config.local.options.ColorOption;
import main.walksy.lib.core.config.local.options.SpriteOption;
import main.walksy.lib.core.config.local.options.groups.OptionGroup;
import main.walksy.lib.core.config.local.options.type.WalksyLibColor;
import main.walksy.lib.core.utils.IdentifierWrapper;
import main.walksy.lib.core.utils.PathUtils;

public class Config implements WalksyLibConfig {

    // 1. Static Backing Fields
    public static boolean modEnabled = true;
    public static boolean colorInterpolation = false;
    public static boolean grayscaleTexture = false;
    public static boolean selfStateOnly = false;

    private static WalksyLibColor enabledColor = new WalksyLibColor(0, 255, 0, 255);
    private static WalksyLibColor disabledColor = new WalksyLibColor(255, 0, 0, 255);

    public static IdentifierWrapper enabledTexture = new IdentifierWrapper(
        net.minecraft.resources.Identifier.withDefaultNamespace("textures/entity/shield/shield_base_nopattern.png")
    );

    // 2. Build Options (mapping to static fields)
    private final Option<Boolean> modEnabledOption = BooleanOption.createBuilder(
            "Mod Enabled", 
            () -> modEnabled, 
            modEnabled, 
            newValue -> modEnabled = newValue
        )
        .description(OptionDescription.ofOrderedString(() -> "Toggles ShieldStatuses on or off"))
        .build();

    private final Option<Boolean> selfStateOnlyOption = BooleanOption.createBuilder(
            "Self State Only", 
            () -> selfStateOnly, 
            selfStateOnly, 
            newValue -> selfStateOnly = newValue
        )
        .description(OptionDescription.ofOrderedString(() -> "Shows shield states for the client player only"))
        .availability(() -> modEnabled, "Requires 'Mod Enabled' to be enabled")
        .build();

    private final Option<WalksyLibColor> enabledColorOption = ColorOption.createBuilder(
            "Enabled Color", 
            () -> enabledColor, 
            enabledColor, 
            newValue -> enabledColor = newValue
        )
        .description(OptionDescription.ofOrderedString(() -> "Color of the shield when enabled"))
        .availability(() -> modEnabled, "Requires 'Mod Enabled' to be enabled")
        .build();

    private final Option<IdentifierWrapper> enabledShieldTextureOption = SpriteOption.createBuilder(
            "Enabled Shield Texture", 
            () -> enabledTexture, 
            enabledTexture, 
            newValue -> enabledTexture = newValue
        )
        .description(OptionDescription.ofOrderedString(() -> "Sets the texture used for the shield when enabled"))
        .availability(() -> modEnabled, "Requires 'Mod Enabled' to be enabled")
        .build();

    // 3. Organising Groups and Categories
    private final Category generalCategory = Category.createBuilder("General")
        .group(OptionGroup.createBuilder("Global Options")
            .addOption(modEnabledOption)
            .build())
        .build();

    private final Category colorCategory = Category.createBuilder("Color")
        .group(OptionGroup.createBuilder("General Options")
            .addOption(selfStateOnlyOption)
            .addOption(interpolateShieldColorOption)
            .addOption(grayscaleShieldTextureOption)
            .build())
        .group(OptionGroup.createBuilder("Color Settings")
            .addOption(enabledColorOption)
            .build())
        .build();

    private final Category textureCategory = Category.createBuilder("Texture")
        .group(OptionGroup.createBuilder("Texture Options")
            .addOption(enabledShieldTextureOption)
            .build())
        .build();

    // 4. Define configuration structure & file path
    @Override
    public ModConfig define() {
        return ModConfig.createBuilder()
            .path(PathUtils.ofConfigDir("shieldstatus")) // Generates: config/shieldstatus.json
            .category(generalCategory)
            .category(colorCategory)
            .category(textureCategory)
            .build();
    }
}
```

---

## 3. Supported Option Builders

Every option builder extends [OptionBuilder](file:///home/plinth/WalksyLib/src/main/java/main/walksy/lib/core/config/local/builders/OptionBuilder.java) and supports chainable configurations:
- `.description(OptionDescription)`: Tooltip content. Usually built with `OptionDescription.ofOrderedString(() -> "Tooltip text")`.
- `.onChange(Runnable)`: Executes custom logic when updated.
- `.availability(Supplier<Boolean> condition, String reason)`: Disables option unless condition is met.

### Concrete Builders & Factory Methods:

1. **BooleanOption**:
   `BooleanOption.createBuilder(String name, Supplier<Boolean> getter, boolean defaultValue, Consumer<Boolean> setter)`
   - Call `.addWarning(new BooleanOption.Warning(title, msg, onYes, onNo))` to show a confirmation dialog before changing value.

2. **NumericalOption**:
   `NumericalOption.createBuilder(String name, Supplier<T> getter, T defaultValue, Consumer<T> setter)`
   - Call `.values(T min, T max, T increment)` to configure sliding range constraints. Supports `Integer`, `Float`, and `Double`.

3. **EnumOption**:
   `EnumOption.createBuilder(String name, Supplier<E> getter, E defaultValue, Consumer<E> setter, Class<E> enumClass)`

4. **ColorOption**:
   `ColorOption.createBuilder(String name, Supplier<WalksyLibColor> getter, WalksyLibColor defaultValue, Consumer<WalksyLibColor> setter)`
   - Integrates [WalksyLibColor](file:///home/plinth/WalksyLib/src/main/java/main/walksy/lib/core/config/local/options/type/WalksyLibColor.java), enabling automatic GUI-based controls for alpha, RGB hue, pulse speeds, and rainbow transitions.

5. **SpriteOption**:
   `SpriteOption.createBuilder(String name, Supplier<IdentifierWrapper> getter, IdentifierWrapper defaultValue, Consumer<IdentifierWrapper> setter)`
   - Renders a drag-and-drop field for custom textures, returning a [IdentifierWrapper](file:///home/plinth/WalksyLib/src/main/java/main/walksy/lib/core/utils/IdentifierWrapper.java).

6. **StringOption** / **StringListOption**:
   - `StringOption.createBuilder(String name, Supplier<String> getter, String defaultValue, Consumer<String> setter)`
   - `StringListOption.createBuilder(String name, Supplier<List<String>> getter, List<String> defaultValue, Consumer<List<String>> setter)`

7. **ButtonOption**:
   `ButtonOption.createBuilder(String name, Supplier<Runnable> getter, Runnable defaultValue, Consumer<Runnable> setter)`
   - Renders a clickable button widget executing the provided `Runnable` action.

8. **PixelGridOption** / **PixelGridAnimationOption**:
   - `PixelGridOption.createBuilder(String name, Supplier<PixelGrid> getter, PixelGrid defaultValue, Consumer<PixelGrid> setter)`
   - `PixelGridAnimationOption.createBuilder(String name, Supplier<PixelGridAnimation> getter, PixelGridAnimation defaultValue, Consumer<PixelGridAnimation> setter)`
   - Standard structures allowing creation and rendering of frame-by-frame custom pixel animations directly in the configuration screen.

---

## 4. Built-in Scrolling System

WalksyLib includes a fully automated scrolling system requiring no extra setup by mod developers:
- **Horizontal Scroll**: When a mod contains many tabs/categories, they are rendered in a [ScrollableTabWidget](file:///home/plinth/WalksyLib/src/main/java/main/walksy/lib/core/gui/widgets/ScrollableTabWidget.java). If they overflow the screen width, they can be scrolled horizontally using the mouse wheel while hovering over the tab bar, or by clicking the arrow indicators.
- **Vertical Option Scroll**: When a category contains many option groups, options, or complex custom controls, the config screen ([WalksyLibConfigScreen](file:///home/plinth/WalksyLib/src/main/java/main/walksy/lib/core/gui/impl/WalksyLibConfigScreen.java)) automatically enables smooth vertical scrolling via the mouse wheel.
- **Inner Widget Scrolling**: Hovering over slider widgets or custom sub-panels allows inner widget events (like sliding integer values or dragging grids) to receive scrolling events independently.

---

## 5. Launching the Config GUI

WalksyLib provides two levels of GUI screens: a central dashboard displaying all registered mods, and individual mod config screens.

### ModMenu Integration (Automated)
WalksyLib integrates with **ModMenu** automatically. As long as your mod is registered under the `"walksylib"` entrypoint in `fabric.mod.json`, WalksyLib's internal [ModMenuIntegration](file:///home/plinth/WalksyLib/src/main/java/main/walksy/lib/core/integration/ModMenuIntegration.java) will automatically register config screens for your mod with ModMenu.
- Selecting WalksyLib in ModMenu launches the central mod directory ([APIScreen](file:///home/plinth/WalksyLib/src/main/java/main/walksy/lib/core/gui/impl/APIScreen.java)).
- Selecting your mod in ModMenu will launch your mod's config screen directly.

### Programmatic Launch (Manual)
To open a screen manually (e.g., via keybind, HUD click, or in-game command):

#### A. Opening the Central WalksyLib Screen
```java
import main.walksy.lib.core.gui.impl.APIScreen;
import net.minecraft.client.Minecraft;

// Call this to display the central menu listing all registered walksylib mods
Minecraft.getInstance().setScreenAndShow(new APIScreen(parentScreen));
```

#### B. Opening Your Mod's Specific Screen Directly
```java
import main.walksy.lib.core.gui.impl.WalksyLibConfigScreen;
import net.minecraft.client.Minecraft;

// Open your mod's config screen directly
Minecraft.getInstance().setScreenAndShow(
    new WalksyLibConfigScreen(parentScreen, MyConfigHolder.getConfig(), "My Mod Name")
);
```
