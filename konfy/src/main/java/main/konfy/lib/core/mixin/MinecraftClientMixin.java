package main.konfy.lib.core.mixin;

import main.konfy.lib.core.KonfyLib;
import main.konfy.lib.core.gui.impl.BaseScreen;
import main.konfy.lib.core.utils.MarqueeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
   @Shadow
   @Final
   public Gui gui;
   @Shadow
   public @Nullable Screen screen;

   @Inject(method = "<init>", at = @At("HEAD"))
   private static void onInit(GameConfig gameConfig, CallbackInfo ci) {
      KonfyLib.getInstance().setup();
   }

   @Inject(method = "<init>", at = @At("TAIL"))
   private static void onInitFinished(GameConfig gameConfig, CallbackInfo ci) {
      KonfyLib.getInstance().load();
   }

   @Inject(method = "tick", at = @At("HEAD"))
   public void onTick(CallbackInfo ci) {
      if (this.screen instanceof BaseScreen) {
         MarqueeUtil.tickCount++;
      }

      if (KonfyLib.getInstance() != null) {
         KonfyLib.getInstance().tick();
      }
   }

   @Inject(method = "setScreenAndShow", at = @At("HEAD"))
   public void setScreen(Screen screen, CallbackInfo ci) {
      if (screen instanceof BaseScreen) {
         MarqueeUtil.tickCount = 0;
      }
   }
}
