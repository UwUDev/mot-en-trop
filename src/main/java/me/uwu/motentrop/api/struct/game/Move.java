package me.uwu.motentrop.api.struct.game;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import me.uwu.motentrop.api.struct.math.Tuple;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Move extends Tuple<char[], IWordBox> {
    public Move(char[] word, @NonNull IWordBox wordBox) {
        super(word, wordBox);
    }

    public char[] getWord() {
        return getFirst();
    }

    @NotNull
    public IWordBox getWordBox() {
        return getSecond();
    }
}
