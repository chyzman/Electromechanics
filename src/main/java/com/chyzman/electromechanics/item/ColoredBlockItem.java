package com.chyzman.electromechanics.item;

import com.chyzman.electromechanics.util.Colored;
import io.wispforest.owo.ui.core.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

public class ColoredBlockItem extends BlockItem implements Colored {

    private final DyeColor dyeColor;

    public ColoredBlockItem(DyeColor color, Block block, Settings settings) {
        super(block, settings);

        this.dyeColor = color;
    }

    public ColoredBlockItem(Block block, Settings settings) {
        super(block, settings);

        this.dyeColor = ((Colored) block).getColor();
    }

    @Override
    public DyeColor getColor() {
        return this.dyeColor;
    }
}
