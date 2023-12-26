package com.chyzman.chyzyLogistics.logic;

public interface LogicExpression {
    Integer apply(GateContext context, Integer... integers);
}
