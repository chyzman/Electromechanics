package com.chyzman.chyzyLogistics.util;

import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.StructField;
import net.minecraft.util.DyeColor;

public interface Colored {

    Endec<DyeColor> DYE_COLOR_ENDEC = Endec.forEnum(DyeColor.class);

    DyeColor getColor();
}
