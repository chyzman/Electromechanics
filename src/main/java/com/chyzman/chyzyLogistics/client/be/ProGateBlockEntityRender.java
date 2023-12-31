package com.chyzman.chyzyLogistics.client.be;

import com.chyzman.chyzyLogistics.ChyzyLogistics;
import com.chyzman.chyzyLogistics.block.gate.ProGateBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class ProGateBlockEntityRender implements BlockEntityRenderer<ProGateBlockEntity> {

    private final BlockRenderManager manager;

    public ProGateBlockEntityRender(BlockEntityRendererFactory.Context ctx) {
        this.manager = ctx.getRenderManager();
    }

    @Override
    public void render(ProGateBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var model = MinecraftClient.getInstance().getBakedModelManager().getModel(ChyzyLogistics.id("block/gate/board/base"));

        if(model == null){
            return;
        }

        var world = entity.getWorld();
        var pos = entity.getPos();
        var state = entity.getCachedState();

        RenderLayer renderLayer = RenderLayers.getMovingBlockLayer(state);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);

        var direction = state.get(Properties.HORIZONTAL_FACING);

        matrices.push();

        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-direction.asRotation()));
        matrices.translate(-0.5F, -0.5F, -0.5F);

        manager.getModelRenderer().render(
                world,
                model,
                state,
                entity.getPos(),
                matrices,
                vertexConsumer,
                false,
                world.getRandom(),
                state.getRenderingSeed(pos),
                overlay
        );

        //--

        matrices.push();



        matrices.pop();

        //--

        matrices.pop();
    }
}
