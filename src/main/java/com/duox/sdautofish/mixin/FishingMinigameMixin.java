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

    @Shadow private double bobberPos;
    @Shadow private double fishPos;
    @Shadow private int barSize;
    @Shadow private double bobberVelocity;
    @Shadow private int maxBobberHeight;
    @Shadow private boolean chestVisible;
    @Shadow private int chestPos;
    @Shadow private float points;

    @Inject(method = "tick(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I"))
    private void onTickLogic(boolean mouseDown, CallbackInfo ci) {
        if (AutoFishMod.enabled) {

            double targetBobberPos = this.fishPos - (this.barSize / 2.0) + 7.0;

            // CẬP NHẬT: Thay 0.95f bằng biến có thể cấu hình được
            if ((this.points / 120.0f) > AutoFishMod.fishingProgressTreasure && this.chestVisible) {
                targetBobberPos = this.chestPos + 6.5 - (this.barSize / 2.0);
            }

            this.bobberPos = targetBobberPos;
            this.bobberVelocity = 0;

            if (this.bobberPos > this.maxBobberHeight) {
                this.bobberPos = this.maxBobberHeight;
            } else if (this.bobberPos < 0) {
                this.bobberPos = 0;
            }
        }
    }
}