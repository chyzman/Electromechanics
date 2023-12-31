package com.chyzman.electromechanics.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.Map;

public class SternCopperBlock extends HorizontalFacingBlock {

    public SternCopperBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var retract = player.isSneaking();
        Direction dir = hit.getSide();
        world.setBlockState(pos, dir.getAxis().isHorizontal() ? state.with(FACING, retract ? dir : dir.getOpposite()) : state);
        if (move(world, pos.offset(dir, retract ? -1 : 1), retract ? dir : dir.getOpposite())) {
            world.playSound(null, pos, retract ? SoundEvents.BLOCK_PISTON_CONTRACT :SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
            world.emitGameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of(state));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private static boolean move(World world, BlockPos pos, Direction dir) {
        PistonHandler pistonHandler = new PistonHandler(world, pos, dir, true);
        if (!pistonHandler.calculatePush()) {
            return false;
        } else {
            Map<BlockPos, BlockState> movedBlocks = Maps.newHashMap();
            List<BlockState> list2 = Lists.newArrayList();

            for (BlockPos blockPos2 : pistonHandler.getMovedBlocks()) {
                BlockState blockState = world.getBlockState(blockPos2);
                list2.add(blockState);
                movedBlocks.put(blockPos2, blockState);
            }

            BlockState[] blockStates = new BlockState[pistonHandler.getMovedBlocks().size() + pistonHandler.getBrokenBlocks().size()];
            int j = 0;

            for (int k = pistonHandler.getBrokenBlocks().size() - 1; k >= 0; --k) {
                BlockPos blockPos3 = pistonHandler.getBrokenBlocks().get(k);
                BlockState blockState2 = world.getBlockState(blockPos3);
                BlockEntity blockEntity = blockState2.hasBlockEntity() ? world.getBlockEntity(blockPos3) : null;
                dropStacks(blockState2, world, blockPos3, blockEntity);
                world.setBlockState(blockPos3, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
                world.emitGameEvent(GameEvent.BLOCK_DESTROY, blockPos3, GameEvent.Emitter.of(blockState2));
                if (!blockState2.isIn(BlockTags.FIRE)) {
                    world.addBlockBreakParticles(blockPos3, blockState2);
                }

                blockStates[j++] = blockState2;
            }

            for (int k = pistonHandler.getMovedBlocks().size() - 1; k >= 0; --k) {
                BlockPos blockPos3 = pistonHandler.getMovedBlocks().get(k);
                BlockState blockState2 = world.getBlockState(blockPos3);
                blockPos3 = blockPos3.offset(dir);
                movedBlocks.remove(blockPos3);
                BlockState blockState3 = Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, dir);
                world.setBlockState(blockPos3, blockState3, Block.NO_REDRAW | Block.MOVED);
                world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston(blockPos3, blockState3, list2.get(k), dir, true, false));
                blockStates[j++] = blockState2;
            }

            BlockState blockState5 = Blocks.AIR.getDefaultState();

            for (BlockPos blockPos4 : movedBlocks.keySet()) {
                world.setBlockState(blockPos4, blockState5, Block.NOTIFY_LISTENERS | Block.FORCE_STATE | Block.MOVED);
            }

            for (Map.Entry<BlockPos, BlockState> entry : movedBlocks.entrySet()) {
                BlockPos blockPos5 = entry.getKey();
                BlockState blockState6 = entry.getValue();
                blockState6.prepare(world, blockPos5, 2);
                blockState5.updateNeighbors(world, blockPos5, Block.NOTIFY_LISTENERS);
                blockState5.prepare(world, blockPos5, 2);
            }

            j = 0;

            for (int l = pistonHandler.getBrokenBlocks().size() - 1; l >= 0; --l) {
                BlockState blockState2 = blockStates[j++];
                BlockPos blockPos5 = pistonHandler.getBrokenBlocks().get(l);
                blockState2.prepare(world, blockPos5, 2);
                world.updateNeighborsAlways(blockPos5, blockState2.getBlock());
            }

            for (int l = pistonHandler.getMovedBlocks().size() - 1; l >= 0; --l) {
                world.updateNeighborsAlways(pistonHandler.getMovedBlocks().get(l), blockStates[j++].getBlock());
            }

            return true;
        }
    }
}