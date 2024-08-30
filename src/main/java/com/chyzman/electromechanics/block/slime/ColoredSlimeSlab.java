package com.chyzman.electromechanics.block.slime;

import com.chyzman.electromechanics.util.Colored;
import com.mojang.serialization.MapCodec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

public class ColoredSlimeSlab extends SlimeSlab implements Colored {

    public static final MapCodec<ColoredSlimeSlab> CODEC = CodecUtils.toMapCodec(
            StructEndecBuilder.of(
                    DYE_COLOR_ENDEC.fieldOf("dye_color", Colored::getColor),
                    CodecUtils.toEndec(AbstractBlock.Settings.CODEC).fieldOf("properties", AbstractBlock::getSettings),
                    ColoredSlimeSlab::new
            )
    );

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
