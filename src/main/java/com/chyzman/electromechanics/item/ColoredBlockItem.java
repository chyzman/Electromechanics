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

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class ColoredBlockItem extends BlockItem implements ItemColorProvider, Colored {

    private Float adjustment = null;

    private final DyeColor dyeColor;

    public ColoredBlockItem(DyeColor color, Block block, Settings settings) {
        super(block, settings);

        this.dyeColor = color;
    }

    public ColoredBlockItem(Block block, Settings settings) {
        super(block, settings);

        this.dyeColor = ((Colored) block).getColor();
    }

    public ColoredBlockItem adjustment(float adjustment){
        this.adjustment = adjustment;

        return this;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        Color color = Color.ofDye(this.dyeColor);

        if(adjustment != null) {
            color = color.interpolate(Color.WHITE, adjustment);
        }

        return color.argb();
    }

    @Override
    public DyeColor getColor() {
        return this.dyeColor;
    }
}
