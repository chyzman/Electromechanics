package com.chyzman.chyzyLogistics.util;

import com.chyzman.chyzyLogistics.ChyzyLogistics;
import net.minecraft.util.Identifier;

public class ChyzyLogisticsRegistryHelper {
    public static Identifier id(String path) {
        return new Identifier(ChyzyLogistics.MODID, path);
    }

}