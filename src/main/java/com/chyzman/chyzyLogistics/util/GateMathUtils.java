package com.chyzman.chyzyLogistics.util;

import com.chyzman.chyzyLogistics.logic.api.Side;

import java.util.Map;

public class GateMathUtils {

    public static boolean isOutputtingPower(Map<Side, Integer> outputData){
        return outputData.values().stream().anyMatch(integer -> integer > 0);
    }

    public static int getOutputPower(Map<Side, Integer> outputData, Side side){
        if(!outputData.containsKey(side)) return 0;

        return outputData.get(side);
    }

    public boolean isOutputtingPower(Map<Side, Integer> outputData, Side side){
        return getOutputPower(outputData, side) > 0;
    }
}
