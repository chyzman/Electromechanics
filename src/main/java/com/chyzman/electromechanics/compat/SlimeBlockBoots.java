package com.chyzman.electromechanics.compat;

import com.chyzman.electromechanics.Electromechanics;
import com.mojang.serialization.MapCodec;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.slot.SlotType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SlimeBlockBoots implements Accessory {

    public static final TagKey<Item> SLIME_BLOCK_TAG = TagKey.of(RegistryKeys.ITEM, Electromechanics.id("slime_blocks"));

    public static final SlimeBlockBoots INSTANCE = new SlimeBlockBoots();

    @Override
    public void getAttributesTooltip(ItemStack stack, SlotType type, List<Text> tooltips, Item.TooltipContext tooltipContext, TooltipType tooltipType) {
        if(stack.getCount() >= 2) tooltips.add(Text.translatable("accessories.attribute.slime_hops").formatted(Formatting.BLUE));
    }

    @Override
    public void getExtraTooltip(ItemStack stack, List<Text> tooltips, Item.TooltipContext tooltipContext, TooltipType tooltipType) {
        var description = wrapDescription(Text.translatable("accessories.description." + (stack.getCount() >= 2 ? "active" : "inactive") +".slime_hops").formatted(Formatting.GRAY));

        description.add(Text.empty());

        tooltips.addAll(0, description);
    }

    @Override
    public int maxStackSize(ItemStack stack) {
        return 2;
    }

    /**
     * Code below is taken from: <a href="https://github.com/gliscowo/idwtialsimmoedm/blob/50a50fb77ae27769c12b48d05b5b8926d36e4d80/src/main/java/io/wispforest/idwtialsimmoedm/api/GatherDescriptionCallback.java#L38">GatherDescriptionCallback.wrapDescription</a> and <a href="https://github.com/gliscowo/idwtialsimmoedm/blob/50a50fb77ae27769c12b48d05b5b8926d36e4d80/src/main/java/io/wispforest/idwtialsimmoedm/IdwtialsimmoedmClient.java#L138">IdwtialsimmoedmClient.VisitableTextContent</a>
     * and follows such licenses.
     */
    @Environment(EnvType.CLIENT)
    private static List<Text> wrapDescription(Text description) {
        var lines = MinecraftClient.getInstance().textRenderer.getTextHandler()
                .wrapLines(description, 300, Style.EMPTY.withColor(Formatting.DARK_GRAY))
                .stream()
                .map(VisitableTextContent::new)
                .map(MutableText::of).toList();

        var output = new ArrayList<Text>();

        for (var line : lines) output.addFirst(Text.empty().formatted(Formatting.GRAY).append(line));

        return output.reversed();
    }

    @Environment(EnvType.CLIENT)
    public record VisitableTextContent(StringVisitable content) implements TextContent {
        private static final Type<VisitableTextContent> DUMMY_TYPE = new Type<>(MapCodec.unit(new VisitableTextContent(StringVisitable.EMPTY)), "idwtialsimmoedm:visitable_text");

        @Override public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) { return content.visit(visitor, style); }
        @Override public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) { return content.visit(visitor); }
        @Override public Type<?> getType() { return DUMMY_TYPE; }
    }
}
