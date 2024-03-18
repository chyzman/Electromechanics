package com.chyzman.electromechanics.util;

import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.endec.util.MapCarrier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ImplMapCarrier<M extends MapCarrier> implements MapCarrier {

    private M mapCarrier;

    private Consumer<M> onChange = m -> {};

    public ImplMapCarrier(M mapCarrier){
        this.mapCarrier = mapCarrier;
    }

    public ImplMapCarrier<M> onChange(Consumer<M> onChange){
        this.onChange = onChange;

        return this;
    }

    public M getMapCarrier(){
        return this.mapCarrier;
    }

    public ImplMapCarrier<M> setMapCarrier(M value){
        this.mapCarrier = value;

        return this;
    }

    @Override
    public <T> T getWithErrors(@NotNull KeyedEndec<T> key) {
        return this.mapCarrier.getWithErrors(key);
    }

    @Override
    public <T> void put(@NotNull KeyedEndec<T> key, @NotNull T value) {
        this.mapCarrier.put(key, value);

        this.onChange.accept(this.mapCarrier);
    }

    @Override
    public <T> void delete(@NotNull KeyedEndec<T> key) {
        this.mapCarrier.delete(key);

        this.onChange.accept(this.mapCarrier);
    }

    @Override
    public <T> boolean has(@NotNull KeyedEndec<T> key) {
        return this.mapCarrier.has(key);
    }
}
