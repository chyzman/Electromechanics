package com.chyzman.chyzyLogistics.logic.api;

public enum Side {
    FRONT("f"),
    RIGHT("r"),
    BACK("b"),
    LEFT("l");

    public final String variableLetter;

    Side(String variableLetter){
        this.variableLetter = variableLetter;
    }
}
