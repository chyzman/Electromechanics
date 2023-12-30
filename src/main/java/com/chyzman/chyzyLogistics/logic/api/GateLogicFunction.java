package com.chyzman.chyzyLogistics.logic.api;

import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface GateLogicFunction {
    Integer apply(GateContext context, Integer... integers);

    static GateLogicFunction of(Function<Integer, Integer> func){
        return (context, integers) -> func.apply(integers[0]);
    }

    static GateLogicFunction of(BiFunction<Integer, Integer, Integer> func){
        return (context, integers) -> func.apply(integers[0], integers[1]);
    }

    static GateLogicFunction of(TriFunction<Integer, Integer, Integer, Integer> func){
        return (context, integers) -> func.apply(integers[0], integers[1], integers[2]);
    }
}
