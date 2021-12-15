package me.uwu.motentrop.api.struct.game;

import lombok.Data;

import java.util.Set;

public @Data
class BoardState {
    private final Set<Letter> letterState;
    private final Set<String> usedWords;
}