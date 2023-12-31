package com.chyzman.electromechanics.item;

import com.chyzman.electromechanics.util.Colored;
import io.wispforest.owo.ui.core.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

import java.util.function.IntPredicate;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ItemColorProvider.class)
public class ColoredItem extends Item implements ItemColorProvider, Colored {

    private final DyeColor dyeColor;
    private final IntPredicate tintAbove;

    public ColoredItem(DyeColor dyeColor, Settings settings, IntPredicate tintAbove) {
        super(settings);

        this.tintAbove = tintAbove;
        this.dyeColor = dyeColor;
    }

    public ColoredItem(DyeColor dyeColor, Settings settings) {
        this(dyeColor, settings, (number) -> true);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        return tintAbove.test(tintIndex) ? Color.ofDye(this.dyeColor).argb() : -1;
    }

    @Override
    public DyeColor getColor() {
        return this.dyeColor;
    }
}
