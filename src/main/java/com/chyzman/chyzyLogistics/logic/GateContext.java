package com.chyzman.chyzyLogistics.logic;

import com.chyzman.chyzyLogistics.block.gate.GateStateStorage;
import net.minecraft.util.math.Direction;

public abstract class GateContext {

    private final SidesHelper sidesToDirections;

    public final GateStateStorage stateStorage;

    public final Direction facing;

    public GateContext(GateStateStorage stateStorage, Direction facing) {
        this.stateStorage = stateStorage;

        this.facing = facing;

        this.sidesToDirections = new SidesHelper(facing);
    }

    // --

    public final Direction getFacing() {
        return this.facing;
    }

    public final Side getSide(Direction direction) {
        return this.sidesToDirections.getSide(direction);
    }

    public final Direction getDirection(Side side) {
        return this.sidesToDirections.getDirection(side);
    }

    public final int getEmittedRedstonePower(Side side) {
        return this.getEmittedRedstonePower(getDirection(side));
    }

    // --

    public abstract int getEmittedRedstonePower(Direction direction);

}
