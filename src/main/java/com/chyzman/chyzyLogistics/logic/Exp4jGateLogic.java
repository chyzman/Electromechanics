package com.chyzman.chyzyLogistics.logic;

import com.chyzman.chyzyLogistics.logic.api.GateContext;
import com.chyzman.chyzyLogistics.logic.api.GateLogicFunction;
import com.chyzman.chyzyLogistics.logic.api.Side;
import net.objecthunter.exp4j.Expression;

import java.util.List;

public class Exp4jGateLogic implements GateLogicFunction {

    private final List<Side> inputs;
    private final Expression expression;

    public Exp4jGateLogic(List<Side> inputs, Expression expression){
        this.expression = expression;
        this.inputs = inputs;
    }

    @Override
    public Integer apply(GateContext context, Integer... integers) {
        return evaluate(context, integers);
    }

    private int evaluate(GateContext context, Integer... integers) {
        for (int i = 0; i < integers.length; i++) {
            var input = integers[i];
            var side = inputs.get(i);

            expression.setVariable(side.variableLetter, input);
        }

        return (int) expression.evaluate();
    }
}
