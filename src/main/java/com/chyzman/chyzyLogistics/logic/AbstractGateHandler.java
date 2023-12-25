package com.chyzman.chyzyLogistics.logic;

import com.chyzman.chyzyLogistics.block.redstone.RedstoneEvents;
import com.chyzman.chyzyLogistics.registries.RedstoneWires;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.BuiltInEndecs;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractGateHandler {

//    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
//    public static final IntProperty POWER = IntProperty.of("power", 0, 15);

    public static final Map<Identifier, AbstractGateHandler> HANDLERS = new HashMap<>();

    public static Endec<AbstractGateHandler> ENDEC = BuiltInEndecs.IDENTIFIER.xmap(HANDLERS::get, AbstractGateHandler::getId);

    public final Identifier id;

    public final List<Side> inputs;
    public final List<Side> outputs;

    private final Consumer<GateContext> interactEvent;

    public AbstractGateHandler(Identifier id, Consumer<GateContext> interactEvent, List<Side> inputs, List<Side> outputs){
        if(HANDLERS.containsKey(id)){
            throw new IllegalStateException("Unable to add created AbstractGateHandler due to a existing Identifier being registered! [Id: " + id + "]");
        }

        for (Side input : inputs) {
            if(outputs.contains(input)){
                throw new IllegalStateException("A side can not be a input and a output!");
            }
        }

        this.id = id;

        this.interactEvent = interactEvent;

        this.inputs = inputs;
        this.outputs = outputs;
    }

    public Identifier getId(){
        return this.id;
    }

    @Nullable
    public static AbstractGateHandler getHandler(Identifier id){
        return HANDLERS.get(id);
    }

    public void interactWithGate(GateContext context){
        this.interactEvent.accept(context);

        if(context.stateStorage.hasChangesOccurred()){
            updateOutputData(context);
        }
    }

    public boolean wireConnectsTo(Direction facing, Direction dir){
        SidesHelper helper = new SidesHelper(facing);

        Side side = helper.getSide(dir);

        return this.inputs.contains(side) || this.outputs.contains(side);
    }

    public int getPowerLevel(GateContext context, Direction dir){
        updateOutputData(context);

        return context.stateStorage.getOutputPower(context.getSide(dir));
    }

    public boolean isPowered(GateContext context, Direction dir){
        updateOutputData(context);

        return context.stateStorage.isOutputtingPower(context.getSide(dir));
    }

    public boolean isPowered(GateContext context){
        updateOutputData(context);

        return context.stateStorage.isOutputtingPower();
    }

    public void updateOutputData(GateContext context){
        Map<Side, Integer> inputData = new HashMap<>();

        for (Side input : inputs) {
            int inputAmount = context.getEmittedRedstonePower(input);

            context.stateStorage.setInputPower(input, inputAmount);

            inputData.put(input, inputAmount);
        }

        this.calculateOutputData(context, inputData).forEach(context.stateStorage::setOutputPower);
    }

    protected abstract Map<Side, Integer> calculateOutputData(GateContext context, Map<Side, Integer> inputData);

    @Override
    public String toString() {
        return this.id.toString();
    }
}
