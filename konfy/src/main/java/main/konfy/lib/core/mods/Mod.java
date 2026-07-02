package main.konfy.lib.core.mods;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import main.konfy.lib.core.config.impl.ModConfig;
import main.konfy.lib.core.gui.impl.BaseScreen;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public class Mod {
   private final ModContainer container;
   private final Identifier modIcon;
   private final ModConfig config;
   private final Function<Screen, BaseScreen> overridableScreenFactory;
   private final String[] conflictedButtonTitles;

   public Mod(ModContainer container, ModConfig config, Function<Screen, BaseScreen> overridableScreenFactory, String[] conflictedButtonTitles) {
      this.container = container;
      this.config = config;
      this.overridableScreenFactory = overridableScreenFactory;
      this.conflictedButtonTitles = conflictedButtonTitles;
      Identifier identifier = Identifier.fromNamespaceAndPath("konfy", this.getContainer().getMetadata().getId() + "_icon");
      int ICON_SIZE = 32;
      this.modIcon = this.getContainer().getMetadata().getIconPath(32).<Path>flatMap(this.getContainer()::findPath).flatMap(path -> {
         try (InputStream inputStream = Files.newInputStream(path)) {
            NativeImage image = NativeImage.read(Objects.requireNonNull(inputStream));
            return Optional.of(new DynamicTexture(identifier::toString, image));
         } catch (IOException e) {
            System.err.println("Failed to load icon from mod jar:  " + path + " " + e);
            return Optional.empty();
         }
      }).map(tex -> {
         Minecraft.getInstance().getTextureManager().register(identifier, tex);
         return identifier;
      }).orElse(Identifier.withDefaultNamespace("textures/misc/unknown_pack.png"));
   }

   public ModContainer getContainer() {
      return this.container;
   }

   public ModConfig getConfig() {
      return this.config;
   }

   public BaseScreen getOverridableConfigScreen(Screen parent) {
      return this.overridableScreenFactory.apply(parent);
   }

   public String[] getConflictedButtonTitles() {
      return this.conflictedButtonTitles;
   }

   public boolean hasConfig() {
      return this.config != null;
   }

   public Identifier getModIcon() {
      return this.modIcon;
   }
}
