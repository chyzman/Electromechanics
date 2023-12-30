package com.chyzman.chyzyLogistics.logic.api;

public enum Side {
    FRONT("f"), // 0
    RIGHT("r"), // 1
    BACK("b"),  // 2
    LEFT("l");  // 3

    public final String variableLetter;

    Side(String variableLetter){
        this.variableLetter = variableLetter;
    }

    public Side getOpposite(){
        return Side.values()[(this.ordinal() + 2) % 4];
    }
}
