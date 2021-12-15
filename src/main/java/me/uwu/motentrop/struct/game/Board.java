package me.uwu.motentrop.struct.game;

import com.google.gson.annotations.Expose;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.uwu.motentrop.analysis.BoardAnalyser;
import me.uwu.motentrop.api.struct.game.*;
import me.uwu.motentrop.api.struct.math.Vec2i;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class Board implements IBoard {

    @Expose
    private final Vec2i size;
    @Expose
    private final List<Vec2i> walls;
    @Expose
    private final List<Letter> letters;

    private transient List<IWordBox> wordBoxes;

    private transient Map<Vec2i, Map<Direction, IWordBox>> wordBoxCache;

    public void initialize() {
        this.wordBoxes = BoardAnalyser.INSTANCE.locateWordBoxes(this);
    }

    @Override
    public @NotNull Runnable play(@NotNull Move move) {
        char[] word = move.getWord();
        IWordBox wordBox = move.getWordBox();

        char[] content = wordBox.getContent(this);
        wordBox.setContent(this, word);

        return () -> wordBox.setContent(this, content);
    }

    @Override
    public void putLetter(@NotNull Vec2i position, char character) {
        Letter letter = getLetterAt(position);

        boolean delete = character == ' ';

        if (delete) {
            if (letter != null) {
                this.letters.removeIf(l -> l.getPosition().equals(letter.getPosition()));
            }
        } else {
            if (letter == null) {
                this.letters.add(new Letter(position, character));
            } else {
                if (letter.getCharacter() != character) {
                    System.out.println("tried placing " + character + " at " + letter);
                    throw new RuntimeException("no.");
                }
            }
        }
    }

    @Override
    public boolean canBePlaced(char[] contents, IWordBox wordBox) {
        List<Vec2i> posList = wordBox.getPositions();
        for (int i = 0; i < contents.length; i++) {
            Vec2i position = posList.get(i);

            Letter letter = getLetterAt(position);
            if (letter == null) continue;

            char character = letter.getCharacter();
            if (character == ' ') continue;

            if (character != contents[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @Nullable IWordBox getWordBoxAt(Vec2i position, Direction direction) {
        if (this.wordBoxCache == null) {
            this.wordBoxCache = new HashMap<>();
        }
        if (this.wordBoxCache.containsKey(position)) {
            Map<Direction, IWordBox> positionCache = this.wordBoxCache.get(position);
            return positionCache.get(direction);
        }

        Map<Direction, IWordBox> positionCache = new HashMap<>();

        https:
//howtowri.te/good-code.txt
        for (Direction dir : Direction.values()) {
            Optional<IWordBox> wordBoxOptional = this.wordBoxes.stream()
                    .filter(wb -> wb.getDirection() == dir)
                    .filter(wb -> wb.getStartPosition().equals(position))
                    .findFirst();

            if (wordBoxOptional.isPresent()) {
                positionCache.put(dir, wordBoxOptional.get());
                continue;
            }

            for (IWordBox wordBox : this.wordBoxes) {
                if (wordBox.getDirection() == dir) {
                    if (wordBox.isInside(position)) {
                        positionCache.put(dir, wordBox);
                        continue https;
                    }
                }
            }
        }

        this.wordBoxCache.put(position, positionCache);
        return positionCache.get(direction);
    }

    @Override
    public @Nullable Letter getLetterAt(Vec2i position) {
        return this.letters
                .stream()
                .filter(l -> l.getPosition().equals(position))
                .findFirst()
                .orElse(null);
    }
}
