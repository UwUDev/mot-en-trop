package me.uwu.motentrop.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import me.uwu.motentrop.api.struct.game.IBoard;
import me.uwu.motentrop.api.struct.game.Letter;
import me.uwu.motentrop.api.struct.math.Vec2i;
import me.uwu.motentrop.struct.game.Board;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public @Data
class GameConfiguration {

    private static final transient Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .serializeNulls()
            .registerTypeAdapter(Vec2i.class, new Vec2iAdapter())
            .registerTypeAdapter(Letter.class, new LetterAdapter())
            .create();

    private final IBoard board;
    private final List<String> words;

    public static GameConfiguration loadFrom(File matrixFile, File wordsFile) throws IOException {
        Board board = GSON.fromJson(new FileReader(matrixFile), Board.class);
        board.initialize();

        List<String> wordList = Files.readAllLines(wordsFile.toPath())
                .stream()
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(trim -> trim.replace("\n", "").length() > 0)
                .collect(Collectors.toList());
        return new GameConfiguration(board, wordList);
    }
}
