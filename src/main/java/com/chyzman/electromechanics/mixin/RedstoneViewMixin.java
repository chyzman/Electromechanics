package com.chyzman.electromechanics.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.world.RedstoneView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RedstoneView.class)
public interface RedstoneViewMixin {

    @ModifyExpressionValue(
            method = "getEmittedRedstonePower(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 1)
    )
    private boolean test(boolean original, @Local() BlockState blockState){
        return original || blockState.getBlock() instanceof RedstoneWireBlock;
    }
}
