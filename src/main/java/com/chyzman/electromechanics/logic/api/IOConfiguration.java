package com.chyzman.electromechanics.logic.api;

import java.util.List;

public record IOConfiguration(List<Side> inputs, List<Side> outputs) {
    public IOConfiguration(Side input, Side output){
        this(List.of(input), List.of(output));
    }
}
