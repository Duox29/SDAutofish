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

    // Shadow các biến private từ FishingMinigame.java
    @Shadow private float points;          // Điểm bắt cá (Max 120)
    @Shadow private boolean chestVisible;  // Trạng thái hiển thị rương
    @Shadow private float chestTimer;      // Tiến trình bắt rương (Max 30)
    @Shadow private int chestAppearTime;   // Thời gian chờ rương xuất hiện

    @Inject(method = "tick(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I"))
    private void onTickLogic(boolean mouseDown, CallbackInfo ci) {
        if (AutoFishMod.enabled) {

            if (this.chestAppearTime > 1) {
                this.chestAppearTime = 0;
            }
            chestVisible = true;

            // 2. Tự động bắt Rương (Treasure)
            // Khi rương đã hiện, set luôn tiến trình (timer) lên max (30) để bắt xong luôn.
                this.chestTimer = 30.0f;

            // 3. Tự động bắt Cá (Fish)
            // Set điểm lên max (120) để hoàn thành minigame.
            this.points = 120.0f;
        }
    }
}