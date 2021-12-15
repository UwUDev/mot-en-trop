package me.uwu.motentrop.api.struct.game;

import lombok.RequiredArgsConstructor;
import me.uwu.motentrop.api.struct.math.Vec2i;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public enum Direction {
    HORIZONTAL(1, 0),
    VERTICAL(0, 1);

    private final int xOffsetVector;
    private final int yOffsetVector;

    @NotNull
    public Vec2i getOffsetVector() {
        return new Vec2i(xOffsetVector, yOffsetVector);
    }
}
