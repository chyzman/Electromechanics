package com.chyzman.electromechanics.logic.api;

import com.chyzman.electromechanics.logic.api.state.GateContext;
import net.minecraft.util.ActionResult;

public interface GateTickEvent {

    default boolean shouldTick(GateContext context){
        return true;
    }

    ActionResult onTick(GateHandler handler, GateContext context);
}
