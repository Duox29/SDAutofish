package com.duox.sdautofish.mixin;

import com.bonker.stardewfishing.client.FishingMinigame;
import com.duox.sdautofish.AutoFishMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FishingMinigame.class)
public class FishingMinigameMixin {
    @Shadow private float points;
    @Shadow private boolean chestVisible;
    @Shadow private float chestTimer;
    @Shadow private int chestAppearTime;

    @Inject(method = "tick(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I"))
    private void onTickLogic(boolean mouseDown, CallbackInfo ci) {
        if (AutoFishMod.enabled) {

            if (this.chestAppearTime != -1) {
                this.chestAppearTime = 0;
                this.chestTimer = 30.0f;
            }
            this.points = 120.0f;
        }
    }
}