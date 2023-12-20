package com.chyzman.chyzyLogistics.mixin;

import com.chyzman.chyzyLogistics.data.SlimeTags;
import com.chyzman.chyzyLogistics.util.Colored;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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

    //----------------------------------------------------------------------------//

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
    private void firstBlockCulling(CallbackInfoReturnable<Boolean> cir, BlockState blockState) {
        if (!blockState.isIn(SlimeTags.Blocks.SLIME_SLABS) /*&& !blockState.contains(SlabBlock.TYPE)*/) return;

        boolean motionDirUp = this.motionDirection == Direction.UP;
        boolean pistonDirUp = this.world.getBlockState(this.posFrom).get(FacingBlock.FACING) == Direction.UP;
        boolean typeIsTop = blockState.get(SlabBlock.TYPE) == SlabType.TOP;

        boolean bl = ((!motionDirUp && pistonDirUp) && typeIsTop) || ((motionDirUp && !pistonDirUp) && !typeIsTop);

        if(bl) cir.setReturnValue(true);
    }

    //----------------------------------------------------------------------------//

//    @Unique private boolean gelatin$setToAir = false;

//    @Inject(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 1, shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    @ModifyVariable(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", shift = At.Shift.BY, by = 2, ordinal = 1), ordinal = 0) //@WrapOperation(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 1))
    private BlockState setBlockState(BlockState blockState, @Local(argsOnly = true) BlockPos pos, @Local(ordinal = 0) int i){
        //Used as a fix for some weird mixin problem with doing and an inject and modify variable in this way
        //It breaks the local capture of the blockstate within the OG equation
        BlockState blockState2 = this.world.getBlockState(pos.offset(this.motionDirection.getOpposite(), i - 1));

        //--------------------------\/--\/--\/------------------------------\\
        if (!(blockState2.isIn(SlimeTags.Blocks.SLIME_SLABS) /*|| blockState2.contains(SlabBlock.TYPE)*/)) {
            return blockState;
        }

        boolean type2IsTop = blockState2.get(SlabBlock.TYPE) == SlabType.TOP;

        boolean motionDirectionUp = this.motionDirection == Direction.UP;

        if (motionDirectionUp == type2IsTop) return Blocks.AIR.getDefaultState();

        if (blockState.isIn(SlimeTags.Blocks.SLIME_SLABS) /*|| blockState.contains(SlabBlock.TYPE)*/) {
            boolean typeIsTop = blockState.get(SlabBlock.TYPE) == SlabType.TOP;

            if (this.motionDirection.getId() >= 2) {
                if (!type2IsTop && typeIsTop) return Blocks.AIR.getDefaultState();
                if (type2IsTop && !typeIsTop) return Blocks.AIR.getDefaultState();
            }

            if (motionDirectionUp && !typeIsTop) return Blocks.AIR.getDefaultState();
            if (!motionDirectionUp && typeIsTop) return Blocks.AIR.getDefaultState();
        }

        return blockState;
    }

//    @ModifyVariable(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", shift = At.Shift.BY, by = 2, ordinal = 1), ordinal = 0)
//    private BlockState setBlockState(BlockState state) {
//        return gelatin$setToAir ? Blocks.AIR.getDefaultState() : state;
//    }

    //----------------------------------------------------------------------------//

    @ModifyExpressionValue(method = "tryMoveAdjacentBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
    private Direction[] test1(Direction[] original, @Local() BlockState blockState, @Share("SlabType") LocalRef<SlabType> slabType){
        ObjectArrayList<Direction> directions = new ObjectArrayList<>();

        for (Direction direction : original) {
            if (blockState.isIn(SlimeTags.Blocks.SLIME_SLABS)/* || blockState.contains(SlabBlock.TYPE)*/ && this.motionDirection.getId() >= 2) {
                slabType.set(blockState.get(SlabBlock.TYPE));

                if (slabType.get() == SlabType.BOTTOM && direction == Direction.UP) {
                    continue;
                } else if (slabType.get() == SlabType.TOP && direction == Direction.DOWN) {
                    continue;
                }
            }

            directions.add(direction);
        }

        return directions.toArray(Direction[]::new);
    }

    @WrapOperation(method = "tryMoveAdjacentBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"))
    private boolean test2(BlockState blockState2, BlockState blockState, Operation<Boolean> original, @Local() Direction direction, @Share("SlabType") LocalRef<SlabType> slabType){
        if (blockState2.isIn(SlimeTags.Blocks.SLIME_SLABS) /*|| blockState2.contains(SlabBlock.TYPE)*/) {
            SlabType type2 = blockState2.get(SlabBlock.TYPE);

            if (direction.getId() >= 2) {
                if (slabType.get() == SlabType.TOP && type2 == SlabType.BOTTOM) {
                    return false;
                } else if (slabType.get() == SlabType.BOTTOM && type2 == SlabType.TOP) {
                    return false;
                }
            }

            if (direction == Direction.DOWN && type2 == SlabType.BOTTOM) return false;
            if (direction == Direction.UP && type2 == SlabType.TOP) return false;
        }

        return original.call(blockState2, blockState);
    }

    //----------------------------------------------------------------------------//

}