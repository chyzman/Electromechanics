package com.chyzman.chyzyLogistics.logic.api.mode;

import com.chyzman.chyzyLogistics.logic.api.Side;
import com.chyzman.chyzyLogistics.logic.api.SignalType;

public abstract class StaticSignalTypeModeHandler implements ModeHandler {

    protected final SignalType signalType;

    protected StaticSignalTypeModeHandler(SignalType signalType){
        this.signalType = signalType;
    }

    @Override
    public SignalType getInputType(int mode, Side side) {
        return this.signalType;
    }

    @Override
    public SignalType getOutputType(int mode, Side side) {
        return this.signalType;
    }
}
