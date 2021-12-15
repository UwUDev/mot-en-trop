package me.uwu.motentrop.api.struct.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardScore {
    SOLVED(100),
    FAILED(-100),
    UNDETERMINED(0);

    private final int score;

    public boolean isUndetermined() {
        return this == UNDETERMINED;
    }
}
