package com.chyzman.chyzyLogistics.block.gate;

import com.chyzman.chyzyLogistics.block.redstone.RedstoneEvents;
import com.chyzman.chyzyLogistics.logic.*;
import com.mojang.serialization.MapCodec;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ProGateBlock extends AbstractRedstoneGateBlock implements ImplBlockEntityProvider {

    static {
        RedstoneEvents.SHOULD_CANCEl_CONNECTION.register((world, pos, state, pos2, state2) -> {
            if(!(state.getBlock() instanceof RedstoneWireBlock)) return false;
            if(!(state2.getBlock() instanceof ProGateBlock gateBlock)) return false;

            var xDiff = pos2.getX() - pos.getX();
            var zDiff = pos2.getZ() - pos.getZ();

            var blockEntity = world.getBlockEntity(pos2, ProGateBlockEntity.getBlockEntityType()).get();

            return !gateBlock.handler.wireConnectsTo(blockEntity, state2.get(FACING), Direction.fromVector(xDiff, 0, zDiff));
        });
    }

    public MapCodec<ProGateBlock> CODEC = StructEndecBuilder.of(
            GateHandler.ENDEC.fieldOf("gate_handler", block -> block.handler),
            Endec.ofCodec(AbstractBlock.Settings.CODEC).fieldOf("properties", AbstractBlock::getSettings),
            ProGateBlock::new
    ).mapCodec();

    protected final GateHandler handler;

    public ProGateBlock(GateHandler handler){
        this(handler, FabricBlockSettings.copy(Blocks.REPEATER));
    }

    public ProGateBlock(GateHandler handler, Settings settings) {
        super(settings);

        this.handler = handler;

        this.setDefaultState(this.stateManager
                .getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(POWERED, Boolean.FALSE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    protected MapCodec<? extends AbstractRedstoneGateBlock> getCodec(){
        return CODEC;
    }

    //--

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ProGateBlockEntity.createBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return ImplBlockEntityProvider.super.createScreenHandlerFactory(world, pos);
    }

    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        return ImplBlockEntityProvider.super.onSyncedBlockEvent(world, pos, type, data);
    }

    //--

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        }

        var blockEntity = world.getBlockEntity(pos, ProGateBlockEntity.getBlockEntityType()).get();

        WorldGateContext context = WorldGateContext.of(world, pos, blockEntity);

        handler.interactWithGate(context);

        world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, blockEntity.getMode() == 1 ? 0.55F : 0.5F);

        updatePowered(world, pos, state);

        return ActionResult.success(world.isClient);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView worldView, BlockPos pos, Direction direction) {
        if(!(worldView instanceof RedstoneView redstoneView)) return 0;

        WorldGateContext context = WorldGateContext.of(redstoneView, pos);

        return context.stateStorage.getOutputPower(context.getSide(direction));
    }

    @Override
    protected int getPower(World world, BlockPos pos, BlockState state) {
        WorldGateContext context = WorldGateContext.of(world, pos);

        if(hasPower(world, pos, state)){
            return context.stateStorage.getOutputPower(context.getSide(context.getFacing()));
        }

        return 0;
    }

    @Override
    public boolean hasPower(World world, BlockPos pos, BlockState state){
        var blockEntity = world.getBlockEntity(pos, ProGateBlockEntity.getBlockEntityType()).get();

        WorldGateContext context = WorldGateContext.of(world, pos, blockEntity);

        return handler.isPowered(context);
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 2;
    }
}