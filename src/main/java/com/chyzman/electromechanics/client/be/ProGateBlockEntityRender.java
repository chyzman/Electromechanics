package com.chyzman.electromechanics.client.be;

import com.chyzman.electromechanics.ElectromechanicsLogistics;
import com.chyzman.electromechanics.block.gate.ProGateBlock;
import com.chyzman.electromechanics.block.gate.ProGateBlockEntity;
import com.chyzman.electromechanics.client.GateModelLoader;
import com.chyzman.electromechanics.logic.SidesHelper;
import com.chyzman.electromechanics.logic.api.Side;
import com.chyzman.electromechanics.logic.api.SignalType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import java.util.function.Consumer;

public class ProGateBlockEntityRender implements BlockEntityRenderer<ProGateBlockEntity> {

    private final BlockRenderManager manager;

    public ProGateBlockEntityRender(BlockEntityRendererFactory.Context ctx) {
        this.manager = ctx.getRenderManager();
    }

    @Override
    public void render(ProGateBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var modelManager = MinecraftClient.getInstance().getBakedModelManager();

        var model = modelManager.getModel(ElectromechanicsLogistics.id("block/gate/board/base"));

        if(model == null){
            return;
        }

        var handler = entity.getHandler();

        var state = entity.getCachedState();

        var direction = state.get(Properties.HORIZONTAL_FACING);

        RenderLayer renderLayer = RenderLayers.getMovingBlockLayer(state);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);

        var renderContext = new ModelRenderContext(manager, modelManager, entity, matrices, vertexConsumer, overlay);

        matrices.push();

        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-direction.asRotation()));
        matrices.translate(-0.5F, -0.5F, -0.5F);

        renderContext.render(model);

        matrices.pop();

        //--

        matrices.push();

        matrices.translate(0, 0.008, 0);

        var sideHelper = new SidesHelper(direction.getOpposite());

        var outputs = handler.getOutputs(entity);
        var inputs = handler.getInputs(entity);

        boolean fullDigital = true;

        for (Side output : outputs) {
            var signalType = handler.getSideSignalType(entity, output);

            if(signalType == SignalType.ANALOG) fullDigital = false;

            var pathModel = entity.getOutputPower(output) <= 0
                    ? GateModelLoader.REDSTONE_PATH_OFF
                    : GateModelLoader.REDSTONE_PATH_ON;

            var dirSide = sideHelper.getDirection(output);

            renderPath(pathModel, dirSide, matrices, renderContext::render);

            renderSideType(Blocks.ORANGE_CONCRETE, dirSide, matrices, renderContext);
        }

        for (Side input : inputs) {
            var signalType = handler.getSideSignalType(entity, input);

            if(signalType == SignalType.ANALOG) fullDigital = false;

            var pathModel = entity.getInputPower(input) <= 0
                    ? GateModelLoader.REDSTONE_PATH_OFF
                    : GateModelLoader.REDSTONE_PATH_ON;

            var dirSide = sideHelper.getDirection(input);

            renderPath(pathModel, dirSide, matrices, renderContext::render);

            renderSideType(Blocks.BLUE_CONCRETE, dirSide, matrices, renderContext);
        }

        matrices.pop();

        Identifier chipModel;

        if(outputs.size() + inputs.size() == 4){
            chipModel = GateModelLoader.FULL_CHIP;
        } else if(!fullDigital) {
            chipModel = GateModelLoader.HALF_CHIP;
        } else {
            chipModel = GateModelLoader.QUARTER_CHIP;
        }

        Side targetOutput;

        if(outputs.size() == 0){
            targetOutput = sideHelper.getSide(direction);
        } else if(outputs.size() == 3) {
            targetOutput = outputs.get(1);
        } else {
            targetOutput = outputs.get(0);
        }

        var chipDirection = sideHelper.getDirection(targetOutput);

        matrices.push();

        matrices.translate(0, 0.125, 0);

        matrices.scale(0.8f, 0.8f, 0.8f);

        matrices.translate(0.125f, 0, 0.125f);

        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-chipDirection.asRotation()));
        matrices.translate(-0.5F, -0.5F, -0.5F);

        renderContext.render(chipModel);

        matrices.pop();

        //--

        matrices.push();

        var text = Text.of(handler.displaySymbol());
        var textRenderer = MinecraftClient.getInstance().textRenderer;

        {
            var scale = 1/40f;

            matrices.scale(scale, scale, scale);

            matrices.translate(0, 6, 0);

            var textWidth = textRenderer.getWidth(text);
            var textHeight = textRenderer.fontHeight;

            float xOffset = 1.5f;
            float zOffset = 1f;

            if(direction == Direction.SOUTH){
                xOffset = -0.5f;
                zOffset = -1f;
            } else if(direction == Direction.EAST){
                xOffset = -0.5f;
                //zOffset = 1f;
            } else if(direction == Direction.WEST){
                //xOffset = 1.5f;
                zOffset = -1f;
            }

            var scaleFactor = 0.5f;

            if(chipModel == GateModelLoader.FULL_CHIP){
                scaleFactor = Math.min(12f / textWidth, 0.95f);
            } else {
                scaleFactor = Math.min(8f / textWidth, 0.75f);// ;
            }

            matrices.translate(20, 0, 20);

            matrices.scale(scaleFactor, 1, scaleFactor);

            matrices.translate(-((textWidth / 2f) + xOffset /*+ 2*/), 0, 0);

            matrices.translate(0, 0, -((textHeight / 2f) + zOffset));

            matrices.translate(0.5F, 0.5F, 0.5F);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            matrices.translate(-0.5F, -0.5F, -0.5F);

            matrices.translate(0.5F, 0.5F, 0.5F);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(direction.asRotation()), (textWidth / 2), (textHeight / 2), 0);
            matrices.translate(-0.5F, -0.5F, -0.5F);
        }

        textRenderer.draw(
                text,
                0, //0
                0,
                Colors.WHITE,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.POLYGON_OFFSET,
                0,//Colors.RED,
                light
        );

        matrices.pop();

        //--


    }

    public void renderPath(Identifier id, Direction direction, MatrixStack matrices, Consumer<Identifier> consumer){
        float offset = 0.5f - 0.0620f;

        matrices.push();

        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-direction.asRotation()));
        matrices.translate(-0.5F, -0.5F, -0.5F);

        matrices.translate(offset, 0, 0.5);

        matrices.scale(1.01f,1, 1.01f);

        matrices.scale(0.5f, 1, 1);
        matrices.translate(0.06f, 0, 0);

        consumer.accept(id);

        matrices.pop();
    }

    public void renderSideType(Block block, Direction direction, MatrixStack matrices, ModelRenderContext context){
        var model = context.renderManager.getModel(block.getDefaultState());

        var player = MinecraftClient.getInstance().player;

        if(player == null || !player.shouldCancelInteraction()) return;

        matrices.push();

        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-direction.asRotation()));
        matrices.translate(-0.5F, -0.5F, -0.5F);

        matrices.translate(0.5 - 0.05, 0, 0.5);
        matrices.translate(0, -0.15, 0.38);

        matrices.scale(0.1f, 0.1f, 0.1f);
        matrices.translate(0, 2, 0);

        context.render(model);

        matrices.pop();
    }

    public static class ModelRenderContext {
        private final BlockRenderManager renderManager;
        private final BakedModelManager modelManager;

        private final MatrixStack matrices;
        private final VertexConsumer vertexConsumer;

        private final BlockEntity blockEntity;

        private final long seed;
        private final int overlay;

        public ModelRenderContext(BlockRenderManager renderManager, BakedModelManager modelManager, BlockEntity blockEntity, MatrixStack matrices, VertexConsumer vertexConsumer, int overlay){
            this.renderManager = renderManager;
            this.modelManager = modelManager;

            this.matrices = matrices;
            this.vertexConsumer = vertexConsumer;

            this.blockEntity = blockEntity;

            this.seed = blockEntity.getCachedState()
                    .getRenderingSeed(blockEntity.getPos());
            this.overlay = overlay;
        }

        public void render(Identifier id){
            var model = modelManager.getModel(id);

            render(model);
        }

        public void render(BakedModel model){
            var world = blockEntity.getWorld();
            var pos = blockEntity.getPos();
            var state = blockEntity.getCachedState();

            var random = world.getRandom();

            renderManager.getModelRenderer().render(world, model, state, pos, matrices, vertexConsumer, false, random, seed, overlay);
        }
    }
}
