package com.chyzman.electromechanics.logic.api;

import com.chyzman.electromechanics.logic.api.state.GateStateStorage;
import net.minecraft.util.ActionResult;

public interface GateInteractEvent {
    ActionResult interact(GateStateStorage storage);
}
