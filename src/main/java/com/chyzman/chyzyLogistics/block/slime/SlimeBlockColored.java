package com.chyzman.chyzyLogistics.block.slime;

import com.chyzman.chyzyLogistics.util.Colored;
import io.wispforest.owo.ui.core.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlimeBlock;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

@EnvironmentInterface(value = EnvType.CLIENT, itf = BlockColorProvider.class)
public class SlimeBlockColored extends SlimeBlock implements BlockColorProvider, Colored {

    public final DyeColor dyeColor;

    public SlimeBlockColored(DyeColor dyeColor, Settings settings) {
        super(settings.mapColor(dyeColor.getMapColor()));

        this.dyeColor = dyeColor;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        return Color.ofDye(this.dyeColor).argb();
    }

    @Override
    public DyeColor getColor() {
        return this.dyeColor;
    }
}
