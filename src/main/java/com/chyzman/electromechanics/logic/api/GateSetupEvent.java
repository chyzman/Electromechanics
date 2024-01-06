package com.chyzman.electromechanics.logic.api;

import com.chyzman.electromechanics.logic.api.state.GateStateStorage;

public interface GateSetupEvent {

    void setup(GateStateStorage storage);
}
