package com.shole.feature;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;

public final class AutoShieldBreak {

    private static boolean enabled = true;
    private static boolean processing = false; // re-entrancy guard
    private static int targetSlot = -1;
    private static int ticksRemaining = -1;

    private AutoShieldBreak() {}

    public static void init() {
        AttackEntityCallback.EVENT.register(AutoShieldBreak::onAttack);
        // decrement delay ticks and swap back to original sword slot when done
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (ticksRemaining > 0) {
                ticksRemaining--;
                if (ticksRemaining == 0) {
                    if (targetSlot >= 0 && targetSlot < 9) {
                        swapTo(client, client.player, targetSlot);
                    }
                    ticksRemaining = -1;
                    targetSlot = -1;
                }
            }
        });
    }

    public static boolean isEnabled() { return enabled; }
    public static void setEnabled(boolean value) { enabled = value; }
    public static void toggle() { enabled = !enabled; }

    private static InteractionResult onAttack(Player player, net.minecraft.world.level.Level level,
            net.minecraft.world.InteractionHand hand, Entity target,
            net.minecraft.world.phys.EntityHitResult hitResult) {

        if (!enabled || processing) return InteractionResult.PASS;
        if (!(target instanceof LivingEntity livingTarget)) return InteractionResult.PASS;
        if (!livingTarget.isBlocking()) return InteractionResult.PASS;

        ItemStack held = player.getMainHandItem();
        if (!held.is(ItemTags.SWORDS)) return InteractionResult.PASS;
        if (held.getItem() instanceof AxeItem) return InteractionResult.PASS;

        int axeSlot = findAxeSlot(player);
        if (axeSlot == -1) return InteractionResult.PASS;

        Minecraft client = Minecraft.getInstance();
        int swordSlot = player.getInventory().getSelectedSlot();

        processing = true;
        try {
            swapTo(client, player, axeSlot);
            client.gameMode.attack(player, target);
            player.swing(hand);
            // swap back delay = base + random[0..randomRange]
            targetSlot = swordSlot;
            int range = com.shole.config.SholeConfig.randomRange;
            int offset = range > 0 ? java.util.concurrent.ThreadLocalRandom.current().nextInt(range + 1) : 0;
            ticksRemaining = com.shole.config.SholeConfig.swapBackDelay + offset;
        } finally {
            processing = false;
        }
        return InteractionResult.SUCCESS; // we already attacked manually — cancel the default attack
    }

    private static void swapTo(Minecraft client, Player player, int slot) {
        player.getInventory().setSelectedSlot(slot);
        if (client.getConnection() != null) {
            client.getConnection().send(new ServerboundSetCarriedItemPacket(slot));
        }
    }

    private static int findAxeSlot(Player player) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getItem(i).getItem() instanceof AxeItem) return i;
        }
        return -1;
    }
}
