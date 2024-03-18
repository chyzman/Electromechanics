package com.chyzman.electromechanics.block.slime;

import com.chyzman.electromechanics.util.Colored;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.BlockState;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ColoredBlockProvider.class)
public class ColoredSlimeSlab extends SlimeSlab implements ColoredBlockProvider, Colored {

    private final DyeColor dyeColor;

    public ColoredSlimeSlab(DyeColor dyeColor, Settings settings) {
        super(settings.mapColor(dyeColor.getMapColor()));

        this.dyeColor = dyeColor;
    }

    // From TranslucentBlock::isSideInvisible
    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.isOf(this) ? true : super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    public DyeColor getColor() {
        return this.dyeColor;
    }
}
