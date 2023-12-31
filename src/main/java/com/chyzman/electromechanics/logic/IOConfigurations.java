package com.chyzman.electromechanics.logic;

import com.chyzman.electromechanics.logic.api.IOConfiguration;
import com.chyzman.electromechanics.logic.api.Side;

import java.util.List;

public class IOConfigurations {

    public static IOConfiguration MONO_TO_MONO = new IOConfiguration(List.of(Side.BACK), List.of(Side.FRONT));

    public static IOConfiguration MONO_TO_TRI = new IOConfiguration(List.of(Side.BACK), List.of(Side.LEFT, Side.FRONT, Side.RIGHT));

    public static IOConfiguration BI_TO_MONO = new IOConfiguration(List.of(Side.LEFT, Side.RIGHT), List.of(Side.FRONT));

    public static IOConfiguration TRI_TO_MONO = new IOConfiguration(List.of(Side.LEFT, Side.BACK, Side.RIGHT), List.of(Side.FRONT));

    public static IOConfiguration NONE_TO_QUAD = new IOConfiguration(List.of(), List.of(Side.LEFT, Side.BACK, Side.RIGHT, Side.FRONT));

}
