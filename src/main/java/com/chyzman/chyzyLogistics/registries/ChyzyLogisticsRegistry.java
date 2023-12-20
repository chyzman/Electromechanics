package com.chyzman.chyzyLogistics.registries;

import com.chyzman.chyzyLogistics.block.ListenerBlock;
import com.chyzman.chyzyLogistics.block.detector.AdvancedDetectorBlock;
import com.chyzman.chyzyLogistics.block.detector.DetectorBlock;
import com.chyzman.chyzyLogistics.block.gate.BiGateBlock;
import com.chyzman.chyzyLogistics.block.gate.MonoGateBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;

import static com.chyzman.chyzyLogistics.util.ChyzyLogisticsRegistryHelper.id;

public class ChyzyLogisticsRegistry {

    public static final List<ItemStack> items = new ArrayList<>();

    private static Item registerItem(String name, Item item) {
        items.add(new ItemStack(item));
        return Registry.register(Registries.ITEM, id(name), item);
    }

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, id(name), block);
    }

    private static Block registerBlockAndItem(String name, Block block) {
        var aBlock = registerBlock(name, block);
        registerItem(name, new BlockItem(aBlock, new Item.Settings()));
        return aBlock;
    }

    public static final Block DETECTOR_BLOCK = registerBlockAndItem("detector", new DetectorBlock(FabricBlockSettings.copy(Blocks.OBSERVER)));

    public static final Block ADVANCED_DETECTOR_BLOCK = registerBlockAndItem("advanced_detector", new AdvancedDetectorBlock(FabricBlockSettings.copy(DETECTOR_BLOCK).pistonBehavior(PistonBehavior.BLOCK)));

    public static final Block LISTENER_BLOCK = registerBlockAndItem("listener", new ListenerBlock(FabricBlockSettings.copy(Blocks.OBSERVER)));

    public static final Block GATE = registerBlockAndItem("gate", new MonoGateBlock(aBoolean -> aBoolean, FabricBlockSettings.copy(Blocks.REPEATER)));

    public static final Block NOT_GATE = registerBlockAndItem("not_gate", new MonoGateBlock(aBoolean -> !aBoolean, FabricBlockSettings.copy(Blocks.REPEATER)));

    public static final Block AND_GATE = registerBlockAndItem("and_gate", new BiGateBlock((right, left) -> right && left,FabricBlockSettings.copy(Blocks.REPEATER)));

    public static final Block OR_GATE = registerBlockAndItem("or_gate", new BiGateBlock((right, left) -> right || left,FabricBlockSettings.copy(Blocks.REPEATER)));

    public static final Block XOR_GATE = registerBlockAndItem("xor_gate", new BiGateBlock((right, left) -> right ^ left,FabricBlockSettings.copy(Blocks.REPEATER)));

    public static final Block NAND_GATE = registerBlockAndItem("nand_gate", new BiGateBlock((right, left) -> !(right && left),FabricBlockSettings.copy(Blocks.REPEATER)));

    public static final Block NOR_GATE = registerBlockAndItem("nor_gate", new BiGateBlock((right, left) -> !(right || left),FabricBlockSettings.copy(Blocks.REPEATER)));

    public static final Block XNOR_GATE = registerBlockAndItem("xnor_gate", new BiGateBlock((right, left) -> right == left,FabricBlockSettings.copy(Blocks.REPEATER)));

    public static void init() {
    }
}