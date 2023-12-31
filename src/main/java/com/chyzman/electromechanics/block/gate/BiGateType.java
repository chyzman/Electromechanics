package com.chyzman.electromechanics.block.gate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public record BiGateType(String type, BiFunction<Boolean, Boolean, Boolean> logicFunc) {

    private static final Map<String, BiGateType> TYPES = new HashMap<>();

    public BiGateType {
        if(TYPES.containsKey(type)){
            throw new IllegalStateException("Unable to register a given type on creation due to it having a duplicate type taken! [Type: " + type + " ]");
        }

        TYPES.put(type, this);
    }

    public static final BiGateType AND = new BiGateType("and", (right, left) -> right && left);
    public static final BiGateType OR = new BiGateType("or", (right, left) -> right || left);
    public static final BiGateType XOR = new BiGateType("xor", (right, left) -> right ^ left);

    public static BiGateType getType(String type){
        return TYPES.getOrDefault(type, AND);
    }
}
