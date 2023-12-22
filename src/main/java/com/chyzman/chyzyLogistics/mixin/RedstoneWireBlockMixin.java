package com.chyzman.chyzyLogistics.mixin;

import com.chyzman.chyzyLogistics.block.ColoredRedstoneWireBlock;
import com.chyzman.chyzyLogistics.block.gate.MonoGateBlock;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin {

    @Shadow @Final public static Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY;

    @Shadow protected abstract WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction, boolean bl);

    @Unique
    private static boolean GLOBAL_WIRES_GIVE_POWER = false;

    @Unique
    private static Block GLOBAL_LOCK_TYPE = null;

    @Inject(method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z",
            at = @At("HEAD"), cancellable = true)
    private static void dontConnectToMyDamnGates(BlockState state, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof MonoGateBlock gateBlock && !gateBlock.wireConnectsTo(state, dir)) {
            cir.setReturnValue(false);
        }

        if (state.getBlock() instanceof ColoredRedstoneWireBlock) {
            cir.setReturnValue(true);
        }
    }

    @ModifyExpressionValue(method = "increasePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean adjustForColoredVariants(boolean orignal, @Local(argsOnly = true) BlockState state){
        var block = state.getBlock();

        if(!(block instanceof RedstoneWireBlock)) return false;

        return ColoredRedstoneWireBlock.isValid((Block) (Object) this, block);
    }

    // --

    @Inject(
            method = "getRenderConnectionType(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/enums/WireConnection;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BlockView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 0, shift = At.Shift.BY, by = 2),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void preventConnectionIfInvalid(BlockView world, BlockPos pos, Direction direction, boolean bl, CallbackInfoReturnable<WireConnection> cir, BlockPos blockPos, BlockState blockState){
        if(!ColoredRedstoneWireBlock.isValid((Block) (Object) this, blockState.getBlock())) {
            cir.setReturnValue(WireConnection.NONE);
        }
    }

    @WrapOperation(
            method = "getRenderConnectionType(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/enums/WireConnection;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;connectsTo(Lnet/minecraft/block/BlockState;)Z")
    )
    private boolean test(BlockState state, Operation<Boolean> original){
        if (!ColoredRedstoneWireBlock.isValid((Block) (Object) this, state.getBlock())) {
            return false;
        }

        return original.call(state);
    }

    // --

    @Inject(
            method = "getReceivedRedstonePower",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getReceivedRedstonePower(Lnet/minecraft/util/math/BlockPos;)I", shift = At.Shift.BEFORE, id = "b")
    )
    private void globalToggleOff(World world, BlockPos pos, CallbackInfoReturnable<Integer> cir){
        //if(((Object) this == Blocks.REDSTONE_WIRE)) return;

        GLOBAL_LOCK_TYPE = (Block) (Object) this;
        GLOBAL_WIRES_GIVE_POWER = false;
    }

    @Inject(
            method = "getReceivedRedstonePower",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getReceivedRedstonePower(Lnet/minecraft/util/math/BlockPos;)I", shift = At.Shift.AFTER, id = "a")
    )
    private void globalToggleOn(World world, BlockPos pos, CallbackInfoReturnable<Integer> cir){
        //if(((Object) this == Blocks.REDSTONE_WIRE)) return;

        GLOBAL_LOCK_TYPE = null;
        GLOBAL_WIRES_GIVE_POWER = true;
    }

    @ModifyExpressionValue(
            method = { /*"getStrongRedstonePower",*/ "getWeakRedstonePower", "emitsRedstonePower" },
            at = @At(value = "FIELD", target = "Lnet/minecraft/block/RedstoneWireBlock;wiresGivePower:Z")
    )
    private boolean useGlobalGivePower(boolean original){
        //if(((Object) this == Blocks.REDSTONE_WIRE)) return original;

        return original && GLOBAL_WIRES_GIVE_POWER;
    }

    @ModifyExpressionValue(
            method = { "getStrongRedstonePower" },
            at = @At(value = "FIELD", target = "Lnet/minecraft/block/RedstoneWireBlock;wiresGivePower:Z")
    )
    private boolean useGlobalGivePower1(boolean original){
        //if(((Object) this == Blocks.REDSTONE_WIRE)) return original;

        //if(((Object) this == Blocks.REDSTONE_WIRE) && GLOBAL_LOCK_TYPE instanceof ColoredRedstoneWireBlock) return original;

        return original && GLOBAL_WIRES_GIVE_POWER;
    }

    // --

    @ModifyReturnValue(method = "getPlacementState(Lnet/minecraft/world/BlockView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", at = @At(value = "RETURN", ordinal = 1))
    private BlockState adjustState(BlockState state, @Local(argsOnly = true) BlockView world, @Local(argsOnly = true) BlockPos pos){
        boolean bl = !world.getBlockState(pos.up()).isSolidBlock(world, pos);

        for(Direction direction : Direction.Type.HORIZONTAL) {
            Block otherBlock = world.getBlockState(pos.offset(direction)).getBlock();

            if(ColoredRedstoneWireBlock.isValid((Block) (Object) this, otherBlock)) continue;

            WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction, bl);

            if(wireConnection == WireConnection.NONE){
                var oppositeDirection = direction.getOpposite();

                WireConnection oppositeWireConnection = this.getRenderConnectionType(world, pos, oppositeDirection, bl);

                if(oppositeWireConnection == WireConnection.NONE){
                    state = state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(oppositeDirection), WireConnection.NONE);
                }
            }

            state = state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection);
        }

        return state;
    }
}