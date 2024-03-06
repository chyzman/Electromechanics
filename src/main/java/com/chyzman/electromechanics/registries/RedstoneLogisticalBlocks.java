package com.chyzman.electromechanics.registries;

import com.chyzman.electromechanics.block.ListenerBlock;
import com.chyzman.electromechanics.block.SternCopperBlock;
import com.chyzman.electromechanics.block.detector.AdvancedDetectorBlock;
import com.chyzman.electromechanics.block.detector.DetectorBlock;
import com.chyzman.electromechanics.block.gate.*;
import com.chyzman.electromechanics.item.GateBlockItem;
import com.chyzman.electromechanics.logic.DigitalGateHandlers;
import com.chyzman.electromechanics.logic.AnalogGateHandlers;
import com.chyzman.electromechanics.logic.DirectionGateHandlers;
import com.chyzman.electromechanics.logic.api.TimerGateHandlers;
import com.google.common.collect.ImmutableList;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class RedstoneLogisticalBlocks implements BlockRegistryContainer {

    private static final List<Item> ITEMS = new ArrayList<>();

    public static final Block DETECTOR = new DetectorBlock(FabricBlockSettings.copy(Blocks.OBSERVER));

    public static final Block ADVANCED_DETECTOR =  new AdvancedDetectorBlock(FabricBlockSettings.copy(DETECTOR).pistonBehavior(PistonBehavior.BLOCK));

    public static final Block LISTENER = new ListenerBlock(FabricBlockSettings.copy(Blocks.OBSERVER));

    public static final Block GATE = new GateBlock(DigitalGateHandlers.REPEATER);

    public static final Block AND_GATE = new GateBlock(DigitalGateHandlers.AND);

    public static final Block OR_GATE = new GateBlock(DigitalGateHandlers.OR);

    public static final Block XOR_GATE = new GateBlock(DigitalGateHandlers.XOR);

    public static final Block TIMER = new GateBlock(TimerGateHandlers.TIMER);

    public static final Block ADVANCED_TIMER = new GateBlock(TimerGateHandlers.ADVANCED_TIMER);

    //--

    public static final Block TRI_AND_GATE = new GateBlock(DigitalGateHandlers.TRIPLE_AND);

    public static final Block TRI_OR_GATE = new GateBlock(DigitalGateHandlers.TRIPLE_OR);

    public static final Block AND_THEN_OR_GATE = new GateBlock(DigitalGateHandlers.AND_THEN_OR);

    public static final Block OR_THEN_AND_GATE = new GateBlock(DigitalGateHandlers.OR_THEN_AND);

    public static final Block CROSS_GATE = new GateBlock(DirectionGateHandlers.CROSS);

    public static final Block DIRECTABLE_GATE = new GateBlock(DirectionGateHandlers.DIRECTABLE);

    //--

    public static final Block ANALOG_GATE = new GateBlock(AnalogGateHandlers.GATE);

    public static final Block ADDITION_GATE = new GateBlock(AnalogGateHandlers.ADDITION);

    public static final Block SUBTRACTION_GATE = new GateBlock(AnalogGateHandlers.SUBTRACTION);

    public static final Block MULTIPLICATION_GATE = new GateBlock(AnalogGateHandlers.MULTIPLICATION);

    public static final Block DIVISION_GATE = new GateBlock(AnalogGateHandlers.DIVISION);

    public static final Block MODULUS_GATE = new GateBlock(AnalogGateHandlers.MODULUS);

    public static final Block COUNTER_GATE = new GateBlock(AnalogGateHandlers.COUNTER);

    //--

    public static final Block T_FLIP_FLOP = new GateBlock(DigitalGateHandlers.T_FLIP_FLOP);

    //public static final Block BOARD = new BoardBlock(FabricBlockSettings.copy(Blocks.REPEATER));

    public static final Block STERN_COPPER = new SternCopperBlock(FabricBlockSettings.copy(Blocks.COPPER_BLOCK));

    public static final BlockSetType OBSERVER_BLOCK_SET_TYPE = BlockSetType.register(
            new BlockSetType(
                    "observer",
                    true,
                    true,
                    false,
                    BlockSetType.ActivationRule.MOBS,
                    BlockSoundGroup.STONE,
                    SoundEvents.BLOCK_IRON_DOOR_CLOSE,
                    SoundEvents.BLOCK_IRON_DOOR_OPEN,
                    SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE,
                    SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN,
                    SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF,
                    SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON,
                    SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF,
                    SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON
            )
    );

    public static final Block OBSERVER_BUTTON = new ButtonBlock(OBSERVER_BLOCK_SET_TYPE, 2, FabricBlockSettings.copy(Blocks.OBSERVER).noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY));

    @Override
    public BlockItem createBlockItem(Block block, String identifier) {
        BlockItem item;

        if(block instanceof GateBlock){
            item = new GateBlockItem(block, new Item.Settings());
        } else {
            item = BlockRegistryContainer.super.createBlockItem(block, identifier);
        }

        ITEMS.add(item);

        return item;
    }

    public static List<Item> getBlockItems(){
        return ImmutableList.copyOf(ITEMS);
    }
}