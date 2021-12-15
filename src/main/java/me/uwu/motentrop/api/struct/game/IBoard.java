package me.uwu.motentrop.api.struct.game;

import me.uwu.motentrop.api.struct.math.Vec2i;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface IBoard {

    @NotNull Runnable play(@NotNull Move move);

    default void withPlay(@NotNull Move move, @NotNull Consumer<IBoard> boardConsumer) {
        Runnable cancel = play(move);
        boardConsumer.accept(this);
        cancel.run();
    }

    default void printBoard(Consumer<String> stringConsumer) {
        Vec2i size = getSize();

        StringBuilder stringBuilder = new StringBuilder();
        for (int y = 0; y < size.getY(); y++) {
            stringBuilder.append("\n");
            for (int x = 0; x < size.getX(); x++) {
                if (x != 0) {
                    stringBuilder.append("|");
                }

                Vec2i pos = Vec2i.valueOf(x, y);
                Letter letter = getLetterAt(pos);
                boolean wall = getWalls().stream().anyMatch(pos::equals);
                stringBuilder.append(wall ? "█" : (letter == null ? ' ' : letter.getCharacter()));
            }
        }

        stringConsumer.accept(stringBuilder.toString());
    }

    /**
     * Places a letter at the provided position.
     *
     * @param position The position to place to.
     * @param letter   The letter to place.
     */
    void putLetter(@NotNull Vec2i position, char letter);

    /**
     * Gets the {@link IWordBox} at the specified position and direction.
     *
     * @param position  The position to check for
     * @param direction The direction to check for
     * @return the wordbox at the provided position
     */
    @Nullable IWordBox getWordBoxAt(Vec2i position, Direction direction);

    /**
     * Gets the {@link Letter} at the specified position.
     *
     * @param position The position to check for
     * @return the letter at the provided position
     */
    @Nullable Letter getLetterAt(Vec2i position);

    boolean canBePlaced(char[] content, IWordBox wordBox);

    /**
     * @return the size vector of the board
     */
    @NotNull Vec2i getSize();

    /**
     * @return a {@link Set} of the positions of the board's walls
     */
    @NotNull List<Vec2i> getWalls();

    /**
     * @return a {@link Set} of {@link IWordBox WordBox}es
     */
    @NotNull List<IWordBox> getWordBoxes();

    /**
     * @return a {@link Set} of {@link Letter Letter}s
     */
    @NotNull List<Letter> getLetters();
}
