package com.chyzman.chyzyLogistics.logic;

import com.chyzman.chyzyLogistics.ChyzyLogistics;
import com.chyzman.chyzyLogistics.logic.mode.BasicLogicModeHandler;
import com.chyzman.chyzyLogistics.logic.mode.ModeHandler;
import com.chyzman.chyzyLogistics.logic.mode.expression.BasicExpressionModeHandler;
import com.chyzman.chyzyLogistics.logic.mode.expression.GateExpressionBuilder;
import com.chyzman.chyzyLogistics.mixin.ExpressionBuilderAccessor;
import net.minecraft.util.Identifier;
import net.objecthunter.exp4j.operator.Operator;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DigitalGateHandlers {

    private static final Operator booleanInversion = new Operator("!", 1, true, 1) {
        @Override
        public double apply(double... args) {
            return BooleanUtils.toInteger(args[0] != 1);
        }
    };

    public static final GateHandler BASE = monoGate(ChyzyLogistics.id("base"), (input) -> input);

    public static final GateHandler AND = biGate(ChyzyLogistics.id("and"), (right, left) -> (right * left));
    public static final GateHandler OR = biGate(ChyzyLogistics.id("or"), (right, left) -> (right + left));
    public static final GateHandler XOR = biGate(ChyzyLogistics.id("xor"), (right, left) -> (right ^ left));

    public static final GateHandler TRIPLE_AND = triGateExpression(ChyzyLogistics.id("tri_and"), "r*b*l");
    public static final GateHandler TRIPLE_OR = triGate(ChyzyLogistics.id("tri_or"), (right, middle, left) -> (right + middle + left));
    public static final GateHandler AND_THEN_OR = triGate(ChyzyLogistics.id("and_then_or"), (right, middle, left) -> ((right * middle) + left));
    public static final GateHandler OR_THEN_AND = triGate(ChyzyLogistics.id("or_then_and"), (right, middle, left) -> ((right + middle) * left));

    public static GateHandler monoGate(Identifier id, Function<Integer, Integer> func){
        return GateHandler.of(id,
                DefaultModeHandlers.monoToMono(SignalType.DIGITAL, BasicLogicModeHandler::new),
                digitalInversion((context, integers) -> func.apply(integers[0])));
    }

    public static GateHandler biGate(Identifier id, BiFunction<Integer, Integer, Integer> func){
        return GateHandler.of(id,
                DefaultModeHandlers.biToMono(SignalType.DIGITAL, BasicLogicModeHandler::new),
                digitalInversion((context, integers) -> func.apply(integers[0], integers[1])));
    }

    public static GateHandler triGate(Identifier id, TriFunction<Integer, Integer, Integer, Integer> func){
        return GateHandler.of(id,
                DefaultModeHandlers.triToMono(SignalType.DIGITAL, BasicLogicModeHandler::new),
                digitalInversion((context, integers) -> func.apply(integers[0], integers[1], integers[2])));
    }

    private static Consumer<BasicLogicModeHandler> digitalInversion(LogicExpression logicExpression){
        LogicExpression invertedBuilder = (context, integers) -> BooleanUtils.toInteger(logicExpression.apply(context, integers) == 0);

        return handler -> handler.add(logicExpression)
                .add(invertedBuilder);
    }

    //--

    public static GateHandler monoGateExpression(Identifier id, String expression){
        return GateHandler.of(id,
                DefaultModeHandlers.monoToMono(SignalType.DIGITAL, BasicExpressionModeHandler::new),
                digitalExpressionInversion(new GateExpressionBuilder(expression).variable(Side.BACK)));
    }

    public static GateHandler biGateExpression(Identifier id, String expression){
        return GateHandler.of(id,
                DefaultModeHandlers.biToMono(SignalType.DIGITAL, BasicExpressionModeHandler::new),
                digitalExpressionInversion(new GateExpressionBuilder(expression).variable(Side.LEFT, Side.RIGHT)));
    }

    public static GateHandler triGateExpression(Identifier id, String expression){
        return GateHandler.of(id,
                DefaultModeHandlers.triToMono(SignalType.DIGITAL, BasicExpressionModeHandler::new),
                digitalExpressionInversion(new GateExpressionBuilder(expression).variable(Side.LEFT, Side.BACK, Side.RIGHT)));
    }

    private static Consumer<BasicExpressionModeHandler> digitalExpressionInversion(GateExpressionBuilder builder){
        String expression = ((ExpressionBuilderAccessor) builder).chyz$getExpression();

        var invertedBuilder = new GateExpressionBuilder("!(" + expression + ")")
                .variable(builder.getInputs())
                .operator(booleanInversion);

        return handler -> handler.add(builder)
                .add(invertedBuilder);
    }

    //--
}
