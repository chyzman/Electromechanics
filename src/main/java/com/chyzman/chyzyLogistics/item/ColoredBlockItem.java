package com.chyzman.chyzyLogistics.item;

import com.chyzman.chyzyLogistics.util.Colored;
import io.wispforest.owo.ui.core.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class ColoredBlockItem extends BlockItem implements ItemColorProvider, Colored {

    private final DyeColor dyeColor;

    public ColoredBlockItem(Block block, Settings settings) {
        super(block, settings);

        this.dyeColor = ((Colored) block).getColor();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        return Color.ofDye(this.dyeColor).argb();
    }


    @Override
    public DyeColor getColor() {
        return this.dyeColor;
    }
}
