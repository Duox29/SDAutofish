package com.duox.sdautofish.mixin;

import com.bonker.stardewfishing.server.data.MinigameModifiers;
import com.bonker.stardewfishing.server.data.MinigameModifiersReloadListener;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = MinigameModifiersReloadListener.class, remap = false)
public class MinigameCrashFixMixin {

    // Shadow field static INSTANCE từ class gốc để kiểm tra
    @Shadow private static MinigameModifiersReloadListener INSTANCE;

    @Inject(method = "getModifiers", at = @At("HEAD"), cancellable = true)
    private static void onGetModifiers(ItemStack stack, CallbackInfoReturnable<Optional<MinigameModifiers>> cir) {
        // SAFETY CHECK: Nếu INSTANCE chưa khởi tạo (null), trả về Empty ngay lập tức
        if (INSTANCE == null) {
            cir.setReturnValue(Optional.empty());
        }
    }
}