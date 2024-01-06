package com.chyzman.electromechanics.logic.api;

import com.chyzman.electromechanics.logic.api.state.GateContext;

public interface GateTickEvent {

    default boolean shouldTick(GateContext context){
        return true;
    }

    void onTick(GateContext context);
}
