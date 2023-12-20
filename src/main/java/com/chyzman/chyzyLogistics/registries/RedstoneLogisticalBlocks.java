package com.chyzman.chyzyLogistics.registries;

import com.chyzman.chyzyLogistics.block.ListenerBlock;
import com.chyzman.chyzyLogistics.block.detector.AdvancedDetectorBlock;
import com.chyzman.chyzyLogistics.block.detector.DetectorBlock;
import com.chyzman.chyzyLogistics.block.gate.BiGateBlock;
import com.chyzman.chyzyLogistics.block.gate.MonoGateBlock;
import com.google.common.collect.ImmutableList;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class RedstoneLogisticalBlocks implements BlockRegistryContainer {

    private static final List<Item> ITEMS = new ArrayList<>();

    public static final Block DETECTOR = new DetectorBlock(FabricBlockSettings.copy(Blocks.OBSERVER));

    public static final Block ADVANCED_DETECTOR =  new AdvancedDetectorBlock(FabricBlockSettings.copy(DETECTOR).pistonBehavior(PistonBehavior.BLOCK));

    public static final Block LISTENER = new ListenerBlock(FabricBlockSettings.copy(Blocks.OBSERVER));

    public static final Block GATE = new MonoGateBlock(aBoolean -> aBoolean, FabricBlockSettings.copy(Blocks.REPEATER));

    public static final Block NOT_GATE = new MonoGateBlock(aBoolean -> !aBoolean, FabricBlockSettings.copy(Blocks.REPEATER));

    public static final Block AND_GATE = new BiGateBlock((right, left) -> right && left,FabricBlockSettings.copy(Blocks.REPEATER));

    public static final Block OR_GATE = new BiGateBlock((right, left) -> right || left,FabricBlockSettings.copy(Blocks.REPEATER));

    public static final Block XOR_GATE = new BiGateBlock((right, left) -> right ^ left,FabricBlockSettings.copy(Blocks.REPEATER));

    public static final Block NAND_GATE = new BiGateBlock((right, left) -> !(right && left),FabricBlockSettings.copy(Blocks.REPEATER));

    public static final Block NOR_GATE = new BiGateBlock((right, left) -> !(right || left),FabricBlockSettings.copy(Blocks.REPEATER));

    public static final Block XNOR_GATE =  new BiGateBlock((right, left) -> right == left,FabricBlockSettings.copy(Blocks.REPEATER));

    @Override
    public BlockItem createBlockItem(Block block, String identifier) {
        var item = BlockRegistryContainer.super.createBlockItem(block, identifier);

        ITEMS.add(item);

        return item;
    }

    public static List<Item> getBlockItems(){
        return ImmutableList.copyOf(ITEMS);
    }
}