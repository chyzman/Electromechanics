package com.chyzman.chyzyLogistics.mixin;

import com.chyzman.chyzyLogistics.block.gate.MonoGateBlock;
import com.chyzman.chyzyLogistics.block.redstone.RedstoneEvents;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin {

    @Shadow @Final public static Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY;

    @Shadow protected abstract WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction, boolean bl);

    @Unique
    private static boolean GLOBAL_WIRES_GIVE_POWER = false;

    @Inject(method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z",
            at = @At("HEAD"), cancellable = true)
    private static void handleCustomConnections(BlockState state, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof MonoGateBlock gateBlock && !gateBlock.wireConnectsTo(state, dir)) {
            cir.setReturnValue(false);
        }

        if (state.getBlock() instanceof RedstoneWireBlock) {
            cir.setReturnValue(true);
        }
    }

    // --

    @Inject(
            method = "getReceivedRedstonePower",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getReceivedRedstonePower(Lnet/minecraft/util/math/BlockPos;)I", shift = At.Shift.BEFORE, id = "b")
    )
    private void globalToggleOff(World world, BlockPos pos, CallbackInfoReturnable<Integer> cir){
        GLOBAL_WIRES_GIVE_POWER = false;
    }

    @Inject(
            method = "getReceivedRedstonePower",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getReceivedRedstonePower(Lnet/minecraft/util/math/BlockPos;)I", shift = At.Shift.AFTER, id = "a")
    )
    private void globalToggleOn(World world, BlockPos pos, CallbackInfoReturnable<Integer> cir){
        GLOBAL_WIRES_GIVE_POWER = true;
    }

    @ModifyExpressionValue(
            method = { "getStrongRedstonePower", "getWeakRedstonePower", "emitsRedstonePower" },
            at = @At(value = "FIELD", target = "Lnet/minecraft/block/RedstoneWireBlock;wiresGivePower:Z")
    )
    private boolean useGlobalGivePower(boolean original){
        return original && GLOBAL_WIRES_GIVE_POWER;
    }

    // --

    @WrapOperation(method = "getReceivedRedstonePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;increasePower(Lnet/minecraft/block/BlockState;)I", ordinal = 0))
    private int validConnectionCheck(RedstoneWireBlock instance, BlockState state, Operation<Integer> original, @Local(argsOnly = true) World world, @Local(argsOnly = true) BlockPos pos, @Local() Direction direction){
        return isValid(world, direction, pos, null) ? original.call(instance, state) : 0;
    }

    @WrapOperation(method = "getReceivedRedstonePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;increasePower(Lnet/minecraft/block/BlockState;)I", ordinal = 1))
    private int validConnectionCheckUp(RedstoneWireBlock instance, BlockState state, Operation<Integer> original, @Local(argsOnly = true) World world, @Local(argsOnly = true) BlockPos pos, @Local() Direction direction){
        return isValid(world, direction, pos, Direction.UP) ? original.call(instance, state) : 0;
    }

    @WrapOperation(method = "getReceivedRedstonePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;increasePower(Lnet/minecraft/block/BlockState;)I", ordinal = 2))
    private int validConnectionCheckDown(RedstoneWireBlock instance, BlockState state, Operation<Integer> original, @Local(argsOnly = true) World world, @Local(argsOnly = true) BlockPos pos, @Local() Direction direction){
        return isValid(world, direction, pos, Direction.DOWN) ? original.call(instance, state) : 0;
    }

    //--

    @ModifyExpressionValue(method = "increasePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean adjustForColoredVariants(boolean orignal, @Local(argsOnly = true) BlockState state){
        return state.getBlock() instanceof RedstoneWireBlock || orignal;
    }

    // --

    @Inject(
            method = "getRenderConnectionType(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/enums/WireConnection;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BlockView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 0, shift = At.Shift.BY, by = 2),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void preventConnectionIfInvalid(BlockView world, BlockPos pos, Direction direction, boolean bl, CallbackInfoReturnable<WireConnection> cir, BlockPos blockPos, BlockState blockState){
        if(!isValid(world, direction, pos, null)) cir.setReturnValue(WireConnection.NONE);
    }

    @WrapOperation(
            method = "getRenderConnectionType(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/enums/WireConnection;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;connectsTo(Lnet/minecraft/block/BlockState;)Z", ordinal = 0)
    )
    private boolean validConnectionCheckUp(BlockState state2, Operation<Boolean> original, @Local(argsOnly = true) BlockView world, @Local(argsOnly = true) Direction direction, @Local(argsOnly = true) BlockPos pos){
        if(!isValid(world, direction, pos, Direction.UP)) return false;

        return original.call(state2);
    }

    @WrapOperation(
            method = "getRenderConnectionType(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/enums/WireConnection;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;connectsTo(Lnet/minecraft/block/BlockState;)Z", ordinal = 1)
    )
    private boolean validConnectionCheckDown(BlockState state2, Operation<Boolean> original, @Local(argsOnly = true) BlockView world, @Local(argsOnly = true) Direction direction, @Local(argsOnly = true) BlockPos pos){
        if(!isValid(world, direction, pos, Direction.DOWN)) return false;

        return original.call(state2);
    }

    // --

    @Unique
    private final ThreadLocal<@Nullable BlockState> blockStateCache = ThreadLocal.withInitial(() -> null);

    @ModifyReturnValue(method = "getPlacementState(Lnet/minecraft/world/BlockView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", at = @At(value = "RETURN", ordinal = 1))
    private BlockState adjustState(BlockState state, @Local(argsOnly = true) BlockView world, @Local(argsOnly = true) BlockPos pos){
        boolean bl = !world.getBlockState(pos.up()).isSolidBlock(world, pos);

        if(world instanceof WorldView worldView && worldView.isClient()) {
            System.out.println();
            System.out.println(state.toString());
        }

        for(Direction direction : Direction.Type.HORIZONTAL) {
            if(isValid(world, direction, pos, state,null)) continue;

            blockStateCache.set(state);

            WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction, bl);

            if(wireConnection == WireConnection.NONE){
                var oppositeDirection = direction.getOpposite();

                WireConnection oppositeWireConnection = this.getRenderConnectionType(world, pos, oppositeDirection, bl);

                if(oppositeWireConnection == WireConnection.NONE){
                    state = state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(oppositeDirection), WireConnection.NONE);
                }
            }

            blockStateCache.set(null);

            state = state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection);
        }

        if(world instanceof WorldView worldView && worldView.isClient()) System.out.println(state.toString());

        return state;
    }

    @Inject(
            method = "getDefaultWireState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;getRenderConnectionType(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/enums/WireConnection;", shift = At.Shift.BEFORE)
    )
    private void setCache(BlockView world, BlockState state, BlockPos pos, CallbackInfoReturnable<BlockState> cir){
        this.blockStateCache.set(state);
    }

    @Inject(
            method = "getDefaultWireState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;getRenderConnectionType(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/enums/WireConnection;", shift = At.Shift.AFTER)
    )
    private void clearCache(BlockView world, BlockState state, BlockPos pos, CallbackInfoReturnable<BlockState> cir){
        this.blockStateCache.set(null);
    }

    @Unique
    private boolean isValid(BlockView world, Direction primaryDir, BlockPos pos, @Nullable Direction additionalDir){
        var blockState = world.getBlockState(pos);

        if(!(blockState.getBlock() instanceof RedstoneWireBlock)) {
            var cacheState = blockStateCache.get();

            if(cacheState != null) {
                blockState = cacheState;
            }
        }

        return isValid(world, primaryDir, pos, blockState, additionalDir);
    }

    //--

    @Unique
    private static boolean isValid(BlockView world, Direction primaryDir, BlockPos pos, BlockState state, @Nullable Direction additionalDir){
        BlockPos pos2 = pos.offset(primaryDir);

        if(additionalDir != null) pos2 = pos2.offset(additionalDir);

        BlockState state2 = world.getBlockState(pos2);

        return !RedstoneEvents.SHOULD_CANCEl_CONNECTION.invoker()
                .shouldCancel(world, pos, state, pos2, state2);
    }

    //--

    @ModifyArg(
            method = "randomDisplayTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/RedstoneWireBlock;addPoweredParticles(Lnet/minecraft/world/World;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/Direction;FF)V"
            ),
            index = 3
    )
    private Vec3d checkForDifferentColor(Vec3d color, @Local(argsOnly = true) World world, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) BlockState state){
        var newColor = RedstoneEvents.PARTICLE_COLOR_GATHERER_EVENT.invoker().getColor(world, pos, state);

        return newColor != null ? newColor : color;
    }
}