package com.duox.sdautofish.mixin;

import com.bonker.stardewfishing.client.FishingScreen;
import com.duox.sdautofish.AutoFishMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingScreen.class)
public abstract class FishingScreenMixin {

    @Shadow private int animationTimer;
    @Shadow private FishingScreen.Status status;

    // Shadow phương thức onClose để gọi trực tiếp (mặc định Mixin tự hiểu remap=true cho shadow)
    @Shadow public abstract void onClose();

    /**
     * Can thiệp vào quá trình khởi tạo màn hình.
     * Chuyển ngay sang MINIGAME để bỏ qua hoàn toàn animation "HIT!" ban đầu.
     * Dùng remap = true vì init là method override của Minecraft.
     */
    @Inject(method = "init()V", at = @At("TAIL"), remap = true)
    private void onInit(CallbackInfo ci) {
        if (AutoFishMod.enabled && this.status == FishingScreen.Status.HIT_TEXT) {
            this.status = FishingScreen.Status.MINIGAME;
            // Set timer = MAX_VALUE là điều kiện để logic Minigame bắt đầu chạy (theo code gốc)
            this.animationTimer = Integer.MAX_VALUE;
        }
    }

    /**
     * Can thiệp ngay sau khi minigame tính toán xong kết quả.
     * Gọi onClose() ngay lập tức để bỏ qua mọi animation thắng thua hoặc mở rương.
     * Dùng remap = false vì setResult là method riêng của mod.
     */
    @Inject(method = "setResult", at = @At("TAIL"), remap = false)
    private void onSetResult(boolean success, double accuracy, boolean gotChest, boolean goldenChest, CallbackInfo ci) {
        if (AutoFishMod.enabled) {
            // Đóng GUI ngay lập tức.
            // Server vẫn sẽ nhận được packet hoàn thành minigame và vật phẩm rương (nếu có).
            this.onClose();
        }
    }
}