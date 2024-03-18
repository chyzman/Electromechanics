package com.chyzman.electromechanics.util;

import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.endec.util.MapCarrier;
import org.jetbrains.annotations.NotNull;

public interface DelagatingMapCarrier extends MapCarrier {

    <M extends MapCarrier> M getMap();

    @Override
    default <T> T getWithErrors(@NotNull KeyedEndec<T> key) {
        return getMap().getWithErrors(key);
    }

    @Override
    default <T> void put(@NotNull KeyedEndec<T> key, @NotNull T value) {
        getMap().put(key, value);
    }

    @Override
    default <T> void delete(@NotNull KeyedEndec<T> key) {
        getMap().delete(key);
    }

    @Override
    default <T> boolean has(@NotNull KeyedEndec<T> key) {
        return getMap().has(key);
    }
}
