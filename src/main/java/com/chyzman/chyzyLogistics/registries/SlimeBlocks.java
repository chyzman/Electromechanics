package com.chyzman.chyzyLogistics.registries;

import com.chyzman.chyzyLogistics.ChyzyLogistics;
import com.chyzman.chyzyLogistics.block.slime.SlimeBlockColored;
import com.chyzman.chyzyLogistics.block.slime.SlimeSlab;
import com.chyzman.chyzyLogistics.block.slime.SlimeSlabColored;
import com.chyzman.chyzyLogistics.item.ColoredBlockItem;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SlimeBlocks {

    private static final Map<String, Block> SLIME_VARIANTS = new HashMap<>();

    private static final List<Item> SLIME_SLABS = new ArrayList<>();
    private static final List<Item> SLIME_BLOCKS = new ArrayList<>();

    public static void init(){
        SLIME_VARIANTS.put("slime_block", Blocks.SLIME_BLOCK);

        var slimeSlab = registerBlockAndItem(
                "slime_slab",
                () -> new SlimeSlab(FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK)),
                block -> {
                    var item = new BlockItem(block, new FabricItemSettings());

                    SLIME_SLABS.add(item);

                    return item;
                });

        SLIME_VARIANTS.put("slime_slab", slimeSlab);

        for (DyeColor value : DyeColor.values()) {
            registerBlockAndItem(value.asString() + "_slime_slab",
                    () -> new SlimeSlabColored(value, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK).mapColor(value)),
                    block -> {
                        var item = new ColoredBlockItem(block, new FabricItemSettings());

                        SLIME_SLABS.add(item);

                        return item;
                    });

            registerBlockAndItem(value.asString() + "_slime_block",
                    () -> new SlimeBlockColored(value, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK).mapColor(value)),
                    block -> {
                        var item = new ColoredBlockItem(block, new FabricItemSettings());

                        SLIME_BLOCKS.add(item);

                        return item;
                    });
        }
    }

    //--

    private static Block registerColoredBlockAndItem(DyeColor color, String variant, Function<DyeColor, Block> blockFunc, Function<Block, BlockItem> blockItemFunc){
        var block = registerColoredBlock(color, variant, blockFunc);

        registerColoredBlockItem(color, variant, block, blockItemFunc);

        return block;
    }

    private static Block registerColoredBlock(DyeColor color, String variant, Function<DyeColor, Block> blockFunc){
        return registerBlock(color.asString() + "_" + variant, () -> blockFunc.apply(color));
    }

    private static BlockItem registerColoredBlockItem(DyeColor color, String variant, Block block, Function<Block, BlockItem> blockItemFunc){
        return registerBlockItem(color.asString() + "_" + variant, block, blockItemFunc);
    }

    //--

    private static Block registerBlockAndItem(String path, Supplier<Block> blockFunc, Function<Block, BlockItem> blockItemFunc){
        var block = registerBlock(path, blockFunc);

        registerBlockItem(path, block, blockItemFunc);

        return block;
    }

    private static Block registerBlock(String path, Supplier<Block> blockFunc){
        var block = blockFunc.get();

        return Registry.register(Registries.BLOCK, new Identifier(ChyzyLogistics.MODID, path), block);
    }

    private static BlockItem registerBlockItem(String path, Block block, Function<Block, BlockItem> blockItemFunc){
        var item = blockItemFunc.apply(block);

        return Registry.register(Registries.ITEM, new Identifier(ChyzyLogistics.MODID, path), item);
    }

    //--

    public static Map<String, Block> variantInfo(){
        return ImmutableMap.copyOf(SLIME_VARIANTS);
    }

    public static List<Item> getSlimeSlabs(){
        return ImmutableList.copyOf(SLIME_SLABS);
    }

    public static List<Item> getSlimeBlocks(){
        return ImmutableList.copyOf(SLIME_BLOCKS);
    }

    //--

    public static void iterateVariants(Consumer<Block> consumer){
        for (var entry : SLIME_VARIANTS.entrySet()) {
            for(DyeColor dyeColor : DyeColor.values()){
                consumer.accept(getColoredVariant(dyeColor, entry.getKey()));
            }
        }
    }

    public static Block getColoredVariant(DyeColor color, String type){
        return Registries.BLOCK.get(new Identifier(ChyzyLogistics.MODID, color.asString() + "_" + type));
    }

    public static boolean isVariant(Identifier id, String variant){
        if(!id.getNamespace().equals(ChyzyLogistics.MODID)) return false;

        var path = id.getPath();

        if(!path.contains(variant)) return false;

        String[] parts = id.getPath().split(variant);

        if(parts.length > 1) return false;

        return true;
    }

}
