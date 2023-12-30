package com.chyzman.chyzyLogistics.logic.api;

import com.chyzman.chyzyLogistics.logic.IOConfigurations;
import com.chyzman.chyzyLogistics.logic.api.Side;

import java.util.List;

public record IOConfiguration(List<Side> inputs, List<Side> outputs) {
    public IOConfiguration(Side input, Side output){
        this(List.of(input), List.of(output));
    }
}
