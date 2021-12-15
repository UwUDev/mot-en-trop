package me.uwu.motentrop.struct.game;

import lombok.*;
import me.uwu.motentrop.api.struct.game.Direction;
import me.uwu.motentrop.api.struct.game.IBoard;
import me.uwu.motentrop.api.struct.game.IWordBox;
import me.uwu.motentrop.api.struct.math.Vec2i;
import me.uwu.motentrop.api.struct.game.Letter;

import java.util.List;

public @Data
class WordBox implements IWordBox {
    private final Vec2i startPosition;
    private final Direction direction;
    private final int size;

    private boolean empty;
    private char[] _content;


    @Override
    public void setContent(IBoard board, char[] provided) {
        assert provided.length == size;
        this._content = provided;

        List<Vec2i> positions = getPositions();
        for (int i = 0; i < size; i++) {
            board.putLetter(positions.get(i), this._content == null ? ' ' : this._content[i]);
        }
    }

    @Override
    public char[] getContent(IBoard board) {
        char[] content = new char[size];

        this.empty = true;

        List<Vec2i> positions = getPositions();
        for (int i = 0; i < size; i++) {
            Letter letter = board.getLetterAt(positions.get(i));
            content[i] = letter == null ? ' ' : letter.getCharacter();

            if (content[i] != ' ') {
                empty = false;
            }
        }
        return content;
    }

    private boolean fits(char[] provided) {
        if (this._content == null) return true;
        assert this._content.length != provided.length;

        for (int i = 0; i < provided.length; i++) {
            char curr = this._content[i];
            if (curr == ' ' || curr == provided[i]) {
                continue;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean canAccept(char[] provided) {
        return provided.length == size && fits(provided);
    }

    @Override
    public boolean isFilled() {
        return this._content != null && this._content.length == size;
    }
}
