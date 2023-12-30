package com.chyzman.chyzyLogistics.logic.api;

import net.minecraft.util.math.MathHelper;

public enum SignalType {
    DIGITAL,
    ANALOG;

    public int evaluateOutput(int power) {
        var value = switch (this) {
            case DIGITAL -> (power > 0) ? 15 : 0;
            case ANALOG -> power;
        };

        return MathHelper.clamp(value, 0, 15);
    }

    public int evaluateInput(int power) {
        var value = switch (this) {
            case DIGITAL -> (power > 0) ? 1 : 0;
            case ANALOG -> power;
        };

        return MathHelper.clamp(value, 0, 15);
    }
}
