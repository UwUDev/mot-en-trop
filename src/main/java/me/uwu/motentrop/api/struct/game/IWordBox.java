package me.uwu.motentrop.api.struct.game;

import me.uwu.motentrop.api.struct.math.Vec2i;

import java.util.ArrayList;
import java.util.List;

public interface IWordBox {
    Vec2i getStartPosition();

    Direction getDirection();

    int getSize();

    default List<Vec2i> getPositions() {
        List<Vec2i> positions = new ArrayList<>();

        Vec2i startPosition = getStartPosition();
        Vec2i directionOffsetVector = getDirection().getOffsetVector();

        for (int i = 0; i < getSize(); i++) {
            Vec2i position = startPosition.offset(directionOffsetVector.multiply(i));
            positions.add(position);
        }

        return positions;
    }

    default boolean isInside(Vec2i position) {
        return getPositions().stream().anyMatch(position::equals);
    }

    /**
     * @param board The {@link IBoard Board} instance to check on.
     * @return The current content of this {@link IWordBox WordBox}.
     */
    char[] getContent(IBoard board);

    /**
     * @param board   The {@link IBoard Board} instance to check on.
     * @param content The content to be written.
     */
    void setContent(IBoard board, char[] content);

    /**
     * @param content The text to override this WordBox's content
     * @return wheather or not the WordBox can accept the provided content
     */
    boolean canAccept(char[] content);

    /**
     * Implementation details: check the internal stored `content`
     * instead of the board's contents.
     *
     * @return if this WordBox contains a full word
     */
    boolean isFilled();
}
