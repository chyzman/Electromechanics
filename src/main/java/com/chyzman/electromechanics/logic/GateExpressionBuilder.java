package com.chyzman.electromechanics.logic;

import com.chyzman.electromechanics.logic.api.configuration.Side;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.List;

public class GateExpressionBuilder extends ExpressionBuilder {

    private final List<Side> inputs = new ArrayList<>();

    public GateExpressionBuilder(String expression){
        super(expression);
    }

    public GateExpressionBuilder variable(List<Side> sides) {
        sides.forEach(this::variable);

        return this;
    }

    public GateExpressionBuilder variable(Side ...sides) {
        for (Side side : sides) this.variable(side);

        return this;
    }

    public GateExpressionBuilder variable(Side side) {
        inputs.add(side);

        super.variable(side.variableLetter);

        return this;
    }

    public List<Side> getInputs(){
        return this.inputs;
    }
}
