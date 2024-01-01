package com.chyzman.electromechanics.client.be;

import com.chyzman.electromechanics.item.ProGateBlockItem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ProGateBlockItemRender implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var render = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();

        var item = stack.getItem();

        if(!(item instanceof ProGateBlockItem blockItem)) return;

        var nbt = stack.getNbt();

        if(nbt == null) return;

        BlockEntity blockEntity = BlockEntity.createFromNbt(BlockPos.ORIGIN, blockItem.getBlock().getDefaultState(), nbt.getCompound("BlockEntityTag"));

        if(blockEntity == null) return;

        blockEntity.setWorld(MinecraftClient.getInstance().world);

        render.renderEntity(blockEntity, matrices, vertexConsumers, light, overlay);
    }
}
