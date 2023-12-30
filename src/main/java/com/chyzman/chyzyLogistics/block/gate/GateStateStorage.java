package com.chyzman.chyzyLogistics.block.gate;

import com.chyzman.chyzyLogistics.logic.api.Side;

import java.util.Map;

public interface GateStateStorage {

    void setOutputPower(Side side, int power);

    void setInputPower(Side side, int power);

    boolean hasChangesOccurred();

    // --

    int getInputPower(Side side);

    boolean isBeingPowered(Side side);

    int getOutputPower(Side side);

    boolean isOutputtingPower(Side side);

    boolean isOutputtingPower();

    //--

    void setMode(int mode);

    int getMode();

    //--


}
