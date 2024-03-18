package com.chyzman.electromechanics.block.slime;

import com.chyzman.electromechanics.util.Colored;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.SlimeBlock;
import net.minecraft.util.DyeColor;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ColoredBlockProvider.class)
public class ColoredSlimeBlock extends SlimeBlock implements ColoredBlockProvider, Colored {

    private final DyeColor dyeColor;

    public ColoredSlimeBlock(DyeColor dyeColor, Settings settings) {
        super(settings.mapColor(dyeColor.getMapColor()));

        this.dyeColor = dyeColor;
    }

    @Override
    public DyeColor getColor() {
        return this.dyeColor;
    }
}
