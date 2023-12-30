package com.chyzman.chyzyLogistics.logic.api.handlers;

import com.chyzman.chyzyLogistics.block.gate.GateStateStorage;
import com.chyzman.chyzyLogistics.logic.api.Side;
import com.chyzman.chyzyLogistics.logic.api.GateInteractEvent;
import com.chyzman.chyzyLogistics.logic.api.SetupDynamicStorage;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class StaticIOGateHandler extends GateHandler {

    private final List<Side> inputs;
    private final List<Side> outputs;

    public StaticIOGateHandler(Identifier id, List<Side> inputs, List<Side> outputs, GateInteractEvent interactEvent, SetupDynamicStorage setupEvent) {
        super(id, interactEvent, setupEvent);

        this.inputs = inputs;
        this.outputs = outputs;
    }

    @Override
    public List<Side> getInputs(GateStateStorage stateStorage) {
        return this.inputs;
    }

    @Override
    public List<Side> getOutputs(GateStateStorage stateStorage) {
        return this.outputs;
    }
}
