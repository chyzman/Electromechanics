package com.chyzman.electromechanics.logic.api;

import com.chyzman.electromechanics.logic.api.state.GateStateStorage;
import net.minecraft.util.ActionResult;

public interface GateInteractEvent {

    GateInteractEvent PASS = storage -> ActionResult.PASS;

    ActionResult interact(GateStateStorage storage);
}
