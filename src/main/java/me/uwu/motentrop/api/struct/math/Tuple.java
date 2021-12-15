package me.uwu.motentrop.api.struct.math;

import lombok.Data;

public @Data
class Tuple<K, V> {
    private final K first;
    private final V second;
}
