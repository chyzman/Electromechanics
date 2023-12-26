package com.chyzman.chyzyLogistics.logic.mode;

import com.chyzman.chyzyLogistics.logic.Side;
import com.chyzman.chyzyLogistics.logic.SignalType;
import com.chyzman.chyzyLogistics.logic.mode.ModeHandler;

import java.util.List;

public abstract class StaticIOModeHandler implements ModeHandler {

    protected final List<Side> inputs;
    protected final List<Side> outputs;

    protected final SignalType signalType;

    protected StaticIOModeHandler(List<Side> inputs, List<Side> outputs, SignalType signalType){
        this.inputs = inputs;
        this.outputs = outputs;

        this.signalType = signalType;
    }

    public List<Side> getInputSides(int mode){
        return this.inputs;
    }

    @Override
    public List<Side> getOutputSides(int mode) {
        return this.outputs;
    }

    @Override
    public SignalType getInputType(Side side) {
        return this.signalType;
    }

    @Override
    public SignalType getOutputType(Side side) {
        return this.signalType;
    }
}
