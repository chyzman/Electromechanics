package com.chyzman.electromechanics.util;

import com.chyzman.electromechanics.ElectromechanicsLogistics;
import net.minecraft.util.Identifier;

public class ChyzyLogisticsRegistryHelper {
    public static Identifier id(String path) {
        return new Identifier(ElectromechanicsLogistics.MODID, path);
    }

}