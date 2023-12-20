package com.chyzman.chyzyLogistics.block.slime;

import com.chyzman.chyzyLogistics.util.Colored;
import io.wispforest.owo.ui.core.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

@EnvironmentInterface(value = EnvType.CLIENT, itf = BlockColorProvider.class)
public class SlimeSlabColored extends SlimeSlab implements BlockColorProvider, Colored {

    public final DyeColor dyeColor;

    public SlimeSlabColored(DyeColor dyeColor, Settings settings) {
        super(settings.mapColor(dyeColor.getMapColor()));

        this.dyeColor = dyeColor;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        Color color = Color.ofDye(this.dyeColor);
        return color.argb();
    }

    @Override
    public DyeColor getColor() {
        return this.dyeColor;
    }
}
