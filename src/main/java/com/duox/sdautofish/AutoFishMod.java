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

    // Treasure catch threshold (0.0 to 1.0)
    public static float fishingProgressTreasure = 0.8f;

    // Tracks whether the user used the +/- combo so V alone doesn't toggle
    private boolean comboUsed = false;

    public static final KeyMapping TOGGLE_KEY = new KeyMapping(
            "key.autofish.toggle",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.autofish"
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
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        long window = mc.getWindow().getWindow();
        boolean isVDown = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_V);

        // --- V + / V - COMBO to adjust treasure threshold ---
        if (isVDown && event.getAction() == GLFW.GLFW_PRESS) {
            boolean changed = false;

            if (event.getKey() == GLFW.GLFW_KEY_MINUS || event.getKey() == GLFW.GLFW_KEY_KP_SUBTRACT) {
                fishingProgressTreasure = Math.max(0.0f, fishingProgressTreasure - 0.05f);
                changed = true;
            } else if (event.getKey() == GLFW.GLFW_KEY_EQUAL || event.getKey() == GLFW.GLFW_KEY_KP_ADD) {
                fishingProgressTreasure = Math.min(1.0f, fishingProgressTreasure + 0.05f);
                changed = true;
            }

            if (changed) {
                comboUsed = true;
                mc.player.displayClientMessage(
                        Component.literal("Treasure Threshold: " + String.format("%.2f", fishingProgressTreasure)),
                        true
                );
                while (TOGGLE_KEY.consumeClick()) {}
                return;
            }
        }

        // --- TOGGLE handling: toggle on V release only if the combo wasn't used ---
        if (event.getKey() == GLFW.GLFW_KEY_V) {
            if (event.getAction() == GLFW.GLFW_PRESS) {
                comboUsed = false;
            } else if (event.getAction() == GLFW.GLFW_RELEASE) {
                if (!comboUsed) {
                    enabled = !enabled;
                    mc.player.displayClientMessage(
                            Component.literal("Auto Fish: " + (enabled ? "ON" : "OFF")),
                            true
                    );
                }
                while (TOGGLE_KEY.consumeClick()) {}
            }
        } else if (TOGGLE_KEY.getKey().getValue() != GLFW.GLFW_KEY_V) {
            // Fallback if the keybind was rebound away from V
            while (TOGGLE_KEY.consumeClick()) {
                enabled = !enabled;
                mc.player.displayClientMessage(
                        Component.literal("Auto Fish: " + (enabled ? "ON" : "OFF")),
                        true
                );
            }
        }
    }
}