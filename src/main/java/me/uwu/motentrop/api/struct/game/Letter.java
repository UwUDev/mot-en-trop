package me.uwu.motentrop.api.struct.game;

import lombok.Data;
import me.uwu.motentrop.api.struct.math.Vec2i;

public @Data
class Letter {
    private final Vec2i position;
    private final char character;
}
