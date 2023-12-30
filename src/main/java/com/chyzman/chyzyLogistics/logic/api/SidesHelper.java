package com.chyzman.chyzyLogistics.logic.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.math.Direction;

public class SidesHelper {

    private final BiMap<Side, Direction> sidesToDirections;

    public SidesHelper(Direction facing){
        this.sidesToDirections = getOrientationData(facing);
    }

    public static BiMap<Side, Direction> getOrientationData(Direction facing) {
        BiMap<Side, Direction> sidesToDirections = HashBiMap.create(4);

        sidesToDirections.put(Side.FRONT, facing);

        var direction = facing;

        for (int i = 1; i < 4; i++) {
            direction = direction.rotateClockwise(Direction.Axis.Y);

            sidesToDirections.put(Side.values()[i], direction);
        }

        return sidesToDirections;
    }

    public final Side getSide(Direction direction) {
        return this.sidesToDirections.inverse().get(direction);
    }

    public final Direction getDirection(Side side) {
        return this.sidesToDirections.get(side);
    }
}
