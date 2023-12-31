package com.chyzman.electromechanics.block.slime;

import com.chyzman.electromechanics.util.Colored;
import com.mojang.serialization.MapCodec;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.SlimeBlock;
import net.minecraft.util.DyeColor;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ColoredBlockProvider.class)
public class ColoredSlimeBlock extends SlimeBlock implements ColoredBlockProvider, Colored {

    public static final MapCodec<ColoredSlimeBlock> CODEC = StructEndecBuilder.of(
            DYE_COLOR_ENDEC.fieldOf("dye_color", Colored::getColor),
            Endec.ofCodec(AbstractBlock.Settings.CODEC).fieldOf("properties", AbstractBlock::getSettings),
            ColoredSlimeBlock::new
    ).mapCodec();

    private final DyeColor dyeColor;

    public ColoredSlimeBlock(DyeColor dyeColor, Settings settings) {
        super(settings.mapColor(dyeColor.getMapColor()));

        this.dyeColor = dyeColor;
    }

    // Thanks mojang for this as it is now fucked
    @Override
    public MapCodec<SlimeBlock> getCodec() {
        return CODEC.xmap(slimeBlockColored -> slimeBlockColored, slimeBlock -> {
            if(slimeBlock instanceof ColoredSlimeBlock slimeBlockColored) return slimeBlockColored;

            throw new IllegalStateException("Codec passed incorrect type due to mojank moment leading to type restriction issue! Check SlimeBlockColored::getCodec()");
        });
    }

    @Override
    public DyeColor getColor() {
        return this.dyeColor;
    }
}
