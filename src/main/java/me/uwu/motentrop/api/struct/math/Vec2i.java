package me.uwu.motentrop.api.struct.math;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

public @Data
class Vec2i {
    public static final transient Vec2i ORIGIN = new Vec2i(0, 0);

    private final int x;
    private final int y;

    @NotNull
    public Vec2i offset(int x, int y) {
        return new Vec2i(this.x + x, this.y + y);
    }

    @NotNull
    public Vec2i offset(int a) {
        return this.offset(a, a);
    }

    @NotNull
    public Vec2i offset(Vec2i offsetVector) {
        return this.offset(offsetVector.x, offsetVector.y);
    }

    @NotNull
    public Vec2i multiply(int x, int y) {
        return new Vec2i(this.x * x, this.y * y);
    }

    @NotNull
    public Vec2i multiply(int a) {
        return this.multiply(a, a);
    }

    @NotNull
    public Vec2i multiply(Vec2i multiplyVector) {
        return this.multiply(multiplyVector.x, multiplyVector.y);
    }

    public static Vec2i valueOf(int... values) {
        assert values.length == 2;
        return new Vec2i(values[0], values[1]);
    }
}
