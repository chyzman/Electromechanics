package com.chyzman.electromechanics;

import com.chyzman.electromechanics.block.detector.AdvancedDetectorBlockEntity;
import com.chyzman.electromechanics.block.detector.DetectorBlockEntity;
import com.chyzman.electromechanics.block.gate.GateBlock;
import com.chyzman.electromechanics.block.gate.GateBlockEntity;
import com.chyzman.electromechanics.block.redstone.RedstoneEvents;
import com.chyzman.electromechanics.registries.RedstoneLogisticalBlocks;
import com.chyzman.electromechanics.registries.RedstoneWires;
import com.chyzman.electromechanics.registries.SlimeBlocks;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.json.WrapperGroup;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class ElectromechanicsLogistics implements ModInitializer {

    public static final String MODID = "electromechanics";

    public static boolean bypassingAir = true;

    public static final BlockEntityType<DetectorBlockEntity> DETECTOR_BLOCK_ENTITY = net.minecraft.registry.Registry.register(Registries.BLOCK_ENTITY_TYPE, id("detector"), FabricBlockEntityTypeBuilder.create(DetectorBlockEntity::new, RedstoneLogisticalBlocks.DETECTOR).build());
    public static final BlockEntityType<AdvancedDetectorBlockEntity> ADVANCED_DETECTOR_BLOCK_ENTITY = net.minecraft.registry.Registry.register(Registries.BLOCK_ENTITY_TYPE, id("advanced_detector"), FabricBlockEntityTypeBuilder.create(AdvancedDetectorBlockEntity::new, RedstoneLogisticalBlocks.ADVANCED_DETECTOR).build());

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(RedstoneLogisticalBlocks.class, MODID, false);
        GateBlockEntity.getBlockEntityType();

        for (Item item : RedstoneLogisticalBlocks.getBlockItems()) {
            if(!(item instanceof BlockItem blockItem)) continue;

            var block = blockItem.getBlock();

            if(!(block instanceof GateBlock)) continue;
        }

        SlimeBlocks.init();
        RedstoneWires.init();

        ServerEventListeners.init();

        RedstoneEvents.SHOULD_CANCEl_CONNECTION.register((world, pos, state, pos2, state2) -> {
            var block = state.getBlock();
            var block2 = state2.getBlock();

            if(!(block instanceof RedstoneWireBlock) || !(block2 instanceof RedstoneWireBlock)) return false;

            DyeColor dyeColor = RedstoneWires.getDyeColor(block);
            DyeColor dyeColor2 = RedstoneWires.getDyeColor(block2);

            if(dyeColor == null || dyeColor2 == null) return false;

            return dyeColor != dyeColor2;
        });

        RegistryEntryAddedCallback.event(Registries.ITEM_GROUP).register((rawId, id, group) -> {
            if(id.equals(ItemGroups.REDSTONE.getValue()) && group instanceof WrapperGroup wrapperGroup){
                wrapperGroup.addCustomTab(
                        Icon.of(RedstoneLogisticalBlocks.ADVANCED_DETECTOR),
                        "advanced_redstone_blocks",
                        (context, entries) -> {
                            var blockItems = Registries.ITEM.stream().filter(item -> item instanceof BlockItem).map(item -> (BlockItem) item).toList();

                            var gates = new ArrayList<>(blockItems.stream().filter(item -> item.getBlock() instanceof AbstractRedstoneGateBlock)
                                    .map(Item::getDefaultStack)
                                    .toList()
                            );

                            var observers = new ArrayList<>(blockItems.stream().filter(item -> item.getBlock() instanceof ObserverBlock).map(ItemStack::new).toList());
                            for (int i = 0; i <= ((int)Math.ceil(gates.size() / 9.0) * 9) - gates.size(); i++) {
                                gates.add(ItemStack.EMPTY);
                            };
                            var items = new ArrayList<>(gates);
                            for (int i = 0; i <= ((int)Math.ceil(observers.size() / 9.0) * 9) - observers.size(); i++) {
                                observers.add(ItemStack.EMPTY);
                            };
                            items.addAll(observers);
                            entries.addAll(
                                    items,
                                    ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS
                            );
                        },
                        false
                );
                wrapperGroup.addCustomTab(
                        Icon.of(SlimeBlocks.getSlimeSlabs().get(0)),
                        "slime_block_variants",
                        (context, entries) -> {
                            entries.addAll(
                                    SlimeBlocks.getSlimeSlabs().stream().map(ItemStack::new).toList(),
                                    ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS
                            );

                            entries.add(Blocks.SLIME_BLOCK);

                            entries.addAll(
                                    SlimeBlocks.getSlimeBlocks().stream().map(ItemStack::new).toList(),
                                    ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS
                            );
                        },
                        false
                );

                wrapperGroup.addCustomTab(
                        Icon.of(Blocks.REDSTONE_WIRE),
                        "stone_wire_variants",
                        (context, entries) -> {
                            entries.add(Blocks.REDSTONE_WIRE);

                            entries.addAll(
                                    RedstoneWires.getDusts().stream().map(ItemStack::new).toList(),
                                    ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS
                            );
                        },
                        false
                );
            }
        });


    }

    public static Identifier id(String path){
        return new Identifier(MODID, path);
    }
}