package com.duox.sdautofish.mixin;

import com.bonker.stardewfishing.client.FishingMinigame;
import com.duox.sdautofish.AutoFishMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class for FishingMinigame to enable direct position control.
 * Overrides bobber position for perfect auto-fishing alignment.
 */
@Mixin(value = FishingMinigame.class, remap = false)
public class FishingMinigameMixin {

    // Shadow fields from FishingMinigame for position and state access
    @Shadow private double bobberPos;
    @Shadow private double fishPos;
    @Shadow private int barSize;
    @Shadow private double bobberVelocity;
    @Shadow private int maxBobberHeight;
    @Shadow private boolean chestVisible;
    @Shadow private int chestPos;
    @Shadow private float points;

    /**
     * Injects logic into the FishingMinigame's tick method to control the bobber's position.
     * 
     * @param mouseDown whether the mouse is currently being held down
     * @param ci        callback info for the injection
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I"))
    private void onTickLogic(boolean mouseDown, CallbackInfo ci) {
        // Only activate when mod is enabled
        if (AutoFishMod.enabled) {

            // --- AUTO-HOOK LOGIC ---
            // Calculate the target position to center the bobber on the fish.
            // Formula: fishPos - (barSize / 2) + 7 (center offset)
            double targetBobberPos = this.fishPos - (this.barSize / 2.0) + 7.0;

            // --- AUTO-LOOT TREASURE LOGIC (Optional) ---
            // Prioritize treasure chests if progress is safe (> 95%) and a chest is visible.
            // 120 is the default POINTS_TO_FINISH
            if ((this.points / 120.0f) > 0.95f && this.chestVisible) {
                // Chest height is ~13, so center offset is ~6.5
                targetBobberPos = this.chestPos + 6.5 - (this.barSize / 2.0);
            }

            // Directly set the bobber's position, ignoring physics/inertia.
            this.bobberPos = targetBobberPos;
            this.bobberVelocity = 0; // Nullify velocity

            // Clamp the bobber's position to stay within the minigame bounds.
            if (this.bobberPos > this.maxBobberHeight) {
                this.bobberPos = this.maxBobberHeight;
            } else if (this.bobberPos < 0) {
                this.bobberPos = 0;
            }
        }
    }
}