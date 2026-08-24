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
 * This occurs when getModifiers is called before the listener has been initialized.
 *
 * Only applies to Stardew Fishing 3.3 and older. From 3.5 onward getModifiers was
 * removed and INSTANCE is created lazily by getOrCreate(), so the crash no longer
 * exists and the injector must be optional or mixin application fails outright.
 */
@Mixin(value = MinigameModifiersReloadListener.class, remap = false)
public class MinigameCrashFixMixin {

    // Shadow the static INSTANCE field from the original class for checking.
    @Shadow private static MinigameModifiersReloadListener INSTANCE;

    /**
     * Injects a check at the beginning of getModifiers to prevent a NullPointerException.
     * If the INSTANCE is null, it returns an empty Optional, avoiding the crash.
     *
     * require = 0 so this is a no-op on versions where the method is absent.
     *
     * @param stack The ItemStack being checked for modifiers.
     * @param cir   The callback info for the returnable method.
     */
    @Inject(method = "getModifiers", at = @At("HEAD"), cancellable = true, require = 0)
    private static void onGetModifiers(ItemStack stack, CallbackInfoReturnable<Optional<MinigameModifiers>> cir) {
        // SAFETY CHECK: If INSTANCE is not yet initialized (null), return Empty immediately to prevent a crash.
        if (INSTANCE == null) {
            cir.setReturnValue(Optional.empty());
        }
    }
}