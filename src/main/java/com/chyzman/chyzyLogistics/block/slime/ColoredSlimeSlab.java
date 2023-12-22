package com.chyzman.chyzyLogistics.block.slime;

import com.chyzman.chyzyLogistics.util.Colored;
import com.mojang.serialization.MapCodec;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

@EnvironmentInterface(value = EnvType.CLIENT, itf = BlockColorProvider.class)
public class ColoredSlimeSlab extends SlimeSlab implements ColoredBlockProvider, Colored {

    public static final MapCodec<ColoredSlimeSlab> CODEC = StructEndecBuilder.of(
            DYE_COLOR_ENDEC.fieldOf("dye_color", Colored::getColor),
            Endec.ofCodec(AbstractBlock.Settings.CODEC).fieldOf("properties", AbstractBlock::getSettings),
            ColoredSlimeSlab::new
    ).mapCodec();

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
    public MapCodec<? extends SlimeSlab> getCodec() {
        return CODEC;
    }

    @Override
    public DyeColor getColor() {
        return this.dyeColor;
    }
}
