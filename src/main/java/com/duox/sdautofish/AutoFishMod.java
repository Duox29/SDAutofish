package com.duox.sdautofish;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

/**
 * Main mod class for AutoFish functionality.
 * Provides toggle key binding and mod state management.
 */
@Mod("sdautofish")
public class AutoFishMod {
    public static boolean enabled = false;
    public static final KeyMapping TOGGLE_KEY = new KeyMapping(
            "key.sdautofish.toggle",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.sdautofish"
    );

    /**
     * Initializes the mod by registering event listeners and key bindings.
     */
    public AutoFishMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerBindings);
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Registers the toggle key binding with the game.
     */
    private void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_KEY);
    }

    /**
     * Handles key input events to toggle auto-fish functionality.
     * Shows status message when toggled.
     */
    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        if (TOGGLE_KEY.consumeClick()) {
            enabled = !enabled;
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(
                        Component.literal("Auto Fish: " + (enabled ? "ON" : "OFF")),
                        true
                );
            }
        }
    }
}