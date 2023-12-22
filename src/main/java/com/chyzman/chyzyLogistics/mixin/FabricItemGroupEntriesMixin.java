package com.chyzman.chyzyLogistics.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.chyzman.chyzyLogistics.ChyzyLogistics.bypassingAir;

@Mixin(FabricItemGroupEntries.class)
public abstract class FabricItemGroupEntriesMixin {

    @Inject(method = "checkStack", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private static void giveMyRedstoneSomeSpace(ItemStack stack, CallbackInfo ci) {
        if (bypassingAir) ci.cancel();
    }
}