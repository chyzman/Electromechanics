package com.chyzman.electromechanics.mixin.lithium;

import com.bawnorton.mixinsquared.TargetHandler;
import com.chyzman.electromechanics.block.redstone.RedstoneEvents;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RedstoneWireBlock.class, priority = 1500)
public abstract class Lithium_RedstoneWireBlockMixin {

    @TargetHandler(
            mixin = "net.caffeinemc.mods.lithium.mixin.block.redstone_wire.RedStoneWireBlockMixin",
            name = "getReceivedPower"
    )
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean adjustIsOfCheck1(BlockState state, Block block, Operation<Boolean> orignal){
        return orignal.call(state, block) || state.getBlock() instanceof RedstoneWireBlock;
    }

    @TargetHandler(
            mixin = "net.caffeinemc.mods.lithium.mixin.block.redstone_wire.RedStoneWireBlockMixin",
            name = "getStrongPowerTo"
    )
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean adjustIsOfCheck2(BlockState state, Block block, Operation<Boolean> orignal){
        return orignal.call(state, block) || state.getBlock() instanceof RedstoneWireBlock;
    }

    //--

    @TargetHandler(
            mixin = "net.caffeinemc.mods.lithium.mixin.block.redstone_wire.RedStoneWireBlockMixin",
            name = "getPowerFromSide"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"))
    private void initData(World world, BlockPos pos2, Direction direction, boolean checkWiresAbove, CallbackInfoReturnable<Integer> cir, @Share(namespace = "electromechanics", value = "pos") LocalRef<BlockPos> pos, @Share(namespace = "electromechanics", value = "state") LocalRef<BlockState> state){
        pos.set(pos2.offset(direction.getOpposite()));

        state.set(world.getBlockState(pos.get()));
    }

    @TargetHandler(
            mixin = "net.caffeinemc.mods.lithium.mixin.block.redstone_wire.RedStoneWireBlockMixin",
            name = "getPowerFromSide"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 0))
    private boolean adjustIsOfCheck3(boolean orignal, @Local(argsOnly = true) World world, @Local(argsOnly = true) BlockPos pos2, @Local(ordinal = 0) BlockState state2, @Share(namespace = "electromechanics", value = "pos") LocalRef<BlockPos> pos, @Share(namespace = "electromechanics", value = "state") LocalRef<BlockState> state){
        return orignal || RedstoneEvents.isValid(world, pos.get(), state.get(), pos2, state2);
    }

    @TargetHandler(
            mixin = "net.caffeinemc.mods.lithium.mixin.block.redstone_wire.RedStoneWireBlockMixin",
            name = "getPowerFromSide"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 1))
    private boolean adjustIsOfCheck4(boolean orignal, @Local(argsOnly = true) World world, @Local(ordinal = 1) BlockPos up, @Local(ordinal = 1) BlockState aboveState, @Share(namespace = "electromechanics", value = "pos") LocalRef<BlockPos> pos, @Share(namespace = "electromechanics", value = "state") LocalRef<BlockState> state){
        return orignal || RedstoneEvents.isValid(world, pos.get(), state.get(), up, aboveState);
    }

    @TargetHandler(
            mixin = "net.caffeinemc.mods.lithium.mixin.block.redstone_wire.RedStoneWireBlockMixin",
            name = "getPowerFromSide"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 2))
    private boolean adjustIsOfCheck5(boolean orignal, @Local(argsOnly = true) World world, @Local(name = "down") BlockPos down, @Local(name = "belowState") BlockState belowState, @Share(namespace = "electromechanics", value = "pos") LocalRef<BlockPos> pos, @Share(namespace = "electromechanics", value = "state") LocalRef<BlockState> state){
        return orignal || RedstoneEvents.isValid(world, pos.get(), state.get(), down, belowState);
    }
}
