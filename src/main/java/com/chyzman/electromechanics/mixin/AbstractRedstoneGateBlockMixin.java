package com.chyzman.electromechanics.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractRedstoneGateBlock.class)
public abstract class AbstractRedstoneGateBlockMixin {

    @ModifyExpressionValue(method = "getPower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean adjustForColoredVariants(boolean orignal, @Local(ordinal = 1) BlockState blockState){
        return orignal || blockState.getBlock() instanceof RedstoneWireBlock;
    }
}
