package com.chyzman.electromechanics.util;

import io.wispforest.endec.Endec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.util.DyeColor;

public interface Colored {

    @Environment(EnvType.CLIENT) ItemColorProvider ITEM_COLOR_PROVIDER = (stack, tintIndex) -> stack.getItem() instanceof Colored colored ? colored.getArgbColor() : -1;
    @Environment(EnvType.CLIENT) BlockColorProvider BLOCK_COLOR_PROVIDER = (state, world, pos, tintIndex) -> state.getBlock() instanceof Colored colored ? colored.getArgbColor() : -1;

    Endec<DyeColor> DYE_COLOR_ENDEC = Endec.forEnum(DyeColor.class);

    DyeColor getColor();

    default int getArgbColor() {
        return getColor().getEntityColor();
    }
}
