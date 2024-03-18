package com.chyzman.electromechanics.block.gate;

import com.chyzman.electromechanics.block.redstone.RedstoneEvents;
import com.chyzman.electromechanics.logic.api.GateHandler;
import com.chyzman.electromechanics.logic.api.configuration.Side;
import com.chyzman.electromechanics.logic.api.state.WorldGateContext;
import com.chyzman.electromechanics.util.BlockEntityOps;
import com.mojang.serialization.MapCodec;
import io.wispforest.endec.CodecUtils;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.TickPriority;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class GateBlock extends AbstractRedstoneGateBlock implements ImplBlockEntityProvider {

    static {
        RedstoneEvents.SHOULD_CANCEl_CONNECTION.register((world, pos, state, pos2, state2) -> {
            if(!(state.getBlock() instanceof RedstoneWireBlock)) return false;
            if(!(state2.getBlock() instanceof GateBlock gateBlock)) return false;

            var xDiff = pos.getX() - pos2.getX();
            var zDiff = pos.getZ() - pos2.getZ();

            var blockEntity = world.getBlockEntity(pos2, GateBlockEntity.getBlockEntityType()).get();

            return !gateBlock.handler.wireConnectsTo(blockEntity.storage(), state2.get(FACING).getOpposite(), Direction.fromVector(xDiff, 0, zDiff));
        });
    }

    protected final GateHandler handler;

    public GateBlock(GateHandler handler){
        this(handler, FabricBlockSettings.copy(Blocks.REPEATER));

        GateBlockEntity.GATE_TYPE_BUILDER.addBlock(this);
    }

    public GateBlock(GateHandler handler, Settings settings) {
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
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        world.getBlockEntity(pos, GateBlockEntity.getBlockEntityType()).ifPresent(blockEntity -> {
            BlockEntityOps.readFromCarrier(blockEntity, itemStack.getNbt());
        });
    }

    //--

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        var stack = this.asItem().getDefaultStack();

        world.getBlockEntity(pos, GateBlockEntity.getBlockEntityType())
                .ifPresent(gateBlockEntity -> gateBlockEntity.setStackNbt(stack));

        return stack;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return GateBlockEntity.createBlockEntity(pos, state);
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

    @Override
    public <T extends BlockEntity> Consumer<T> getTickMethod() {
        return t -> ((GateBlockEntity) t).tick();
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> getType() {
        return (BlockEntityType<T>) GateBlockEntity.getBlockEntityType();
    }

    //--

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!this.isLocked(world, pos, state)) {
            WorldGateContext context = WorldGateContext.of(world, pos);

            context.toggleUpdateOutput(true);

            for (Side changedOutput : this.handler.changedOutputs(context)) {
                //System.out.println(changedOutput);

                var pos2 = pos.offset(context.getDirection(changedOutput));

                world.updateNeighbor(pos2, this, pos);

                // For above and below redstone powering
                if(world.getBlockState(pos2).isSolidBlock(world, pos2)){
                    world.updateNeighbor(pos2.offset(Direction.UP), this, pos);
                    world.updateNeighbor(pos2.offset(Direction.DOWN), this, pos);
                }
            }
        }
    }

    public void updatePowered(World world, BlockPos pos, BlockState state) {
        if (!this.isLocked(world, pos, state)) {
            WorldGateContext context = WorldGateContext.of(world, pos);

            boolean bl = false;

            var changedOutputs = this.handler.changedOutputs(context);

            for (Side changedOutput : changedOutputs) {
                if(context.storage().isOutputtingPower(changedOutput)){
                    bl = true;
                    break;
                }
            }

            var bl2 = !changedOutputs.isEmpty();

            if (bl2 && !world.getBlockTickScheduler().isTicking(pos, this)) {
                TickPriority tickPriority = TickPriority.HIGH;
                if (this.isTargetNotAligned(world, pos, state)) {
                    tickPriority = TickPriority.EXTREMELY_HIGH;
                } else if (bl) {
                    tickPriority = TickPriority.VERY_HIGH;
                }

                world.scheduleBlockTick(pos, this, this.handler.getUpdateDelay(context), tickPriority);
            }
        }
    }


    //--

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) return ActionResult.PASS;

        var context = WorldGateContext.of(world, pos);

        var result = handler.interactWithGate(context);

        if(result == ActionResult.SUCCESS){
            world.updateNeighborsAlways(pos, state.getBlock());
        }

        world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, context.storage().getMode() == 1 ? 0.55F : 0.5F);

        updatePowered(world, pos, state);

        return ActionResult.success(world.isClient);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView worldView, BlockPos pos, Direction direction) {
        if(!(worldView instanceof World world) || direction.getAxis().isVertical()) return 0;

        WorldGateContext context = WorldGateContext.of(world, pos);

        var side = context.getSide(direction.getOpposite());

        if(!this.handler.getOutputs(context.storage()).contains(side)) return 0;

        return context.storage().getOutputPower(side);
    }

    //--

    @Deprecated
    @Override
    protected int getPower(World world, BlockPos pos, BlockState state) {
        if(!hasPower(world, pos, state)) return 0;

        WorldGateContext context = WorldGateContext.of(world, pos);

        return context.storage().getOutputPower(context.getSide(context.getFacing()));
    }

    @Deprecated
    @Override
    public boolean hasPower(World world, BlockPos pos, BlockState state){
        var context = WorldGateContext.of(world, pos);

        //handler.isPowered(context);

        return context.storage().isOutputtingPower();
    }

    @Deprecated
    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 2;
    }
}