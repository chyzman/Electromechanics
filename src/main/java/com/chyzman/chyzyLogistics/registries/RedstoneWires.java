package com.chyzman.chyzyLogistics.registries;

import com.chyzman.chyzyLogistics.ChyzyLogistics;
import com.chyzman.chyzyLogistics.block.ColoredRedstoneWireBlock;
import com.chyzman.chyzyLogistics.block.slime.ColoredSlimeSlab;
import com.chyzman.chyzyLogistics.item.ColoredBlockItem;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RedstoneWires {

    private static final Map<String, Block> DUST_VARIANTS = new HashMap<>();

    private static final List<Item> DUSTS = new ArrayList<>();

    public static void init(){
        DUST_VARIANTS.put("redstone_wire", Blocks.REDSTONE_WIRE);

        for (DyeColor value : DyeColor.values()) {
            RegistryUtils.registerBlockAndItem(value.asString() + "_redstone_wire",
                    () -> new ColoredRedstoneWireBlock(value, FabricBlockSettings.copyOf(Blocks.REDSTONE_WIRE).mapColor(value)),
                    block -> {
                        var item = new ColoredBlockItem(block, new FabricItemSettings());

                        DUSTS.add(item);

                        return item;
                    });
        }

    }

    public static Map<String, Block> variantInfo(){
        return ImmutableMap.copyOf(DUST_VARIANTS);
    }

    public static List<Item> getDusts(){
        return ImmutableList.copyOf(DUSTS);
    }

    public static void iterateVariants(Consumer<Block> consumer){
        for (var entry : DUST_VARIANTS.entrySet()) {
            for(DyeColor dyeColor : DyeColor.values()){
                consumer.accept(getColoredVariant(dyeColor, entry.getKey()));
            }
        }
    }

    public static Block getColoredVariant(DyeColor color, String type){
        return Registries.BLOCK.get(new Identifier(ChyzyLogistics.MODID, color.asString() + "_" + type));
    }
}
