package com.duox.sdautofish.mixin;

import com.bonker.stardewfishing.client.FishingMinigame;
import com.bonker.stardewfishing.client.FishingScreen;
import com.duox.sdautofish.AutoFishMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class for FishingScreen to enable auto-fishing functionality.
 */
@Mixin(value = FishingScreen.class)
public abstract class FishingAutoMixin {

    @Shadow private FishingMinigame minigame;
    @Shadow public abstract void setInputDown(boolean down);

    /**
     * Auto-fishing logic that controls the fishing bar position.
     * Aligns the bar center with fish position, prioritizing treasure chests when safe.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (!AutoFishMod.enabled || minigame == null) {
            return;
        }

        float fishPos = minigame.getFishPos();
        float bobberPos = minigame.getBobberPos();
        int barSize = minigame.getBarSize();

        float barCenter = bobberPos + (barSize / 2.0f);
        float fishCenter = fishPos + 8.0f;

        float targetCenter = fishCenter;

        if (minigame.getProgress() > AutoFishMod.fishingProgressTreasure && minigame.isChestVisible()) {
            float chestCenter = minigame.getChestPos() + 6.5f;
            targetCenter = chestCenter;
        }

        float delta = targetCenter - barCenter;

        if (Math.abs(delta) < 3.0f) {
            return;
        }

        boolean shouldPress = delta > 0;

        this.setInputDown(shouldPress);
    }
}