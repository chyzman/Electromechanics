package com.chyzman.electromechanics.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.chyzman.electromechanics.block.redstone.RedstoneEvents;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RedstoneWireBlock.class, priority = 1500)
public abstract class Lithium_RedstoneWireBlockMixin {

    @Shadow @Final public static IntProperty POWER;

    @TargetHandler(
            mixin = "me.jellysquid.mods.lithium.mixin.block.redstone_wire.RedstoneWireBlockMixin",
            name = "getReceivedPower"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean adjustIsOfCheck1(boolean orignal, @Local(ordinal = 0) BlockState neighbor){
        return orignal || neighbor.getBlock() instanceof RedstoneWireBlock;
    }

    @TargetHandler(
            mixin = "me.jellysquid.mods.lithium.mixin.block.redstone_wire.RedstoneWireBlockMixin",
            name = "getStrongPowerTo"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean adjustIsOfCheck2(boolean orignal, @Local(ordinal = 0) BlockState neighbor){
        return orignal || neighbor.getBlock() instanceof RedstoneWireBlock;
    }

    //--

    @TargetHandler(
            mixin = "me.jellysquid.mods.lithium.mixin.block.redstone_wire.RedstoneWireBlockMixin",
            name = "getPowerFromSide"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"))
    private void initData(World world, BlockPos pos2, Direction direction, boolean checkWiresAbove, CallbackInfoReturnable<Integer> cir, @Share("pos") LocalRef<BlockPos> pos, @Share("state") LocalRef<BlockState> state){
        pos.set(pos2.offset(direction.getOpposite()));
        state.set(world.getBlockState(pos.get()));
    }

    @TargetHandler(
            mixin = "me.jellysquid.mods.lithium.mixin.block.redstone_wire.RedstoneWireBlockMixin",
            name = "getPowerFromSide"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 0))
    private boolean adjustIsOfCheck3(boolean orignal, @Local(argsOnly = true) World world, @Local(argsOnly = true) BlockPos pos2, @Local(ordinal = 0) BlockState state2, @Share("pos") LocalRef<BlockPos> pos, @Share("state") LocalRef<BlockState> state){
        return orignal || isValid(world, pos.get(), state.get(), pos2, state2);
    }

    @TargetHandler(
            mixin = "me.jellysquid.mods.lithium.mixin.block.redstone_wire.RedstoneWireBlockMixin",
            name = "getPowerFromSide"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 1))
    private boolean adjustIsOfCheck4(boolean orignal, @Local(argsOnly = true) World world, @Local(ordinal = 1) BlockPos up, @Local(ordinal = 1) BlockState aboveState, @Share("pos") LocalRef<BlockPos> pos, @Share("state") LocalRef<BlockState> state){
        return orignal || isValid(world, pos.get(), state.get(), up, aboveState);
    }

    @TargetHandler(
            mixin = "me.jellysquid.mods.lithium.mixin.block.redstone_wire.RedstoneWireBlockMixin",
            name = "getPowerFromSide"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 2))
    private boolean adjustIsOfCheck5(boolean orignal, @Local(argsOnly = true) World world, @Local(name = "down") BlockPos down, @Local(name = "belowState") BlockState belowState, @Share("pos") LocalRef<BlockPos> pos, @Share("state") LocalRef<BlockState> state){
        return orignal || isValid(world, pos.get(), state.get(), down, belowState);
    }

    @Unique
    private static boolean isValid(BlockView world, BlockPos pos, BlockState state, BlockPos pos2, BlockState state2){
        if(!(state2.getBlock() instanceof RedstoneWireBlock)) return false;

        return !RedstoneEvents.SHOULD_CANCEl_CONNECTION.invoker()
                .shouldCancel(world, pos, state, pos2, state2);
    }

}
