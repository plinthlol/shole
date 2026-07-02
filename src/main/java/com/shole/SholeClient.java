package com.shole;

import com.shole.config.SholeConfig;
import com.shole.feature.AutoJump;
import com.shole.feature.AutoShieldBreak;
import com.mojang.blaze3d.platform.InputConstants;
import main.konfy.lib.core.gui.impl.KonfyLibConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

public final class SholeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoShieldBreak.init();
        AutoJump.init();
        com.shole.feature.JumpCharge.init();

        KeyMapping.Category sholeCategory = KeyMapping.Category.register(Shole.id("shole"));

        KeyMapping toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.shole.autoshieldbreak", InputConstants.Type.KEYSYM, InputConstants.KEY_B,
                sholeCategory
        ));

        KeyMapping configKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.shole.config", InputConstants.Type.KEYSYM, InputConstants.KEY_RCONTROL,
                sholeCategory
        ));

        KeyMapping jumpKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.shole.autojump", InputConstants.Type.KEYSYM, InputConstants.KEY_V,
                sholeCategory
        ));

        KeyMapping jumpChargeKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.shole.jumpcharge", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(),
                sholeCategory
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                AutoShieldBreak.toggle();
                if (client.player != null) {
                    client.player.sendOverlayMessage(
                        Component.literal(
                            "Auto Shield Break: " + (AutoShieldBreak.isEnabled() ? "§aON" : "§cOFF")));
                }
            }

            while (jumpKey.consumeClick()) {
                AutoJump.toggle();
                if (client.player != null) {
                    client.player.sendOverlayMessage(
                        Component.literal(
                            "Auto Jump: " + (AutoJump.isEnabled() ? "§aON" : "§cOFF")));
                }
            }

            while (jumpChargeKey.consumeClick()) {
                com.shole.feature.JumpCharge.toggle();
                if (client.player != null) {
                    client.player.sendOverlayMessage(
                        Component.literal(
                            "Jump Charge: " + (com.shole.feature.JumpCharge.isEnabled() ? "§aON" : "§cOFF")));
                }
            }

            while (configKey.consumeClick()) {
                if (client.screen == null) {
                    client.setScreenAndShow(new KonfyLibConfigScreen(
                            null,
                            SholeConfig.createConfig(),
                            "Shole"
                    ));
                }
            }
        });
    }
}
