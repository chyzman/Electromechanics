package com.chyzman.chyzyLogistics.logic.mode.expression;

import com.chyzman.chyzyLogistics.logic.LogicExpression;
import com.chyzman.chyzyLogistics.logic.mode.ModeHandler;
import net.objecthunter.exp4j.Expression;

public interface ExpressionModeHandler extends ModeHandler {

    Expression getExp4jExpression(int mode);

    default LogicExpression getExpression(int mode){
        return (context, integers) -> {
            var expression = getExp4jExpression(mode);
            var inputSides = this.getInputSides(mode);

            for (int i = 0; i < integers.length; i++) {
                var input = integers[i];
                var side = inputSides.get(i);

                expression.setVariable(side.variableLetter, input);
            }

            return (int) expression.evaluate();
        };
    }
}
