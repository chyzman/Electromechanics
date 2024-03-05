package com.chyzman.electromechanics.client.be;

import com.chyzman.electromechanics.block.gate.GateBlockEntity;
import com.chyzman.electromechanics.item.GateBlockItem;
import com.chyzman.electromechanics.util.BlockEntityOps;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class GateBlockItemRender implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var render = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();

        BlockEntity blockEntity = BlockEntityOps.createFromTypeAndStack(GateBlockEntity.getBlockEntityType(), stack);

        if(blockEntity == null) return;

        blockEntity.setWorld(MinecraftClient.getInstance().world);

        matrices.push();

        if(mode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND || mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND) {
            matrices.translate(0, 0.65, 0);
        } else if(mode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND) {
            matrices.translate(0, 0.3, 0);
        }

        render.renderEntity(blockEntity, matrices, vertexConsumers, light, overlay);

        matrices.pop();
    }
}
