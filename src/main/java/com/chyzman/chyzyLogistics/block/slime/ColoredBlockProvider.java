package com.chyzman.chyzyLogistics.block.slime;

import com.chyzman.chyzyLogistics.util.Colored;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

public interface ColoredBlockProvider extends BlockColorProvider, Colored {
    @Override
    default int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex){
        return Color.ofDye(this.getColor()).argb();
    }
}
