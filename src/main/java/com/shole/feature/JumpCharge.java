package com.shole.feature;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class JumpCharge {

    private static boolean enabled = true;
    private static int ticksRemaining = -1;

    private JumpCharge() {}

    public static void init() {
        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (!enabled) {
                return InteractionResult.PASS;
            }

            // Only run on client-side for the local client player
            Minecraft client = Minecraft.getInstance();
            if (player != client.player) {
                return InteractionResult.PASS;
            }

            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(Items.WIND_CHARGE)) {
                // Pitch of 89-90 degrees (looking straight down)
                float pitch = player.getXRot();
                if (pitch >= 89.0f) {
                    ticksRemaining = 2;
                }
            }

            return InteractionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                ticksRemaining = -1;
                return;
            }

            if (ticksRemaining > 0) {
                ticksRemaining--;
                if (ticksRemaining == 0) {
                    if (client.player.onGround()) {
                        client.player.jumpFromGround();
                    }
                    ticksRemaining = -1;
                }
            }
        });
    }

    public static boolean isEnabled() { return enabled; }
    public static void setEnabled(boolean value) { enabled = value; }
    public static void toggle() { enabled = !enabled; }
}
