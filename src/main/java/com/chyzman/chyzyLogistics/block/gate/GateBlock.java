package com.chyzman.chyzyLogistics.block.gate;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.Direction;

public abstract class GateBlock extends AbstractRedstoneGateBlock {
    public GateBlock(Settings settings) {
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

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    public abstract boolean wireConnectsTo(BlockState state, Direction dir);
}