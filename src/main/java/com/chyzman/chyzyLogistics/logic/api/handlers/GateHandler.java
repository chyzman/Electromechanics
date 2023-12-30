package com.chyzman.chyzyLogistics.logic.api.handlers;

import com.chyzman.chyzyLogistics.block.gate.GateStateStorage;
import com.chyzman.chyzyLogistics.logic.api.GateContext;
import com.chyzman.chyzyLogistics.logic.api.Side;
import com.chyzman.chyzyLogistics.logic.api.SidesHelper;
import com.chyzman.chyzyLogistics.logic.api.SignalType;
import com.chyzman.chyzyLogistics.logic.api.mode.ModeHandler;
import com.chyzman.chyzyLogistics.util.GateMathUtils;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.BuiltInEndecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GateHandler<M extends ModeHandler> {

    public static final Map<Identifier, GateHandler<?>> HANDLERS = new HashMap<>();

    public static final Endec<GateHandler<?>> ENDEC = BuiltInEndecs.IDENTIFIER.xmap(HANDLERS::get, GateHandler::getId);

    private final Identifier id;

    protected final M modeHandler;

    public GateHandler(Identifier id, M modeHandler){
        if(HANDLERS.containsKey(id)){
            throw new IllegalStateException("Unable to add created AbstractGateHandler due to a existing Identifier being registered! [Id: " + id + "]");
        }

        this.id = id;

        this.modeHandler = modeHandler;
    }

    @Nullable
    public static GateHandler<?> getHandler(Identifier id){
        return HANDLERS.get(id);
    }

    // --

    public abstract List<Side> getInputs(GateStateStorage stateStorage);

    public abstract List<Side> getOutputs(GateStateStorage stateStorage);

    public SignalType getInputType(GateContext context, Side side){
        return this.modeHandler.getInputType(context.storage().getMode(), side);
    }

    public SignalType getOutputType(GateContext context, Side side){
        return this.modeHandler.getOutputType(context.storage().getMode(), side);
    }

    public void interactWithGate(GateContext context){
        this.modeHandler.handleModeChange(context.storage());
    }

    public int getUpdateDelay(GateContext context){
        return this.modeHandler.getUpdateDelay(context.storage().getMode());
    }

    // --

    public boolean wireConnectsTo(GateStateStorage storage, Direction facing, Direction dir){
        SidesHelper helper = new SidesHelper(facing);

        Side side = helper.getSide(dir);

        return this.getInputs(storage).contains(side) || this.getOutputs(storage).contains(side);
    }

    public int getPowerLevel(GateContext context, Direction dir){
        return GateMathUtils.getOutputPower(gatherOutputData(context), context.getSide(dir));
    }

    public boolean isPowered(GateContext context, Direction dir){
        gatherOutputData(context);

        return context.storage().isOutputtingPower(context.getSide(dir));
    }

    public boolean isPowered(GateContext context){
        return GateMathUtils.isOutputtingPower(gatherOutputData(context));
    }

    public List<Side> changedOutputs(GateContext context){
        List<Side> outputs = getOutputs(context.storage());

        Map<Side, Integer> oldOutputs = new HashMap<>();

        for (Side output : outputs) {
            oldOutputs.put(output, context.storage().getOutputPower(output));
        }

        var outputData = gatherOutputData(context);

        List<Side> changedOutputs = new ArrayList<>();

        for (Side output : outputs) {
            if(GateMathUtils.getOutputPower(outputData, output) != oldOutputs.get(output)){
                changedOutputs.add(output);
            }
        }

        return changedOutputs;
    }

    private Map<Side, Integer> gatherOutputData(GateContext context){
        Map<Side, Integer> inputData = new HashMap<>();

        for (Side input : getInputs(context.storage())) {
            int inputAmount = context.getEmittedRedstonePower(input);

            context.storage().setInputPower(input, inputAmount);

            inputData.put(input, inputAmount);
        }

        var map = this.calculateOutputData(context, inputData);

        if(context.updateOutput()){
            map.forEach(context.storage()::setOutputPower);
        }

        return map;
    }

    public abstract Map<Side, Integer> calculateOutputData(GateContext context, Map<Side, Integer> inputData);

    // --

    public Identifier getId(){
        return this.id;
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
