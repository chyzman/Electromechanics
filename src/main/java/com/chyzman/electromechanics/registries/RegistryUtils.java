package com.chyzman.electromechanics.registries;

import com.chyzman.electromechanics.Electromechanics;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.function.Function;
import java.util.function.Supplier;

public class RegistryUtils {

    //--

    public static Block registerColoredBlockAndItem(DyeColor color, String variant, Function<DyeColor, Block> blockFunc, Function<Block, BlockItem> blockItemFunc){
        var block = registerColoredBlock(color, variant, blockFunc);

        registerColoredBlockItem(color, variant, block, blockItemFunc);

        return block;
    }

    public static Block registerColoredBlock(DyeColor color, String variant, Function<DyeColor, Block> blockFunc){
        return registerBlock(color.asString() + "_" + variant, () -> blockFunc.apply(color));
    }

    public static BlockItem registerColoredBlockItem(DyeColor color, String variant, Block block, Function<Block, BlockItem> blockItemFunc){
        return registerBlockItem(color.asString() + "_" + variant, block, blockItemFunc);
    }

    //--

    public static Block registerBlockAndItem(String path, Supplier<Block> blockFunc, Function<Block, BlockItem> blockItemFunc){
        var block = registerBlock(path, blockFunc);

        registerBlockItem(path, block, blockItemFunc);

        return block;
    }

    public static Block registerBlock(String path, Supplier<Block> blockFunc){
        var block = blockFunc.get();

        return Registry.register(Registries.BLOCK, new Identifier(Electromechanics.MODID, path), block);
    }

    public static BlockItem registerBlockItem(String path, Block block, Function<Block, BlockItem> blockItemFunc){
        var item = blockItemFunc.apply(block);

        return Registry.register(Registries.ITEM, new Identifier(Electromechanics.MODID, path), item);
    }
}
