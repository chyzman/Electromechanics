package com.chyzman.chyzyLogistics.logic.api.handlers;

import com.chyzman.chyzyLogistics.block.gate.GateStateStorage;
import com.chyzman.chyzyLogistics.logic.api.Side;
import com.chyzman.chyzyLogistics.logic.api.mode.ModeHandler;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class StaticIOGateHandler<M extends ModeHandler> extends GateHandler<M> {

    private final List<Side> inputs;
    private final List<Side> outputs;

    public StaticIOGateHandler(Identifier id, List<Side> inputs, List<Side> outputs, M modeHandler) {
        super(id, modeHandler);

        this.inputs = inputs;
        this.outputs = outputs;
    }

    // --

    public List<Side> getInputs(GateStateStorage stateStorage) {
        return this.inputs;
    }

    public List<Side> getOutputs(GateStateStorage stateStorage) {
        return this.outputs;
    }

    // --

}
