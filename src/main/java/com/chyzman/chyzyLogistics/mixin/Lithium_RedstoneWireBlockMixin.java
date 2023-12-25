package com.chyzman.chyzyLogistics.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RedstoneWireBlock.class, priority = 1500)
public abstract class Lithium_RedstoneWireBlockMixin {

    @TargetHandler(
            mixin = "me.jellysquid.mods.lithium.mixin.block.redstone_wire.RedstoneWireBlockMixin",
            name = "getReceivedPower"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean adjustIsOfCheck1(boolean orignal, @Local(ordinal = 0) BlockState neighbor){
        return neighbor.getBlock() instanceof RedstoneWireBlock || orignal;
    }

    @TargetHandler(
            mixin = "me.jellysquid.mods.lithium.mixin.block.redstone_wire.RedstoneWireBlockMixin",
            name = "getStrongPowerTo"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean adjustIsOfCheck2(boolean orignal, @Local(ordinal = 0) BlockState neighbor){
        return neighbor.getBlock() instanceof RedstoneWireBlock || orignal;
    }

    //--

    @TargetHandler(
            mixin = "me.jellysquid.mods.lithium.mixin.block.redstone_wire.RedstoneWireBlockMixin",
            name = "getPowerFromSide"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 0))
    private boolean adjustIsOfCheck3(boolean orignal, @Local(ordinal = 0) BlockState neighbor){
        return neighbor.getBlock() instanceof RedstoneWireBlock || orignal;
    }

    @TargetHandler(
            mixin = "me.jellysquid.mods.lithium.mixin.block.redstone_wire.RedstoneWireBlockMixin",
            name = "getPowerFromSide"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 1))
    private boolean adjustIsOfCheck4(boolean orignal, @Local(ordinal = 1) BlockState aboveState){
        return aboveState.getBlock() instanceof RedstoneWireBlock || orignal;
    }

    @TargetHandler(
            mixin = "me.jellysquid.mods.lithium.mixin.block.redstone_wire.RedstoneWireBlockMixin",
            name = "getPowerFromSide"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 2))
    private boolean adjustIsOfCheck5(boolean orignal, @Local(name = "belowState") BlockState belowState){
        return belowState.getBlock() instanceof RedstoneWireBlock || orignal;
    }
}
