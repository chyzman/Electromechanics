package com.chyzman.chyzyLogistics.registries;

import com.chyzman.chyzyLogistics.ChyzyLogistics;
import com.chyzman.chyzyLogistics.block.slime.ColoredSlimeBlock;
import com.chyzman.chyzyLogistics.block.slime.SlimeSlab;
import com.chyzman.chyzyLogistics.block.slime.ColoredSlimeSlab;
import com.chyzman.chyzyLogistics.data.SlimeTags;
import com.chyzman.chyzyLogistics.item.ColoredBlockItem;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.wispforest.owo.util.TagInjector;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlimeBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SlimeBlocks {

    private static final Map<String, Block> SLIME_VARIANTS = new HashMap<>();

    private static final List<Item> SLIME_SLABS = new ArrayList<>();
    private static final List<Item> SLIME_BLOCKS = new ArrayList<>();

    public static void init(){
        SLIME_VARIANTS.put("slime_block", Blocks.SLIME_BLOCK);

        var slimeSlab = RegistryUtils.registerBlockAndItem(
                "slime_slab",
                () -> new SlimeSlab(FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK)),
                block -> {
                    var item = new BlockItem(block, new FabricItemSettings());

                    SLIME_SLABS.add(item);

                    return item;
                });

        SLIME_VARIANTS.put("slime_slab", slimeSlab);

        for (DyeColor value : DyeColor.values()) {
            RegistryUtils.registerBlockAndItem(value.asString() + "_slime_slab",
                    () -> new ColoredSlimeSlab(value, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK).mapColor(value)),
                    block -> {
                        var item = new ColoredBlockItem(block, new FabricItemSettings());

                        SLIME_SLABS.add(item);

                        return item;
                    });

            RegistryUtils.registerBlockAndItem(value.asString() + "_slime_block",
                    () -> new ColoredSlimeBlock(value, FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK).mapColor(value)),
                    block -> {
                        var item = new ColoredBlockItem(block, new FabricItemSettings());

                        SLIME_BLOCKS.add(item);

                        return item;
                    });
        }

        iterateVariants(block -> {
            if(block instanceof SlimeBlock){
                TagInjector.inject(Registries.BLOCK, SlimeTags.Blocks.SLIME_BLOCKS.id(), block);
                TagInjector.inject(Registries.BLOCK, SlimeTags.Blocks.COLORED_SLIME_BLOCKS.id(), block);
            } else if (block instanceof SlimeSlab){
                TagInjector.inject(Registries.BLOCK, SlimeTags.Blocks.SLIME_SLABS.id(), block);
                TagInjector.inject(Registries.BLOCK, SlimeTags.Blocks.COLORED_SLIME_SLABS.id(), block);
            }

            TagInjector.inject(Registries.BLOCK, SlimeTags.Blocks.STICKY_BLOCKS.id(), block);
        });

        variantInfo().forEach((s, block) -> {
            if(block instanceof SlimeSlab) {
                TagInjector.inject(Registries.BLOCK, SlimeTags.Blocks.SLIME_SLABS.id(), block);
            }

            TagInjector.inject(Registries.BLOCK, SlimeTags.Blocks.STICKY_BLOCKS.id(), block);
        });

        TagInjector.inject(Registries.BLOCK, SlimeTags.Blocks.STICKY_BLOCKS.id(), Blocks.HONEY_BLOCK);
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
