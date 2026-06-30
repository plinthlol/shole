package com.shole;

import com.shole.feature.AutoShieldBreak;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

public final class SholeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoShieldBreak.init();

        KeyMapping.Category sholeCategory = KeyMapping.Category.register(Shole.id("shole"));

        KeyMapping toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.shole.autoshieldbreak", InputConstants.Type.KEYSYM, InputConstants.KEY_B,
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
        });
    }
}
