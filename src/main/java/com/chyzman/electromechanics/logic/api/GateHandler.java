package com.chyzman.electromechanics.logic.api;

import com.chyzman.electromechanics.logic.api.state.GateContext;
import com.chyzman.electromechanics.logic.api.state.GateStateStorage;
import com.chyzman.electromechanics.logic.api.configuration.IOConfiguration;
import com.chyzman.electromechanics.logic.api.configuration.SideOrientationHelper;
import com.chyzman.electromechanics.logic.api.configuration.Side;
import com.chyzman.electromechanics.logic.api.configuration.SignalType;
import com.chyzman.electromechanics.logic.api.mode.ExpressionModeHandler;
import com.chyzman.electromechanics.logic.api.mode.MultiExpressionModeHandler;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.BuiltInEndecs;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GateHandler {

    private static final Map<Identifier, GateHandler> HANDLERS = new HashMap<>();

    public static final Endec<GateHandler> ENDEC = BuiltInEndecs.IDENTIFIER.xmap(HANDLERS::get, GateHandler::getId);

    // --

    private final Identifier id;
    private final String displaySymbol;

    private final GateOutputFunction outputFunc;
    private @Nullable IOConfiguration ioConfiguration = null;

    private final GateSetupEvent setupEvent;
    private final GateInteractEvent interactEvent;
    private @Nullable GateTickEvent tickEvent = null;

    public GateHandler(Identifier id, String displaySymbol, GateOutputFunction outputFunc, GateSetupEvent setupEvent, GateInteractEvent interactEvent){
        if(HANDLERS.containsKey(id)){
            throw new IllegalStateException("Unable to add created AbstractGateHandler due to a existing Identifier being registered! [Id: " + id + "]");
        }

        HANDLERS.put(id, this);

        this.id = id;
        this.displaySymbol = displaySymbol;

        this.outputFunc = outputFunc;

        this.setupEvent = setupEvent;
        this.interactEvent = interactEvent;
    }

    // --

    public static GateHandler of(Identifier id, String displaySymbol, GateOutputFunction outputFunc, GateSetupEvent setupEvent, GateInteractEvent interactEvent){
        return new GateHandler(id, displaySymbol, outputFunc, setupEvent, interactEvent);
    }

    public static GateHandler of(Identifier id, String displaySymbol, IOConfiguration configuration, GateOutputFunction outputFunc, GateSetupEvent setupEvent, GateInteractEvent interactEvent, GateTickEvent tickEvent){
        return new GateHandler(id, displaySymbol, outputFunc, setupEvent, interactEvent).setIOConfig(configuration).setTickEvent(tickEvent);
    }

    public static GateHandler singleExpression(Identifier id, String displaySymbol, IOConfiguration configuration, ExpressionModeHandler handler){
        return of(id, displaySymbol, configuration, handler.create(), handler, handler, null);
    }

    public static GateHandler multiExpression(Identifier id, String displaySymbol, IOConfiguration configuration, MultiExpressionModeHandler handler){
        return of(id, displaySymbol, configuration, handler.create(), handler, handler, null);
    }

    protected GateHandler setIOConfig(IOConfiguration configuration){
        this.ioConfiguration = configuration;

        return this;
    }

    protected GateHandler setTickEvent(GateTickEvent tickEvent){
        this.tickEvent = tickEvent;

        return this;
    }

    // --

    public String displaySymbol(){
        return this.displaySymbol;
    }

    public void setupStorage(GateStateStorage storage){
        this.setupEvent.setup(storage);
    }

    public ActionResult interactWithGate(GateContext context){
        return this.interactEvent.interact(context.storage());
    }

    public ActionResult onTick(GateContext context){
        if(tickEvent == null || !tickEvent.shouldTick(context)) return ActionResult.PASS;

        return tickEvent.onTick(this, context);
    }

    //--

    public List<Side> getInputs(GateStateStorage stateStorage){
        return (ioConfiguration != null)
                ? ioConfiguration.inputs()
                : stateStorage.dynamicStorage().get(GateStateStorage.INPUTS);
    }

    public List<Side> getOutputs(GateStateStorage stateStorage){
        return (ioConfiguration != null)
                ? ioConfiguration.outputs()
                : stateStorage.dynamicStorage().get(GateStateStorage.OUTPUTS);
    }

    public SignalType getSideSignalType(GateStateStorage storage, Side side){
        var config = storage.dynamicStorage().get(GateStateStorage.SIGNAL_CONFIGURATION);

        var isInput = getInputs(storage).contains(side);

        return config.getSideSignalType(side, isInput);
    }

    public int getUpdateDelay(GateContext context){
        return context.storage().dynamicStorage().get(GateStateStorage.UPDATE_DELAY);
    }

    // --

    public boolean wireConnectsTo(GateStateStorage storage, Direction facing, Direction dir){
        SideOrientationHelper helper = new SideOrientationHelper(facing);

        Side side = helper.getSide(dir);

        return this.getInputs(storage).contains(side) || this.getOutputs(storage).contains(side);
    }

    public int getPowerLevel(GateContext context, Direction dir){
        return gatherOutputData(context)
                .getOrDefault(context.getSide(dir), 0);
    }

    public boolean isPowered(GateContext context, Direction dir){
        gatherOutputData(context);

        return context.storage().isOutputtingPower(context.getSide(dir));
    }

    public boolean isPowered(GateContext context){
        return gatherOutputData(context).values().stream().anyMatch(integer -> integer > 0);
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
            if(!Objects.equals(outputData.getOrDefault(output, 0), oldOutputs.get(output))){
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

        var map = this.outputFunc.calculateOutput(this, context, inputData);

        if(context.updateOutput()){
            map.forEach(context.storage()::setOutputPower);
        }

        return map;
    }

    // --

    @Nullable
    public static GateHandler getHandler(Identifier id){
        return HANDLERS.get(id);
    }

    public Identifier getId(){
        return this.id;
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
