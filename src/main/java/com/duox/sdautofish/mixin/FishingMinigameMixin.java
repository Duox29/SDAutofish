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

    // Shadow biến points (tiến trình bắt cá).
    // Warning "Unable to locate obfuscation mapping" là bình thường.
    @Shadow private float points;

    @Shadow private boolean chestVisible;

    // --- PHẦN GÂY CRASH ---
    // Biến này không tồn tại trong mod stardew_fishing-3.3.jar
    // Bạn cần decompile file jar để tìm tên đúng (ví dụ: treasureProgress, currentTreasureLevel...)
    // Sau khi tìm được, hãy bỏ comment và đổi tên biến ở đây.
    // @Shadow private float treasureCatchLevel;

    @Inject(method = "tick(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I"))
    private void onTickLogic(boolean mouseDown, CallbackInfo ci) {
        if (AutoFishMod.enabled) {

            // 1. Xử lý Cá (Fish) -> Set max điểm để bắt ngay lập tức
            // Giá trị 120.0f thường là max points của minigame này
            this.points = 120.0f;

            // 2. Xử lý Rương (Treasure)
            // Tạm thời comment để tránh crash game.
            // Logic: Nếu tìm được tên biến đúng, set nó thành 1.0f (hoặc max value tương ứng)
            /*
            if (this.chestVisible) {
                 // this.TÊN_BIẾN_ĐÚNG = 1.0f;
            }
            */
        }
    }
}