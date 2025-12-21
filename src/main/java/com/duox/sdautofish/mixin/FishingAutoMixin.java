package com.duox.sdautofish.mixin;

import com.bonker.stardewfishing.client.FishingMinigame;
import com.bonker.stardewfishing.client.FishingScreen;
import com.duox.sdautofish.AutoFishMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FishingScreen.class, remap = false)
public abstract class FishingAutoMixin {

    @Shadow private FishingMinigame minigame;
    @Shadow public abstract void setInputDown(boolean down);

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        // Safety check: Nếu tắt mod hoặc minigame chưa init thì không làm gì
        if (!AutoFishMod.enabled || minigame == null) {
            return;
        }

        // Lấy thông số từ minigame
        float fishPos = minigame.getFishPos();
        float bobberPos = minigame.getBobberPos();
        int barSize = minigame.getBarSize();

        // Tính toán tọa độ tâm
        // Bar size thay đổi tùy theo item cần câu, tâm nằm ở vị trí hiện tại + 1/2 size
        float barCenter = bobberPos + (barSize / 2.0f);

        // Cá có height khoảng 16 pixel (dựa trên texture render), tâm là +8
        float fishCenter = fishPos + 8.0f;

        // Xác định mục tiêu (Target)
        float targetCenter = fishCenter;

        // Logic ưu tiên Rương (Treasure)
        // Chỉ bắt rương khi tiến độ đã an toàn (> 95%) và rương đang hiện
        if (minigame.getProgress() > 0.95f && minigame.isChestVisible()) {
            // Rương có height khoảng 13, tâm là +6.5
            float chestCenter = minigame.getChestPos() + 6.5f;
            targetCenter = chestCenter;
        }

        // Logic điều khiển (Control Loop)
        float delta = targetCenter - barCenter;

        // Hysteresis / Deadzone logic:
        // Nếu khoảng cách nhỏ hơn 3.0 đơn vị, giữ nguyên input hiện tại để tránh rung lắc
        if (Math.abs(delta) < 3.0f) {
            return;
        }

        // Nếu mục tiêu ở trên (delta > 0) -> Nhấn chuột (Input Down = true) để thanh bar bay lên
        // Nếu mục tiêu ở dưới (delta < 0) -> Thả chuột (Input Down = false) để thanh bar rơi xuống theo trọng lực
        boolean shouldPress = delta > 0;

        this.setInputDown(shouldPress);
    }
}