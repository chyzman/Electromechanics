package com.chyzman.chyzyLogistics.block.gate;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

import java.util.function.Function;

public class MonoGateBlock extends GateBlock {

    public final Function<Boolean, Boolean> shouldEmitPower;

    public MonoGateBlock(Function<Boolean, Boolean> shouldEmitPower, Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager
                        .getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(POWERED, Boolean.FALSE)
        );
        this.shouldEmitPower = shouldEmitPower;
    }

    public boolean wireConnectsTo(BlockState state, Direction dir) {
        return dir == state.get(FACING) || dir == state.get(FACING).getOpposite();
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!this.isLocked(world, pos, state)) {
            boolean powered = world.getEmittedRedstonePower(pos.offset(state.get(FACING)), state.get(FACING)) > 0;
            var tempState = state
                    .with(POWERED, this.shouldEmitPower.apply(powered));
            if (!tempState.equals(state)) {
                world.setBlockState(pos, tempState, Block.NOTIFY_LISTENERS);
                world.scheduleBlockTick(pos, this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
            }
        }
    }

    @Override
    protected int getPower(World world, BlockPos pos, BlockState state) {
        boolean powered = world.getEmittedRedstonePower(pos.offset(state.get(FACING)), state.get(FACING)) > 0;
        return this.shouldEmitPower.apply(powered) ? 15 : 0;
    }
}