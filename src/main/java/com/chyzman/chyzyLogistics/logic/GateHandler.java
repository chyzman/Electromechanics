package com.chyzman.chyzyLogistics.logic;

import com.chyzman.chyzyLogistics.block.gate.GateStateStorage;
import com.chyzman.chyzyLogistics.logic.mode.ModeHandler;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.BuiltInEndecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GateHandler {

    public static final Map<Identifier, GateHandler> HANDLERS = new HashMap<>();

    public static Endec<GateHandler> ENDEC = BuiltInEndecs.IDENTIFIER.xmap(HANDLERS::get, GateHandler::getId);

    public final Identifier id;

    protected final ModeHandler modeHandler;

    public GateHandler(Identifier id, ModeHandler modeHandler){
        if(HANDLERS.containsKey(id)){
            throw new IllegalStateException("Unable to add created AbstractGateHandler due to a existing Identifier being registered! [Id: " + id + "]");
        }

        this.id = id;

        this.modeHandler = modeHandler;
    }

//    public static <H extends ModeHandler> GateHandler of(Identifier id, Supplier<H> supplier, Consumer<H> builder){
//        return of(id, supplier.get(), builder);
//    }

    public static <H extends ModeHandler> GateHandler of(Identifier id, H handler, Consumer<H> builder){
        builder.accept(handler);

        return new GateHandler(id, handler);
    }

    @Nullable
    public static GateHandler getHandler(Identifier id){
        return HANDLERS.get(id);
    }

    public SignalType getInputType(Side side){
        return this.modeHandler.getInputType(side);
    }

    public SignalType getOutputType(Side side){
        return this.modeHandler.getOutputType(side);
    }

    public List<Side> getInputs(GateStateStorage stateStorage) {
        return this.modeHandler.getInputSides(stateStorage.getMode());
    }

    public List<Side> getOutputs(GateStateStorage stateStorage) {
        return this.modeHandler.getOutputSides(stateStorage.getMode());
    }

    public void interactWithGate(GateContext context){
        this.modeHandler.handleModeChange(context.stateStorage);
    }

    public boolean wireConnectsTo(GateStateStorage storage, Direction facing, Direction dir){
        SidesHelper helper = new SidesHelper(facing);

        Side side = helper.getSide(dir);

        return this.getInputs(storage).contains(side) || this.getOutputs(storage).contains(side);
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

    private void updateOutputData(GateContext context){
        Map<Side, Integer> inputData = new HashMap<>();

        for (Side input : getInputs(context.stateStorage)) {
            int inputAmount = context.getEmittedRedstonePower(input);

            context.stateStorage.setInputPower(input, inputAmount);

            inputData.put(input, inputAmount);
        }

        this.calculateOutputData(context, inputData).forEach(context.stateStorage::setOutputPower);
    }

    public Map<Side, Integer> calculateOutputData(GateContext context, Map<Side, Integer> inputData) {
        var inputs = this.getInputs(context.stateStorage);

        Integer[] integers = new Integer[inputData.size()];

        for (int i = 0; i < inputs.size(); i++) {
            var side = inputs.get(i);

            integers[i] = this.getInputType(side).evaluateInput(inputData.get(side));
        }

        Integer outputPower = modeHandler.getExpression(context.stateStorage.getMode()).apply(context, integers);

        Map<Side, Integer> outputData = new HashMap<>();

        for (Side output : getOutputs(context.stateStorage)) {
            outputData.put(output, getOutputType(output).evaluateOutput(outputPower));
        }

        return outputData;
    }

    public Identifier getId(){
        return this.id;
    }

    @Override
    public String toString() {
        return this.id.toString();
    }

}
