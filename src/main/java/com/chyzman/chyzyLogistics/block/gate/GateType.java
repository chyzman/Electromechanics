package com.chyzman.chyzyLogistics.block.gate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public record GateType(String type, Function<Boolean, Boolean> logicFunc) {

    private static final Map<String, GateType> TYPES = new HashMap<>();

    public GateType {
        if(TYPES.containsKey(type)){
            throw new IllegalStateException("Unable to register a given type on creation due to it having a duplicate type taken! [Type: " + type + " ]");
        }

        TYPES.put(type, this);
    }

    public static final GateType GATE = new GateType("gate", aBoolean -> aBoolean);

    public static GateType getType(String type){
        return TYPES.getOrDefault(type, GATE);
    }

}
