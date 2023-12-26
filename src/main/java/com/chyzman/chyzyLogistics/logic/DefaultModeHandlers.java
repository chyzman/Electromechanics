package com.chyzman.chyzyLogistics.logic;

import com.chyzman.chyzyLogistics.logic.mode.ModeHandler;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;

public class DefaultModeHandlers {

    public static <H extends ModeHandler> H monoToMono(SignalType type, TriFunction<List<Side>, List<Side>, SignalType, H> constructor){
        return constructor.apply(List.of(Side.BACK), List.of(Side.FRONT), type);
    }

    public static <H extends ModeHandler> H monoToTri(SignalType type, TriFunction<List<Side>, List<Side>, SignalType, H> constructor){
        return constructor.apply(List.of(Side.BACK), List.of(Side.LEFT, Side.FRONT, Side.RIGHT), type);
    }

    public static <H extends ModeHandler> H biToMono(SignalType type, TriFunction<List<Side>, List<Side>, SignalType, H> constructor){
        return constructor.apply(List.of(Side.LEFT, Side.RIGHT), List.of(Side.FRONT), type);
    }

    public static <H extends ModeHandler> H triToMono(SignalType type, TriFunction<List<Side>, List<Side>, SignalType, H> constructor){
        return constructor.apply(List.of(Side.LEFT, Side.BACK, Side.RIGHT), List.of(Side.FRONT), type);
    }

    public static <H extends ModeHandler> H noneToQuad(SignalType type, TriFunction<List<Side>, List<Side>, SignalType, H> constructor){
        return constructor.apply(List.of(), List.of(Side.LEFT, Side.BACK, Side.RIGHT, Side.FRONT), type);
    }
}
