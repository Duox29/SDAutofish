package com.duox.sdautofish.mixin;

import com.bonker.stardewfishing.server.data.MinigameModifiers;
import com.bonker.stardewfishing.server.data.MinigameModifiersReloadListener;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Mixin to fix a crash in the MinigameModifiersReloadListener.
 * Compatible with Stardew Fishing v3.2, v3.3, and v3.4.
 */
@Mixin(value = MinigameModifiersReloadListener.class, remap = false)
public class MinigameCrashFixMixin {

    // Field INSTANCE vẫn tồn tại ở cả 2 phiên bản (dù bản 3.4 có thêm @Nullable) nên Shadow vẫn an toàn.
    @Shadow private static MinigameModifiersReloadListener INSTANCE;

    /**
     * Fix crash for v3.2/v3.3 where getModifiers accesses null INSTANCE.
     * * ADDED: require = 0
     * This tells Mixin: "If this method is missing (like in v3.4), just ignore this injection instead of crashing."
     */
    @Inject(method = "getModifiers", at = @At("HEAD"), cancellable = true, require = 0)
    private static void onGetModifiers(ItemStack stack, CallbackInfoReturnable<Optional<MinigameModifiers>> cir) {
        // Chỉ chạy logic này nếu method getModifiers tồn tại (v3.2/3.3)
        if (INSTANCE == null) {
            cir.setReturnValue(Optional.empty());
        }
    }
}