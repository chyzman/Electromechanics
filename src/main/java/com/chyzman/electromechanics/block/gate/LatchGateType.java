package com.chyzman.electromechanics.block.gate;

import org.apache.commons.lang3.function.TriFunction;

import java.util.HashMap;
import java.util.Map;

public record LatchGateType(String type, TriFunction<Boolean, Boolean, Boolean, Boolean> logicFunc) {

    private static final Map<String, LatchGateType> TYPES = new HashMap<>();

    public LatchGateType {
        if(TYPES.containsKey(type)){
            throw new IllegalStateException("Unable to register a given type on creation due to it having a duplicate type taken! [Type: " + type + " ]");
        }

        TYPES.put(type, this);
    }

    public static final LatchGateType T_FLIP_FLOP = new LatchGateType("T_FLIP_FLOP", (wasPowered, beingPowered, isPowered) -> (!wasPowered && beingPowered) != isPowered);

    public static LatchGateType getType(String type){
        return TYPES.getOrDefault(type, T_FLIP_FLOP);
    }

}