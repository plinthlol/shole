package com.example.mymod.config;

import main.konfy.lib.core.config.impl.ModConfig;
import main.konfy.lib.core.config.local.Category;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.OptionDescription;
import main.konfy.lib.core.config.local.options.*;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;
import main.konfy.lib.core.gui.utils.PixelGrid;
import main.konfy.lib.core.gui.utils.PixelGridAnimation;
import main.konfy.lib.core.utils.KonfyLibColor;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExampleConfig {
    // 1. Boolean Toggle Field
    public static boolean enableFeature = true;

    // 2. Numerical Slider Field
    public static int renderDistance = 8;

    // 3. String Input Field
    public static String serverAddress = "localhost";

    // 4. Enum Selector Field
    public enum DisplayMode {
        MINIMAL,
        DETAILED,
        COMPACT
    }
    public static DisplayMode displayMode = DisplayMode.DETAILED;

    // 5. Color Selector Field
    public static KonfyLibColor themeColor = new KonfyLibColor(0, 120, 255, 255);

    // 6. String List Field
    public static List<String> blacklistedPlayers = new ArrayList<>(List.of("Herobrine", "Griefer"));

    // 7. Static Pixel Grid Field
    public static PixelGrid customEmblem = new PixelGrid(8, 8);

    // 8. Pixel Grid Animation Field
    public static PixelGridAnimation customParticle = new PixelGridAnimation(8, 8);

    public static ModConfig createConfig() {
        // 1. Boolean Option
        Option<Boolean> booleanOption = BooleanOption.createBuilder(
            "Enable Feature",
            () -> enableFeature,
            true,
            val -> enableFeature = val
        ).description(OptionDescription.ofOrderedString(() -> "Toggles the main feature on/off.")).build();

        // 2. Numerical Option (Slider)
        Option<Integer> numericalOption = NumericalOption.createBuilder(
            "Render Distance",
            () -> renderDistance,
            8,
            val -> renderDistance = val
        ).values(2, 32, 1) // Min, Max, Step
         .description(OptionDescription.ofOrderedString(() -> "Controls the viewing distance.")).build();

        // 3. String Option (Text Box)
        Option<String> stringOption = StringOption.createBuilder(
            "Server IP",
            () -> serverAddress,
            "localhost",
            val -> serverAddress = val
        ).description(OptionDescription.ofOrderedString(() -> "Target multiplayer server address.")).build();

        // 4. Enum Option (Cycle button)
        Option<DisplayMode> enumOption = EnumOption.createBuilder(
            "Display Mode",
            DisplayMode.class,
            () -> displayMode,
            DisplayMode.DETAILED,
            val -> displayMode = val
        ).description(OptionDescription.ofOrderedString(() -> "Changes the details rendered in HUD.")).build();

        // 5. Color Option (Hex, Palette, and Sliders Picker)
        Option<KonfyLibColor> colorOption = ColorOption.createBuilder(
            "Theme Color",
            () -> themeColor,
            new KonfyLibColor(0, 120, 255, 255),
            val -> themeColor = val
        ).description(OptionDescription.ofOrderedString(() -> "Custom HUD background theme color.")).build();

        // 6. String List Option (Scrollable edit list)
        Option<List<String>> stringListOption = StringListOption.createBuilder(
            "Player Blacklist",
            () -> blacklistedPlayers,
            List.of(),
            val -> blacklistedPlayers = val
        ).description(OptionDescription.ofOrderedString(() -> "List of players blocked from connecting.")).build();

        // 7. Static Pixel Grid Option
        Option<PixelGrid> pixelGridOption = PixelGridOption.createBuilder(
            "Guild Emblem",
            () -> customEmblem,
            new PixelGrid(8, 8),
            val -> customEmblem = val
        ).description(OptionDescription.ofOrderedString(() -> "Draw your static emblem pixel art.")).build();

        // 8. Animated Pixel Grid Option
        Option<PixelGridAnimation> pixelGridAnimationOption = PixelGridAnimationOption.createBuilder(
            "Effect Frame",
            () -> customParticle,
            new PixelGridAnimation(8, 8),
            val -> customParticle = val
        ).description(OptionDescription.ofOrderedString(() -> "Create dynamic particle effect frames.")).build();

        // 9. Button Option (Actions execution trigger)
        Option<Void> buttonOption = ButtonOption.createBuilder(
            "Reset Options",
            () -> {
                enableFeature = true;
                renderDistance = 8;
                serverAddress = "localhost";
                displayMode = DisplayMode.DETAILED;
                themeColor = new KonfyLibColor(0, 120, 255, 255);
            }
        ).description(OptionDescription.ofOrderedString(() -> "Reverts all settings to defaults.")).build();

        // Group into sections
        OptionGroup generalGroup = OptionGroup.createBuilder("General Settings")
            .addOption(booleanOption)
            .addOption(numericalOption)
            .addOption(stringOption)
            .addOption(enumOption)
            .setExpanded(true)
            .build();

        OptionGroup advancedGroup = OptionGroup.createBuilder("Advanced Widgets")
            .addOption(colorOption)
            .addOption(stringListOption)
            .addOption(pixelGridOption)
            .addOption(pixelGridAnimationOption)
            .addOption(buttonOption)
            .setExpanded(false)
            .build();

        // Category Tab
        Category mainCategory = new Category("Main Settings", List.of(generalGroup, advancedGroup), List.of());

        // Return config builder
        return new ModConfig(Paths.get("config/mymod.json"), List.of(mainCategory), () -> {
            // Save callback configuration logic
            System.out.println("Config saved successfully!");
        });
    }
}
