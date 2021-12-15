package me.uwu.motentrop.renderer;

import me.uwu.motentrop.api.solver.ISolver;
import me.uwu.motentrop.api.struct.game.Direction;
import me.uwu.motentrop.api.struct.game.IBoard;
import me.uwu.motentrop.api.struct.math.Vec2i;
import me.uwu.motentrop.config.GameConfiguration;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.*;

import java.util.function.BiConsumer;

public class GameRenderer extends BasicGame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private final GameConfiguration gameConfiguration;
    private final ISolver solver;

    public GameRenderer(GameConfiguration gameConfiguration, ISolver solver) {
        super("MotEnTrop vDEV");
        this.gameConfiguration = gameConfiguration;
        this.solver = solver;
    }

    public static void runGame(GameConfiguration gameConfiguration, ISolver solver) throws SlickException {
        NativeLoader.INSTANCE.extractLWJGLNatives();

        AppGameContainer app = new AppGameContainer(new GameRenderer(gameConfiguration, solver));
        app.setDisplayMode(WIDTH, HEIGHT, false);
        app.setForceExit(false);
        app.start();
    }

    @Override
    public void init(GameContainer container) {
        container.getInput().addKeyListener(this);
    }

    @Override
    public void keyPressed(int key, char c) {
        if (key == Keyboard.KEY_SPACE) {
            try {
                this.solver.run();
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    public void update(GameContainer container, int delta) {
    }

    @Override
    public void render(GameContainer container, Graphics g) {
        IBoard board = this.gameConfiguration.getBoard();
        Vec2i boardSize = board.getSize();

        int baseX = 10;
        int baseY = 30;
        int tileSize = 25;

        g.setColor(Color.white);
        g.fillRect(baseX, baseY, boardSize.getX() * tileSize, boardSize.getY() * tileSize);

        board.getWalls()
                .forEach(pos -> {
                    g.setColor(Color.black);
                    g.fillRect(baseX + pos.getX() * tileSize, baseY + pos.getY() * tileSize, tileSize, tileSize);
                });

        board.getWordBoxes()
                .forEach(wordBox -> {
                    Vec2i start = wordBox.getStartPosition();
                    int size = wordBox.getSize();
                    Direction direction = wordBox.getDirection();
                    Vec2i vec = direction.getOffsetVector();

                    int x = baseX + start.getX() * tileSize;
                    int y = baseY + start.getY() * tileSize;

                    int width = (size * vec.getX()) * tileSize;
                    int height = (size * vec.getY()) * tileSize;

                    int padding = 5;

                    g.setColor(new Color(direction == Direction.VERTICAL ? 0f : 1f, 0f, direction == Direction.VERTICAL ? 1f : 0f, .2f));
                    g.fillRect(x + padding, y + padding, Math.max(tileSize, width) - (2 * padding), Math.max(tileSize, height) - (2 * padding));
                });

        board.getLetters()
                .forEach(letter -> {
                    BiConsumer<String, Vec2i> drawText = (text, pos) -> g.getFont().drawString(
                            baseX + (pos.getX() * tileSize) + tileSize / 2f - g.getFont().getWidth(text) / 2f,
                            baseY + (pos.getY() * tileSize) + tileSize / 2f - g.getFont().getHeight(text) / 2f - 1,
                            text,
                            Color.black
                    );

                    drawText.accept(String.valueOf(letter.getCharacter()), letter.getPosition());
                });

        String str = "Mot en trop: ";

        if (solver.isSolved()) {
            str += solver.getSearchedWord();
        }
        g.setColor(new Color(1F, 1F, 1F, 1F));
        g.getFont().drawString(
                baseX,
                baseY + (boardSize.getY()) * tileSize + 5,
                str,
                Color.white
        );
    }
}
