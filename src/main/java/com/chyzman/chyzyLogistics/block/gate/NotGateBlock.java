package com.chyzman.chyzyLogistics.block.gate;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class NotGateBlock extends GateBlock {
    public NotGateBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager
                        .getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(POWERED, Boolean.FALSE)
        );
    }

    @Override
    protected MapCodec<? extends AbstractRedstoneGateBlock> getCodec() {
        return null;
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 2;
    }

    public boolean wireConnectsTo(BlockState state, Direction dir) {
        return super.wireConnectsTo(state, dir);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get(POWERED)) {
            return 0;
        } else {
            return state.get(FACING) == direction ? this.getOutputLevel(world, pos, state) : 0;
        }
    }
}