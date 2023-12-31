package com.chyzman.electromechanics.mixin;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.chyzman.electromechanics.ElectromechanicsLogistics.bypassingAir;

@Mixin(FabricItemGroupEntries.class)
public abstract class FabricItemGroupEntriesMixin {

    @Inject(method = "checkStack", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private static void giveMyRedstoneSomeSpace(ItemStack stack, CallbackInfo ci) {
        if (bypassingAir) ci.cancel();
    }
}