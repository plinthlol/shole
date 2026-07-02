package com.shole.config;

import com.shole.feature.AutoJump;
import com.shole.feature.AutoShieldBreak;
import main.konfy.lib.core.config.impl.ModConfig;
import main.konfy.lib.core.config.local.Category;
import main.konfy.lib.core.config.local.Option;
import main.konfy.lib.core.config.local.OptionDescription;
import main.konfy.lib.core.config.local.options.BooleanOption;
import main.konfy.lib.core.config.local.options.NumericalOption;
import main.konfy.lib.core.config.local.options.groups.OptionGroup;

import java.nio.file.Paths;
import java.util.List;

public class SholeConfig {

    // ── Auto Shield Break ─────────────────────────────────────────────────────

    /** Base ticks before swapping back to the original slot. */
    public static int swapBackDelay = 8;

    /**
     * Max random ticks added on top of swapBackDelay.
     * Actual delay = swapBackDelay + nextInt(0, randomRange + 1)
     */
    public static int randomRange = 2;

    // ── Auto Jump ─────────────────────────────────────────────────────────────

    /** Base ticks between taking damage and jumping. */
    public static int jumpDelay = 2;

    /**
     * Max random ticks added on top of jumpDelay.
     * Actual delay = jumpDelay + nextInt(0, jumpRandomRange + 1)
     */
    public static int jumpRandomRange = 1;

    // ── Jump Charge ───────────────────────────────────────────────────────────

    /** Delay in ticks between right-clicking a wind charge and jumping. */
    public static int jumpChargeDelay = 1;

    // ─────────────────────────────────────────────────────────────────────────

    public static ModConfig createConfig() {

        // ── Auto Shield Break group ──────────────────────────────────────────
        Option<Boolean> asbToggle = BooleanOption.createBuilder(
                "Auto Shield Break",
                AutoShieldBreak::isEnabled,
                true,
                AutoShieldBreak::setEnabled
        ).description(OptionDescription.ofOrderedString(
                () -> "Automatically swap to an axe to break shields, then swap back."))
         .build();

        Option<Integer> asbDelay = NumericalOption.createBuilder(
                "Swap-back Delay",
                () -> swapBackDelay,
                8,
                val -> swapBackDelay = val
        ).values(1, 30, 1)
         .description(OptionDescription.ofOrderedString(
                 () -> "Base ticks before swapping back to your sword after an axe attack."))
         .build();

        Option<Integer> asbRange = NumericalOption.createBuilder(
                "Random Range (±)",
                () -> randomRange,
                2,
                val -> randomRange = val
        ).values(0, 10, 1)
         .description(OptionDescription.ofOrderedString(
                 () -> "Extra random ticks added to the swap-back delay."))
         .build();

        OptionGroup asbGroup = OptionGroup.createBuilder("Auto Shield Break")
                .addOption(asbToggle)
                .addOption(asbDelay)
                .addOption(asbRange)
                .setExpanded(true)
                .build();

        // ── Auto Jump group ──────────────────────────────────────────────────
        Option<Boolean> ajToggle = BooleanOption.createBuilder(
                "Auto Jump",
                AutoJump::isEnabled,
                false,
                AutoJump::setEnabled
        ).description(OptionDescription.ofOrderedString(
                () -> "Automatically jump after taking damage."))
         .build();

        Option<Integer> ajDelay = NumericalOption.createBuilder(
                "Jump Delay",
                () -> jumpDelay,
                2,
                val -> jumpDelay = val
        ).values(0, 20, 1)
         .description(OptionDescription.ofOrderedString(
                 () -> "Base ticks between taking damage and jumping."))
         .build();

        Option<Integer> ajRange = NumericalOption.createBuilder(
                "Random Range (±)",
                () -> jumpRandomRange,
                1,
                val -> jumpRandomRange = val
        ).values(0, 10, 1)
         .description(OptionDescription.ofOrderedString(
                 () -> "Extra random ticks added to the jump delay."))
         .build();

        OptionGroup ajGroup = OptionGroup.createBuilder("Auto Jump")
                .addOption(ajToggle)
                .addOption(ajDelay)
                .addOption(ajRange)
                .setExpanded(true)
                .build();

        // ── Jump Charge group ────────────────────────────────────────────────
        Option<Boolean> jcToggle = BooleanOption.createBuilder(
                "Jump Charge",
                com.shole.feature.JumpCharge::isEnabled,
                true,
                com.shole.feature.JumpCharge::setEnabled
        ).description(OptionDescription.ofOrderedString(
                () -> "Automatically jump when throwing a Wind Charge straight down."))
         .build();

        Option<Integer> jcDelay = NumericalOption.createBuilder(
                "Jump Charge Delay",
                () -> jumpChargeDelay,
                1,
                val -> jumpChargeDelay = val
        ).values(0, 10, 1)
         .description(OptionDescription.ofOrderedString(
                 () -> "Delay in ticks between throwing the wind charge and jumping (usually 1 or 2)."))
         .build();

        OptionGroup jcGroup = OptionGroup.createBuilder("Jump Charge")
                .addOption(jcToggle)
                .addOption(jcDelay)
                .setExpanded(true)
                .build();

        // ── Assemble ─────────────────────────────────────────────────────────
        Category core = new Category("Core", List.of(asbGroup, ajGroup, jcGroup), List.of());

        return new ModConfig(Paths.get("config/shole.json"), List.of(core), () -> {});
    }
}
