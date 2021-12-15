package me.uwu.motentrop.analysis;

import lombok.extern.log4j.Log4j2;
import me.uwu.motentrop.api.analysis.IBoardPopulator;
import me.uwu.motentrop.api.struct.game.Direction;
import me.uwu.motentrop.api.struct.game.IBoard;
import me.uwu.motentrop.api.struct.game.IWordBox;
import me.uwu.motentrop.api.struct.math.Vec2i;
import me.uwu.motentrop.struct.game.WordBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public enum BoardAnalyser implements IBoardPopulator {
    INSTANCE;

    BoardAnalyser() {
    }

    @Override
    public List<IWordBox> locateWordBoxes(IBoard board) {
        Vec2i boardSize = board.getSize();
        List<Vec2i> obstructions = board.getWalls();

        List<IWordBox> boxes = new ArrayList<>();
        for (int y = 0; y < boardSize.getY(); y++) {
            final int _y = y;
            List<Vec2i> lineObstructions = obstructions.stream()
                    .filter(v -> v.getY() == _y)
                    .collect(Collectors.toList());

            if (lineObstructions.isEmpty()) {
                boxes.add(new WordBox(new Vec2i(0, y), Direction.HORIZONTAL, boardSize.getX()));
                continue;
            }

            Vec2i lastObstruction = null;
            for (Vec2i obstruction : lineObstructions) {
                if (lastObstruction == null) {
                    if (obstruction.getX() > 1) {
                        boxes.add(new WordBox(new Vec2i(0, y), Direction.HORIZONTAL, obstruction.getX()));
                    }
                } else {
                    int size = (obstruction.getX() - lastObstruction.getX()) - 1;
                    if (size > 1) {
                        boxes.add(new WordBox(new Vec2i(lastObstruction.getX() + 1, y), Direction.HORIZONTAL, size));
                    }
                }

                lastObstruction = obstruction;
            }

            int size = (boardSize.getX() - 1) - lastObstruction.getX();
            if (size > 1) {
                boxes.add(new WordBox(new Vec2i(lastObstruction.getX() + 1, y), Direction.HORIZONTAL, size));
            }
        }

        for (int x = 0; x < boardSize.getX(); x++) {
            final int _x = x;
            List<Vec2i> columnObstructions = obstructions.stream()
                    .filter(v -> v.getX() == _x)
                    .collect(Collectors.toList());

            if (columnObstructions.isEmpty()) {
                boxes.add(new WordBox(new Vec2i(x, 0), Direction.VERTICAL, boardSize.getY()));
                continue;
            }

            Vec2i lastObstruction = null;
            for (Vec2i obstruction : columnObstructions) {
                if (lastObstruction == null) {
                    if (obstruction.getY() > 1) {
                        boxes.add(new WordBox(new Vec2i(x, 0), Direction.VERTICAL, obstruction.getY()));
                    }
                } else {
                    int size = (obstruction.getY() - lastObstruction.getY()) - 1;
                    if (size > 1) {
                        boxes.add(new WordBox(new Vec2i(x, lastObstruction.getY() + 1), Direction.VERTICAL, size));
                    }
                }

                lastObstruction = obstruction;
            }

            int size = (boardSize.getY() - 1) - lastObstruction.getY();
            if (size > 1) {
                boxes.add(new WordBox(new Vec2i(x, lastObstruction.getY() + 1), Direction.VERTICAL, size));
            }
        }
        return boxes;
    }
}
