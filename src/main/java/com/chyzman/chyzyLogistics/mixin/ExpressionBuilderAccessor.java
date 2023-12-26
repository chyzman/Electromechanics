package com.chyzman.chyzyLogistics.mixin;

import net.objecthunter.exp4j.ExpressionBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ExpressionBuilder.class, remap = false)
public interface ExpressionBuilderAccessor {
    @Accessor("expression") String chyz$getExpression();

    @Accessor("expression") void chyz$setExpression(String expression);
}
