package com.chyzman.electromechanics.logic.api.handlers;

import com.chyzman.electromechanics.block.gate.GateStateStorage;
import com.chyzman.electromechanics.logic.api.GateContext;
import com.chyzman.electromechanics.logic.api.Side;
import com.chyzman.electromechanics.logic.SidesHelper;
import com.chyzman.electromechanics.logic.api.SignalType;
import com.chyzman.electromechanics.logic.api.GateInteractEvent;
import com.chyzman.electromechanics.logic.GateMathUtils;
import com.chyzman.electromechanics.logic.api.SetupDynamicStorage;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.BuiltInEndecs;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GateHandler {

    public static final Map<Identifier, GateHandler> HANDLERS = new HashMap<>();

    public static final Endec<GateHandler> ENDEC = BuiltInEndecs.IDENTIFIER.xmap(HANDLERS::get, GateHandler::getId);

    // --

    private final Identifier id;

    private final GateInteractEvent interactEvent;
    private final SetupDynamicStorage setupEvent;

    private String displaySymbol = "";

    public GateHandler(Identifier id, GateInteractEvent interactEvent, SetupDynamicStorage setupEvent){
        if(HANDLERS.containsKey(id)){
            throw new IllegalStateException("Unable to add created AbstractGateHandler due to a existing Identifier being registered! [Id: " + id + "]");
        }

        this.id = id;

        this.interactEvent = interactEvent;
        this.setupEvent = setupEvent;
    }

    public static GateHandler of(Identifier id, GateInteractEvent interactEvent, SetupDynamicStorage setupEvent, TriFunction<GateHandler, GateContext, Map<Side, Integer>, Map<Side, Integer>> outputFunc){
        return new GateHandler(id, interactEvent, setupEvent) {
            @Override
            protected Map<Side, Integer> calculateOutputData(GateContext context, Map<Side, Integer> inputData) {
                return outputFunc.apply(this, context, inputData);
            }
        };
    }

    @Nullable
    public static GateHandler getHandler(Identifier id){
        return HANDLERS.get(id);
    }

    // --

    public GateHandler displaySymbol(String displaySymbol){
        this.displaySymbol = displaySymbol;

        return this;
    }

    public String displaySymbol(){
        return this.displaySymbol;
    }

    public ActionResult interactWithGate(GateContext context){
        return interactEvent.interact(context.storage());
    }

    public void setupStorage(GateStateStorage storage){
        setupEvent.setup(storage);
    }

    public List<Side> getInputs(GateStateStorage stateStorage){
        return stateStorage.dynamicStorage().get(GateStateStorage.INPUTS);
    }

    public List<Side> getOutputs(GateStateStorage stateStorage){
        return stateStorage.dynamicStorage().get(GateStateStorage.OUTPUTS);
    }

    public SignalType getSideSignalType(GateContext context, Side side){
        var config = context.storage().dynamicStorage().get(GateStateStorage.SIGNAL_CONFIGURATION);

        var isInput = getInputs(context.storage()).contains(side);

        return config.getSideSignalType(side, isInput);
    }

    public int getUpdateDelay(GateContext context){
        return context.storage().dynamicStorage().get(GateStateStorage.UPDATE_DELAY);
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

    //--

    protected Map<Side, Integer> gatherInputData(GateContext context){
        Map<Side, Integer> inputData = new HashMap<>();

        for (Side input : getInputs(context.storage())) {
            int inputAmount = context.getEmittedRedstonePower(input);

            context.storage().setInputPower(input, inputAmount);

            inputData.put(input, inputAmount);
        }

        return inputData;
    }

    protected Map<Side, Integer> gatherOutputData(GateContext context){
        Map<Side, Integer> inputData = gatherInputData(context);

        var map = this.calculateOutputData(context, inputData);

        if(context.updateOutput()){
            map.forEach(context.storage()::setOutputPower);
        }

        return map;
    }

    protected abstract Map<Side, Integer> calculateOutputData(GateContext context, Map<Side, Integer> inputData);

    // --

    public Identifier getId(){
        return this.id;
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
