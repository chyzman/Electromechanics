package com.chyzman.chyzyLogistics.logic;

import com.chyzman.chyzyLogistics.block.gate.GateStateStorage;
import com.chyzman.chyzyLogistics.block.gate.ProGateBlock;
import com.chyzman.chyzyLogistics.block.gate.ProGateBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WorldGateContext extends GateContext {

    public final BlockPos position;
    public final World world;

    public BlockState state;

    protected WorldGateContext(World world, BlockPos position, GateStateStorage stateStorage, BlockState state) {
        super(stateStorage, state.get(Properties.HORIZONTAL_FACING));

        this.state = state;
        this.position = position;
        this.world = world;
    }

    public static WorldGateContext of(World world, BlockPos pos, GateStateStorage stateStorage) {
        return new WorldGateContext(world, pos, stateStorage, world.getBlockState(pos));
    }

    public int getEmittedRedstonePower(Direction direction) {
        var dirOpp = direction.getOpposite();

        return world.getEmittedRedstonePower(position.offset(dirOpp), dirOpp);
    }
}
