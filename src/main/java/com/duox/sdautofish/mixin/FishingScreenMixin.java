package com.duox.sdautofish.mixin;

import com.bonker.stardewfishing.client.FishingScreen;
import com.duox.sdautofish.AutoFishMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingScreen.class)
public class FishingScreenMixin {

    @Shadow private int animationTimer;
    @Shadow private FishingScreen.Status status;

    /**
     * Can thiệp vào setResult để bỏ qua thời gian chờ sau khi câu xong (Status SUCCESS/FAILURE).
     * BẮT BUỘC dùng At("TAIL") để ghi đè giá trị 20 mà method gốc set.
     */
    @Inject(method = "setResult", at = @At("TAIL"), remap = false)
    private void onSetResult(boolean success, double accuracy, boolean gotChest, boolean goldenChest, CallbackInfo ci) {
        if (AutoFishMod.enabled) {
            // Set timer về 1 (thay vì 0).
            // Lý do: Logic trong tick() là if (--animationTimer == 0).
            // Nếu set là 1, tick tiếp theo sẽ giảm xuống 0 và kích hoạt hành động (mở rương hoặc đóng GUI) ngay lập tức.
            this.animationTimer = 1;
        }
    }

    /**
     * Can thiệp vào đầu mỗi tick để xử lý các animation chuyển cảnh (Intro và Chest).
     */
    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void onTick(CallbackInfo ci) {
        if (!AutoFishMod.enabled) return;

        // 1. Skip animation "HIT!" lúc vừa dính cá
        if (this.status == FishingScreen.Status.HIT_TEXT) {
            // Chuyển thẳng sang trạng thái Minigame
            this.status = FishingScreen.Status.MINIGAME;
            // Set giá trị khởi tạo mà Minigame logic yêu cầu (dựa trên code gốc: animationTimer = Integer.MAX_VALUE)
            this.animationTimer = Integer.MAX_VALUE;
        }

        // 2. Skip animation mở rương (Status CHEST_OPENING)
        if (this.status == FishingScreen.Status.CHEST_OPENING) {
            // Logic gốc đợi 30 tick. Ta ép về 1 để nó đóng ngay trong tick này.
            if (this.animationTimer > 1) {
                this.animationTimer = 1;
            }
        }
    }
}