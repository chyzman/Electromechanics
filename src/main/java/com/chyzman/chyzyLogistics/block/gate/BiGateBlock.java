package com.chyzman.chyzyLogistics.block.gate;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

import java.util.function.BiFunction;

public class BiGateBlock extends GateBlock {
    public static final BooleanProperty RIGHT_POWERED = BooleanProperty.of("right_powered");
    public static final BooleanProperty LEFT_POWERED = BooleanProperty.of("left_powered");

    public final BiFunction<Boolean, Boolean, Boolean> shouldEmitPower;

    public BiGateBlock(BiFunction<Boolean, Boolean, Boolean> shouldEmitPower, Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager
                        .getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(POWERED, Boolean.FALSE)
                        .with(RIGHT_POWERED, Boolean.FALSE)
                        .with(LEFT_POWERED, Boolean.FALSE)
        );
        this.shouldEmitPower = shouldEmitPower;
    }

    public boolean wireConnectsTo(BlockState state, Direction dir) {
        return dir.getAxis().isHorizontal() && dir != state.get(FACING).getOpposite();
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!this.isLocked(world, pos, state)) {
            boolean rightPowered = world.getEmittedRedstonePower(pos.offset(state.get(FACING).rotateYClockwise()), state.get(FACING).rotateYClockwise()) > 0;
            boolean leftPowered = world.getEmittedRedstonePower(pos.offset(state.get(FACING).rotateYCounterclockwise()), state.get(FACING).rotateYCounterclockwise()) > 0;
            var tempState = state
                    .with(RIGHT_POWERED, rightPowered)
                    .with(LEFT_POWERED, leftPowered)
                    .with(POWERED, this.shouldEmitPower.apply(rightPowered, leftPowered));
            if (!tempState.equals(state)) {
                world.setBlockState(pos, tempState, Block.NOTIFY_LISTENERS);
                world.scheduleBlockTick(pos, this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
            }
        }
    }

    @Override
    protected int getPower(World world, BlockPos pos, BlockState state) {
        boolean rightPowered = world.getEmittedRedstonePower(pos.offset(state.get(FACING).rotateYClockwise()), state.get(FACING).rotateYClockwise()) > 0;
        boolean leftPowered = world.getEmittedRedstonePower(pos.offset(state.get(FACING).rotateYCounterclockwise()), state.get(FACING).rotateYCounterclockwise()) > 0;
        return this.shouldEmitPower.apply(rightPowered, leftPowered) ? 15 : 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(RIGHT_POWERED, LEFT_POWERED);
    }

}