package com.chyzman.chyzyLogistics.logic.api;

import com.chyzman.chyzyLogistics.block.gate.GateStateStorage;
import net.minecraft.util.ActionResult;

public interface GateInteractEvent {
    ActionResult interact(GateStateStorage storage);
}
