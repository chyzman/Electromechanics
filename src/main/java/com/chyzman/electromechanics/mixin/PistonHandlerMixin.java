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

import java.util.stream.Stream;

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
        boolean bl1 = state.isIn(SlimeTags.Blocks.STICKY_BLOCKS);
        boolean bl2 = adjacentState.isIn(SlimeTags.Blocks.STICKY_BLOCKS);

        if(bl1 && bl2){
            var block1 = state.getBlock();
            var block2 = adjacentState.getBlock();

            if(block1 == block2) {
                cir.setReturnValue(true);

                return;
            }

            var dye1 = block1 instanceof Colored colored1 ? colored1.getColor() : null;
            var dye2 = block2 instanceof Colored colored2 ? colored2.getColor() : null;

            cir.setReturnValue(dye1 == dye2 && !(dye2 == null && isNotHoneySticking(state, adjacentState)));
        } else if(bl1 || bl2) {
            cir.setReturnValue(true);
        }
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

        if(((!motionDirUp && pistonDirUp) && typeIsTop) || ((motionDirUp && !pistonDirUp) && !typeIsTop)) {
            cir.setReturnValue(true);
        }
    }

    //----------------------------------------------------------------------------//

    // Another check with the required context with to direction of the motion
    @WrapOperation(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"))
    private boolean test(BlockState prevState, BlockState nextState, Operation<Boolean> original){
        if(SlimeSlab.isSlimeSlab(prevState)) {
            var slabType2 = prevState.get(SlabBlock.TYPE);

            if(this.motionDirection.getHorizontal() != -1) {
                if (SlimeSlab.isSlimeSlab(nextState) && nextState.get(SlabBlock.TYPE) != slabType2) {
                    return false;
                }
            } else if ((this.motionDirection == Direction.UP) == (slabType2 == SlabType.TOP)) {
                return false;
            }
        }

        if(SlimeSlab.isSlimeSlab(nextState) && (this.motionDirection.getOpposite() == Direction.UP) == (nextState.get(SlabBlock.TYPE) == SlabType.TOP)) {
            return false;
        }

        return original.call(prevState, nextState);
    }

    //----------------------------------------------------------------------------//

    @Unique private static final Direction[] HORIZONTAL_AND_DOWN = Stream.concat(Direction.Type.HORIZONTAL.stream(), Stream.of(Direction.DOWN)).toArray(Direction[]::new);
    @Unique private static final Direction[] HORIZONTAL_AND_UP = Stream.concat(Direction.Type.HORIZONTAL.stream(), Stream.of(Direction.UP)).toArray(Direction[]::new);

    // Used to call directions that should not be checked since it is
    // top slab or bottom slab meaning the bottom direction and
    // top direction respectfully does not matter based on the block
    @ModifyExpressionValue(method = "tryMoveAdjacentBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
    private Direction[] filterDirectionsIfSlab(Direction[] original, @Local() BlockState blockState, @Share("blockState_isSlimeSlab") LocalBooleanRef blockState_isSlimeSlab) {
        blockState_isSlimeSlab.set(SlimeSlab.isSlimeSlab(blockState));

        if (!(blockState_isSlimeSlab.get() && this.motionDirection.getId() >= 2)) return original;

        return blockState.get(SlabBlock.TYPE) == SlabType.TOP ? HORIZONTAL_AND_UP : HORIZONTAL_AND_DOWN;
    }

    // Used to further cull weather the two blocks are sticking to each
    // other based on the idea that one is a slime slab but not sticking to each other
    @WrapOperation(method = "tryMoveAdjacentBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"))
    private boolean checkIfSlabsStick(BlockState blockState2, BlockState blockState, Operation<Boolean> original, @Local() Direction direction, @Share("blockState_isSlimeSlab") LocalBooleanRef blockState_isSlimeSlab) {
        if(SlimeSlab.isSlimeSlab(blockState2)) {
            var slabType2 = blockState2.get(SlabBlock.TYPE);

            if(direction.getHorizontal() != -1) {
                if (blockState_isSlimeSlab.get() && blockState.get(SlabBlock.TYPE) != slabType2) {
                    return false;
                }
            } else if ((direction == Direction.UP) == (slabType2 == SlabType.TOP)) {
                return false;
            }
        }

        return original.call(blockState2, blockState);
    }

    //----------------------------------------------------------------------------//

}