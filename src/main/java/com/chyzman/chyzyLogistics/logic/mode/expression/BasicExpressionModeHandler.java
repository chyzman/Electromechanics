package com.chyzman.chyzyLogistics.logic.mode.expression;

import com.chyzman.chyzyLogistics.logic.Side;
import com.chyzman.chyzyLogistics.logic.SignalType;
import com.chyzman.chyzyLogistics.logic.mode.StaticIOModeHandler;
import com.chyzman.chyzyLogistics.mixin.ExpressionBuilderAccessor;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;

public final class BasicExpressionModeHandler extends StaticIOModeHandler implements ExpressionModeHandler {

    private final List<Expression> expressions = new ArrayList<>();

    public BasicExpressionModeHandler(List<Side> inputs, List<Side> outputs, SignalType signalType){
        super(inputs, outputs, signalType);
    }

    public BasicExpressionModeHandler add(ExpressionBuilder builder){
        expressions.add(builder.build());

        return this;
    }

    @Override
    public Expression getExp4jExpression(int mode) {
        return expressions.get(mode);
    }

    public int totalModes(){
        return expressions.size();
    }


}
