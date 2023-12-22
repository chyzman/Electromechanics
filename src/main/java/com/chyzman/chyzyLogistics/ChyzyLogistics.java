package com.chyzman.chyzyLogistics;

import com.chyzman.chyzyLogistics.block.detector.AdvancedDetectorBlockEntity;
import com.chyzman.chyzyLogistics.block.detector.DetectorBlockEntity;
import com.chyzman.chyzyLogistics.registries.RedstoneLogisticalBlocks;
import com.chyzman.chyzyLogistics.registries.RedstoneWires;
import com.chyzman.chyzyLogistics.registries.SlimeBlocks;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.json.OwoItemGroupLoader;
import io.wispforest.owo.itemgroup.json.WrapperGroup;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

import static com.chyzman.chyzyLogistics.util.ChyzyLogisticsRegistryHelper.id;

public class ChyzyLogistics implements ModInitializer {
    public static final String MODID = "chyzylogistics";

    public static final BlockEntityType<DetectorBlockEntity> DETECTOR_BLOCK_ENTITY = net.minecraft.registry.Registry.register(Registries.BLOCK_ENTITY_TYPE, id("detector"), FabricBlockEntityTypeBuilder.create(DetectorBlockEntity::new, RedstoneLogisticalBlocks.DETECTOR).build());
    public static final BlockEntityType<AdvancedDetectorBlockEntity> ADVANCED_DETECTOR_BLOCK_ENTITY = net.minecraft.registry.Registry.register(Registries.BLOCK_ENTITY_TYPE, id("advanced_detector"), FabricBlockEntityTypeBuilder.create(AdvancedDetectorBlockEntity::new, RedstoneLogisticalBlocks.ADVANCED_DETECTOR).build());

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(RedstoneLogisticalBlocks.class, MODID, false);

        SlimeBlocks.init();
        RedstoneWires.init();

        ServerEventListeners.init();

        RegistryEntryAddedCallback.event(Registries.ITEM_GROUP).register((rawId, id, group) -> {
            if(id.equals(ItemGroups.REDSTONE.getValue()) && group instanceof WrapperGroup wrapperGroup){
                wrapperGroup.addCustomTab(
                        Icon.of(RedstoneLogisticalBlocks.ADVANCED_DETECTOR),
                        "advanced_redstone_blocks",
                        (context, entries) -> {
                            entries.addAll(
                                    RedstoneLogisticalBlocks.getBlockItems().stream().map(ItemStack::new).toList(),
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
}