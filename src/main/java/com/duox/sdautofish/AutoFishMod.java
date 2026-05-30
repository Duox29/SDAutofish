package com.duox.sdautofish;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

@Mod("autofish")
public class AutoFishMod {
    public static boolean enabled = false;
    public static float fishingProgressTreasure = 0.8f;
    private boolean comboUsed = false;

    public static final KeyMapping TOGGLE_KEY = new KeyMapping(
            "key.autofish.toggle",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.autofish"
    );

    public AutoFishMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerBindings);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_KEY);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        long window = mc.getWindow().getWindow();
        boolean isVDown = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_V);

        if (isVDown && event.getAction() == GLFW.GLFW_PRESS) {
            boolean changed = false;
            if (event.getKey() == GLFW.GLFW_KEY_MINUS || event.getKey() == GLFW.GLFW_KEY_KP_SUBTRACT) {
                fishingProgressTreasure = Math.max(0.0f, fishingProgressTreasure - 0.05f); // Giới hạn dưới là 0
                changed = true;
            }
            else if (event.getKey() == GLFW.GLFW_KEY_EQUAL || event.getKey() == GLFW.GLFW_KEY_KP_ADD) {
                fishingProgressTreasure = Math.min(1.0f, fishingProgressTreasure + 0.05f); // Giới hạn trên là 1
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