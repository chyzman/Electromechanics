package com.chyzman.electromechanics.util;

import com.chyzman.electromechanics.Electromechanics;
import net.minecraft.util.Identifier;

public class ChyzyLogisticsRegistryHelper {
    public static Identifier id(String path) {
        return new Identifier(Electromechanics.MODID, path);
    }

}