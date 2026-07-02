package com.shole.feature;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.concurrent.ThreadLocalRandom;

public final class AutoJump {

    private static boolean enabled = false;
    private static float lastHealth = -1f;
    private static int ticksRemaining = -1;

    private AutoJump() {}

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                lastHealth = -1f;
                return;
            }

            float currentHealth = client.player.getHealth();

            // Detect damage: health dropped since last tick
            if (enabled && lastHealth > 0f && currentHealth < lastHealth) {
                // Only schedule if not already counting down
                if (ticksRemaining < 0) {
                    int range = com.shole.config.SholeConfig.jumpRandomRange;
                    int offset = range > 0 ? ThreadLocalRandom.current().nextInt(range + 1) : 0;
                    ticksRemaining = com.shole.config.SholeConfig.jumpDelay + offset;
                }
            }

            lastHealth = currentHealth;

            // Count down, then jump
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
