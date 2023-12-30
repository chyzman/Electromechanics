package com.chyzman.chyzyLogistics.logic.api;

import com.chyzman.chyzyLogistics.block.gate.GateStateStorage;
import com.chyzman.chyzyLogistics.logic.SidesHelper;
import net.minecraft.util.math.Direction;

public abstract class GateContext {

    private final SidesHelper sidesToDirections;

    private final GateStateStorage stateStorage;

    private final Direction facing;

    private boolean updateOutput = false;

    public GateContext(GateStateStorage stateStorage, Direction facing) {
        this.stateStorage = stateStorage;

        this.facing = facing;

        this.sidesToDirections = new SidesHelper(facing);
    }

    // --

    public final GateContext toggleUpdateOutput(boolean value){
        this.updateOutput = value;

        return this;
    }

    public final boolean updateOutput(){
        return this.updateOutput;
    }

    public final GateStateStorage storage(){
        return this.stateStorage;
    }

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
