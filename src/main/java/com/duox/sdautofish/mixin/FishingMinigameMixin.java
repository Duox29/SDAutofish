package com.duox.sdautofish.mixin;

import com.bonker.stardewfishing.client.FishingMinigame;
import com.duox.sdautofish.AutoFishMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FishingMinigame.class, remap = false)
public class FishingMinigameMixin {

    // Shadow các biến cần thiết từ FishingMinigame để sử dụng
    @Shadow private double bobberPos;
    @Shadow private double fishPos;
    @Shadow private int barSize;
    @Shadow private double bobberVelocity;
    @Shadow private int maxBobberHeight;
    @Shadow private boolean chestVisible;
    @Shadow private int chestPos;
    @Shadow private float points;

    // Inject vào trước khi logic tính điểm diễn ra (Mth.floor được gọi ở đầu phần game logic)
    // Điều này đảm bảo ta ghi đè vị trí thanh câu TRƯỚC khi game kiểm tra xem có bắt trúng cá hay không.
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I"))
    private void onTickLogic(boolean mouseDown, CallbackInfo ci) {
        // Chỉ kích hoạt khi bật mod
        if (AutoFishMod.enabled) {

            // --- LOGIC TỰ ĐỘNG BÁM CÁ ---
            // Tính toán vị trí mục tiêu: Cá nằm giữa thanh câu
            // Công thức: fishPos - (barSize / 2) + 7 (offset tâm)
            double targetBobberPos = this.fishPos - (this.barSize / 2.0) + 7.0;

            // --- LOGIC TỰ ĐỘNG BẮT RƯƠNG (Tùy chọn) ---
            // Nếu tiến độ đã an toàn (> 95%) và có rương, ưu tiên rương
            // 120 là POINTS_TO_FINISH mặc định
            if ((this.points / 120.0f) > 0.95f && this.chestVisible) {
                // Rương (chiều cao ~13) + offset tâm ~6.5
                targetBobberPos = this.chestPos + 6.5 - (this.barSize / 2.0);
            }

            // Gán trực tiếp vị trí (Bỏ qua vật lý/quán tính)
            this.bobberPos = targetBobberPos;
            this.bobberVelocity = 0; // Triệt tiêu vận tốc

            // Giới hạn (Clamping) để không bay ra ngoài khung
            if (this.bobberPos > this.maxBobberHeight) {
                this.bobberPos = this.maxBobberHeight;
            } else if (this.bobberPos < 0) {
                this.bobberPos = 0;
            }
        }
    }
}