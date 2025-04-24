package com.chyzman.electromechanics.block.gate;

import com.chyzman.electromechanics.Electromechanics;
import com.chyzman.electromechanics.logic.api.state.GateStateStorage;
import com.chyzman.electromechanics.logic.api.GateHandler;
import com.chyzman.electromechanics.logic.api.state.ImplGateStateStorage;
import com.chyzman.electromechanics.logic.api.state.WorldGateContext;
import io.wispforest.owo.ops.WorldOps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class GateBlockEntity extends BlockEntity {

    private static BlockEntityType<GateBlockEntity> GATE_TYPE = null;

    protected static final List<Block> VALID_BLOCKS = new ArrayList<>();

    public static BlockEntityType<GateBlockEntity> getBlockEntityType(){
        if(GATE_TYPE == null){
            GATE_TYPE = Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Electromechanics.id("pro_gate"),
                    BlockEntityType.Builder.create(GateBlockEntity::createBlockEntity, VALID_BLOCKS.toArray(Block[]::new)).build());
        }

        return GATE_TYPE;
    }

    public static void addValidBlock(GateBlock block) {
        VALID_BLOCKS.add(block);
    }

    //--

    private final GateHandler handler;
    private final ImplGateStateStorage storage = new ImplGateStateStorage(
            state -> this.getWorld() != null && !this.getWorld().isClient(), state -> this.markDirty()
    );

    private GateBlockEntity(BlockPos pos, BlockState state, GateHandler handler) {
        super(getBlockEntityType(), pos, state);

        this.handler = handler;
    }

    public static GateBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if(!(state.getBlock() instanceof GateBlock proGateBlock)){
            throw new IllegalStateException("Unable to get the needed AbstractGateHandler from the BlockState");
        }

        var blockEntity = new GateBlockEntity(pos, state, proGateBlock.handler);

        proGateBlock.handler.setupStorage(blockEntity.storage());

        return blockEntity;
    }

    public GateHandler getHandler(){
        return this.handler;
    }

    public GateStateStorage storage(){
        return this.storage;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        var nbt = super.toInitialChunkDataNbt(registryLookup);

        writeNbt(nbt, registryLookup);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        this.storage.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        this.storage.writeNbt(nbt);
    }

    @Override
    public void markDirty() {
        super.markDirty();

        WorldOps.updateIfOnServer(this.world, this.pos);
    }

    //--

    public void tick() {
        if(this.world.isClient()) return;

        var context = WorldGateContext.of(this.world, this.pos);

        if (context == null) return;

        var result = this.handler.onTick(context);

        if(result == ActionResult.SUCCESS){
            world.updateNeighborsAlways(pos, this.getCachedState().getBlock());

            //this.getCachedState().updatePowered(world, pos, state);
            ((GateBlock) this.getCachedState().getBlock()).updatePowered(this.world, this.pos, this.getCachedState());
        }
    }

    //--
}
