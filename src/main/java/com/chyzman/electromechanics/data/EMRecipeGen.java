package com.chyzman.electromechanics.data;

import com.chyzman.electromechanics.Electromechanics;
import com.chyzman.electromechanics.registries.RedstoneLogisticalBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class EMRecipeGen extends FabricRecipeProvider {

    public EMRecipeGen(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        for (var color : DyeColor.values()) {
            var dye_item = Registries.ITEM.get(new Identifier(color + "_dye"));

            {
                var colored_wire = Registries.ITEM.get(Electromechanics.id(color.asString() + "_redstone_wire"));

                var otherWires = getColoredVariants("{0}_redstone_wire", color);

                otherWires.add(Items.REDSTONE);

                var wireArray = otherWires.toArray(ItemConvertible[]::new);

                ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, colored_wire, 8)
                        .input('X', dye_item)
                        .input('#', Ingredient.ofItems(wireArray))
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .criterion("has_dye", RecipeProvider.conditionsFromItem(dye_item))
                        .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                        .offerTo(exporter, MessageFormat.format("dye_stone_wire_{0}", color));
            }

            {
                var colored_slime_slab = Registries.ITEM.get(Electromechanics.id(color.asString() + "_slime_slab"));

                var otherSlabs = getColoredVariants("{0}_slime_slab", color);

                otherSlabs.add(Registries.ITEM.get(Electromechanics.id("slime_slab")));

                var slabArray = otherSlabs.toArray(ItemConvertible[]::new);

                ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, colored_slime_slab, 8)
                        .input('X', dye_item)
                        .input('#', Ingredient.ofItems(slabArray))
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .criterion("has_dye", RecipeProvider.conditionsFromItem(dye_item))
                        .criterion("has_slime_slabs", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(slabArray).build()))
                        .offerTo(exporter, MessageFormat.format("dye_slime_slab_{0}", color));
            }

            {
                var colored_slime_block = Registries.ITEM.get(Electromechanics.id(color.asString() + "_slime_block"));

                var otherBlocks = getColoredVariants("{0}_slime_block", color);

                otherBlocks.add(Registries.ITEM.get(new Identifier("slime_block")));

                var blockArray = otherBlocks.toArray(ItemConvertible[]::new);

                ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, colored_slime_block, 8)
                        .input('X', dye_item)
                        .input('#', Ingredient.ofItems(blockArray))
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .criterion("has_dye", RecipeProvider.conditionsFromItem(dye_item))
                        .criterion("has_slime_blocks", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(blockArray).build()))
                        .offerTo(exporter, MessageFormat.format("dye_slime_block_{0}", color));
            }
        }

        var stoneWire = getColoredVariants("{0}_redstone_wire");

        stoneWire.add(Items.REDSTONE);

        var wireArray = stoneWire.toArray(ItemConvertible[]::new);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.GATE)
                .input('R', Ingredient.ofItems(wireArray))
                .input('S', Items.STONE)
                .pattern("   ")
                .pattern("RRR")
                .pattern("SSS")
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "gate");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.AND_GATE)
                .input('R', Ingredient.ofItems(wireArray))
                .input('T', Items.REDSTONE_TORCH)
                .input('S', Items.STONE)
                .input('L', Items.LEVER)
                .pattern(" R ")
                .pattern("TRT")
                .pattern("LSL")
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "and_gate");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.OR_GATE)
                .input('R', Ingredient.ofItems(wireArray))
                .input('S', Items.STONE)
                .input('L', Items.LEVER)
                .pattern(" R ")
                .pattern(" R ")
                .pattern("LSL")
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "or_gate");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.XOR_GATE)
                .input('R', Ingredient.ofItems(wireArray))
                .input('C', Items.COMPARATOR)
                .input('S', Items.STONE)
                .input('L', Items.LEVER)
                .pattern(" R ")
                .pattern("CRC")
                .pattern("LSL")
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "xor_gate");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.TIMER)
                .input('R', Ingredient.ofItems(wireArray))
                .input('S', Items.STONE)
                .input('T', Items.REPEATER)
                .pattern("RTR")
                .pattern("RTR")
                .pattern("SSS")
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "timer");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.ADVANCED_TIMER)
                .input('R', Ingredient.ofItems(wireArray))
                .input('T', RedstoneLogisticalBlocks.TIMER)
                .input('L', Items.LEVER)
                .pattern(" R ")
                .pattern("LTL")
                .pattern(" L ")
                .criterion("has_timer", RecipeProvider.conditionsFromItem(RedstoneLogisticalBlocks.TIMER))
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "advanced_timer");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.DIRECTABLE_GATE)
                .input('R', Ingredient.ofItems(wireArray))
                .input('G', RedstoneLogisticalBlocks.GATE)
                .input('P', Items.STICKY_PISTON)
                .input('L', Items.LEVER)
                .pattern(" R ")
                .pattern(" G ")
                .pattern("LPL")
                .criterion("has_gate", RecipeProvider.conditionsFromItem(RedstoneLogisticalBlocks.GATE))
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "directable_gate");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.CROSS_GATE)
                .input('R', Ingredient.ofItems(wireArray))
                .input('G', RedstoneLogisticalBlocks.GATE)
                .input('P', Items.STICKY_PISTON)
                .input('L', Items.LEVER)
                .pattern("LPL")
                .pattern("RGR")
                .pattern("LPL")
                .criterion("has_gate", RecipeProvider.conditionsFromItem(RedstoneLogisticalBlocks.GATE))
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "cross_gate");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.ANALOG_GATE)
                .input('R', Ingredient.ofItems(wireArray))
                .input('S', Items.STONE)
                .input('Q', Items.QUARTZ)
                .pattern("   ")
                .pattern("RQR")
                .pattern("SSS")
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "analog_gate");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.ADDITION_GATE)
                .input('R', Ingredient.ofItems(wireArray))
                .input('C', Items.COMPARATOR)
                .input('S', Items.STONE)
                .input('Q', Items.QUARTZ)
                .pattern(" R ")
                .pattern("RQR")
                .pattern("CSC")
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "addition_gate");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.SUBTRACTION_GATE)
                .input('R', Ingredient.ofItems(wireArray))
                .input('C', Items.COMPARATOR)
                .input('S', Items.STONE)
                .input('Q', Items.QUARTZ)
                .pattern(" C ")
                .pattern("RQR")
                .pattern("CSC")
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "subtraction_gate");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.MULTIPLICATION_GATE)
                .input('R', Ingredient.ofItems(wireArray))
                .input('C', Items.COMPARATOR)
                .input('S', Items.STONE)
                .input('A', Items.AMETHYST_SHARD)
                .pattern(" R ")
                .pattern("RAR")
                .pattern("CSC")
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "multiplication_gate");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.DIVISION_GATE)
                .input('R', Ingredient.ofItems(wireArray))
                .input('C', Items.COMPARATOR)
                .input('S', Items.STONE)
                .input('A', Items.AMETHYST_SHARD)
                .pattern(" C ")
                .pattern("RAR")
                .pattern("CSC")
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "division_gate");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.MODULUS_GATE)
                .input('R', Ingredient.ofItems(wireArray))
                .input('D', RedstoneLogisticalBlocks.DIVISION_GATE)
                .input('M', RedstoneLogisticalBlocks.MULTIPLICATION_GATE)
                .input('T', RedstoneLogisticalBlocks.SUBTRACTION_GATE)
                .input('S', Items.STONE)
                .pattern(" R ")
                .pattern("RTR")
                .pattern("DSM")
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "modulus_gate");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.T_FLIP_FLOP)
                .input('R', Ingredient.ofItems(wireArray))
                .input('S', Items.STONE)
                .input('L', Items.LEVER)
                .pattern("   ")
                .pattern("RLR")
                .pattern("SSS")
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "t_flip_flop_gate");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.COUNTER_GATE)
                .input('R', Ingredient.ofItems(wireArray))
                .input('S', Items.STONE)
                .input('H', Items.HOPPER)
                .input('C', Items.COMPARATOR)
                .pattern(" H ")
                .pattern("RHC")
                .pattern("SSS")
                .criterion("has_redstone_wires", RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(wireArray).build()))
                .offerTo(exporter, "counter_gate");

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Registries.ITEM.get(Electromechanics.id("slime_slab")))
                .input('S', Items.SLIME_BLOCK)
                .pattern("SSS")
                .criterion("has_slime_block", RecipeProvider.conditionsFromItem(Items.SLIME_BLOCK))
                .offerTo(exporter, "slime_slab");

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, RedstoneLogisticalBlocks.STERN_COPPER)
                .input('C', Items.COPPER_BLOCK)
                .input('P', Items.PISTON)
                .input('S', Items.NETHER_STAR)
                .pattern("CPC")
                .pattern("PSP")
                .pattern("CPC")
                .criterion("has_star", RecipeProvider.conditionsFromItem(Items.NETHER_STAR))
                .criterion("has_copper", RecipeProvider.conditionsFromItem(Items.COPPER_BLOCK))
                .offerTo(exporter, "stern_copper");

        ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, RedstoneLogisticalBlocks.OBSERVER_BUTTON)
                .input(Items.OBSERVER)
                .criterion("has_observer", RecipeProvider.conditionsFromItem(Items.OBSERVER))
                .offerTo(exporter, "observer_button");
    }

    public List<ItemConvertible> getColoredVariants(String pathFormat, DyeColor ...excludeColors){
        return getColoredVariants(Electromechanics.MODID, pathFormat, excludeColors);
    }

    public List<ItemConvertible> getColoredVariants(String namespace, String pathFormat, DyeColor ...excludeColors){
        var items = new ArrayList<ItemConvertible>();

        var set = Set.of(excludeColors);

        for (var color : DyeColor.values()) {
            if(set.contains(color)) continue;

            var item = Registries.ITEM.get(new Identifier(namespace, MessageFormat.format(pathFormat, color)));

            items.add(item);
        }

        return items;
    }
}
