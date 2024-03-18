package com.chyzman.electromechanics.mixin;

import com.chyzman.electromechanics.block.slime.SlimeSlab;
import com.chyzman.electromechanics.data.SlimeTags;
import com.chyzman.electromechanics.util.Colored;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = PistonHandler.class, priority = 100)
public abstract class PistonHandlerMixin {

    @Shadow @Final private World world;
    @Shadow @Final private Direction motionDirection;
    @Shadow @Final private BlockPos posFrom;

    //----------------------------------------------------------------------------//

    @Inject(method = "isBlockSticky", at = @At(value = "HEAD"), cancellable = true)
    private static void isBlockStickyExt(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if(state.isIn(SlimeTags.Blocks.STICKY_BLOCKS)) cir.setReturnValue(true);
    }

    @Inject(method = "isAdjacentBlockStuck", at = @At(value = "HEAD"), cancellable = true)
    private static void isAdjacentBlockStuckExt(BlockState state, BlockState adjacentState, CallbackInfoReturnable<Boolean> cir) {
        Block block1 = state.getBlock();
        Block block2 = adjacentState.getBlock();

        boolean bl1 = state.isIn(SlimeTags.Blocks.STICKY_BLOCKS);
        boolean bl2 = adjacentState.isIn(SlimeTags.Blocks.STICKY_BLOCKS);

        Boolean returnValue = null;

        if(bl1 && bl2){
            DyeColor dye1 = block1 instanceof Colored colored1 ? colored1.getColor() : null;
            DyeColor dye2 = block2 instanceof Colored colored2 ? colored2.getColor() : null;

            returnValue = block1 == block2 || (dye1 == dye2 && !(dye2 == null && isNotHoneySticking(state, adjacentState)));
        } else if(bl1 || bl2) {
            returnValue = true;
        }

        if(returnValue != null) cir.setReturnValue(returnValue);
    }

    @Unique
    private static boolean isNotHoneySticking(BlockState state, BlockState adjacentState){
        return checkAndCompareHoney(state, adjacentState) || checkAndCompareHoney(adjacentState, state);
    }

    @Unique
    private static boolean checkAndCompareHoney(BlockState state, BlockState adjacentState){
        return state.isOf(Blocks.HONEY_BLOCK) && !adjacentState.isOf(Blocks.HONEY_BLOCK);
    }

    //----------------------------------------------------------------------------//

    @Inject(method = "calculatePush", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", shift = At.Shift.BY, by = 2, ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void shouldCancelCalculateEarly(CallbackInfoReturnable<Boolean> cir, BlockState blockState) {
        if (!(SlimeSlab.isSlimeSlab(blockState))) return;

        boolean motionDirUp = this.motionDirection == Direction.UP;
        boolean pistonDirUp = this.world.getBlockState(this.posFrom).get(FacingBlock.FACING) == Direction.UP;
        boolean typeIsTop = blockState.get(SlabBlock.TYPE) == SlabType.TOP;

        boolean bl = ((!motionDirUp && pistonDirUp) && typeIsTop) || ((motionDirUp && !pistonDirUp) && !typeIsTop);

        if(bl) cir.setReturnValue(true);
    }

    //----------------------------------------------------------------------------//

    // Another check with the required context with to direction of the motion
    @WrapOperation(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"))
    private boolean test(BlockState prevState, BlockState nextState, Operation<Boolean> original){
        if (!(SlimeSlab.isSlimeSlab(prevState))) return original.call(prevState, nextState);

        boolean type2IsTop = prevState.get(SlabBlock.TYPE) == SlabType.TOP;
        boolean motionDirectionUp = this.motionDirection == Direction.UP;
        boolean horizontalDirection = this.motionDirection.getId() >= 2;

        if(horizontalDirection){
            if (!(SlimeSlab.isSlimeSlab(nextState))) return original.call(prevState, nextState);

            boolean typeIsTop = nextState.get(SlabBlock.TYPE) == SlabType.TOP;

            if (type2IsTop != typeIsTop || motionDirectionUp != typeIsTop) {
                return false;
            }
        } else if (motionDirectionUp == type2IsTop) {
            return false;
        }

        return original.call(prevState, nextState);
    }

    //----------------------------------------------------------------------------//

    @Unique
    private static final Direction[] HORIZONTAL = Direction.Type.HORIZONTAL.stream().toArray(Direction[]::new);

    // Used to call directions that should not be checked since it is
    // top slab or bottom slab meaning the bottom direction and
    // top direction respectfully does not matter based on the block
    @ModifyExpressionValue(method = "tryMoveAdjacentBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
    private Direction[] filterDirectionsIfSlab(Direction[] original, @Local() BlockState blockState, @Share("isSlimeSlab") LocalBooleanRef isSlimeSlab) {
        isSlimeSlab.set(SlimeSlab.isSlimeSlab(blockState));

        if (!(isSlimeSlab.get() && this.motionDirection.getId() >= 2)) return original;

        return ArrayUtils.addAll(
                new Direction[] { (blockState.get(SlabBlock.TYPE) == SlabType.TOP) ? Direction.UP : Direction.DOWN },
                HORIZONTAL);
    }

    // Used to further cull weather the two blocks are sticking to each
    // other based on the idea that one is a slime slab but not sticking to each other
    @WrapOperation(method = "tryMoveAdjacentBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"))
    private boolean checkIfSlabsStick(BlockState blockState2, BlockState blockState, Operation<Boolean> original, @Local() Direction direction, @Share("isSlimeSlab") LocalBooleanRef isSlimeSlab){
        if (SlimeSlab.isSlimeSlab(blockState2)) {
            boolean type2IsTop = blockState2.get(SlabBlock.TYPE) == SlabType.TOP;

            if (isSlimeSlab.get() && direction.getId() >= 2 && (blockState.get(SlabBlock.TYPE) == SlabType.TOP) != type2IsTop) return false;
            if ((direction == Direction.UP) == type2IsTop) return false;
        }

        return original.call(blockState2, blockState);
    }

    //----------------------------------------------------------------------------//

}