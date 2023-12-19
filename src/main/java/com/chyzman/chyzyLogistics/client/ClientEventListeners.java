package com.chyzman.chyzyLogistics.client;

import com.chyzman.chyzyLogistics.registries.ChyzyLogisticsRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;

public class ClientEventListeners {
    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
            entries.addAfter((stack) -> stack.getItem().equals(Items.OBSERVER), ChyzyLogisticsRegistry.items, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
        });
    }
}