package com.chyzman.electromechanics.util;

import io.wispforest.owo.serialization.Endec;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EndecUtils {

    public static Endec<Integer> POSITIVE_INT = Endec.INT.xmap(integer -> Math.max(0, integer), integer -> integer);

    public static <K, V> Endec<Map<K, V>> mapOf(Endec<V> valueEndec, Function<String, K> to, Function<K, String> from) {
        return Endec.of((serializer, map) -> {
            try (var mapState = serializer.map(valueEndec, map.size())) {
                map.forEach((k, v) -> mapState.entry(from.apply(k), v));
            }
        }, deserializer -> {
            var mapState = deserializer.map(valueEndec);

            var map = new HashMap<K, V>(mapState.estimatedSize());
            mapState.forEachRemaining(entry -> map.put(to.apply(entry.getKey()), entry.getValue()));

            return map;
        });
    }
}
