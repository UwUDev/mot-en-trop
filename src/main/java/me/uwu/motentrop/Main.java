package me.uwu.motentrop;

import joptsimple.*;
import me.uwu.motentrop.api.solver.ISolver;
import me.uwu.motentrop.config.GameConfiguration;
import me.uwu.motentrop.renderer.GameRenderer;
import me.uwu.motentrop.solver.SolvingAlgorithm;
import me.uwu.motentrop.word.WordManager;
import org.newdawn.slick.SlickException;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String... args) throws IOException {
        OptionParser parser = new OptionParser(true);

        ArgumentAcceptingOptionSpec<File> boardFileOpt = parser.accepts("board", "The matrix json file")
                .withRequiredArg()
                .ofType(File.class)
                .required();

        ArgumentAcceptingOptionSpec<File> wordsFileOpt = parser.accepts("words", "The words list text file")
                .withRequiredArg()
                .ofType(File.class)
                .required();

        AbstractOptionSpec<Void> debugOpt = parser.accepts("debug", "Enables debug printing");

        AbstractOptionSpec<Void> manualOpt = parser.accepts("manualTrigger", "Waits for user input to solve the board");

        AbstractOptionSpec<Void> helpOpt = parser.accepts("help", "Prints a help page").forHelp();

        OptionSet parse;

        try {
            parse = parser.parse(args);
        } catch (OptionException e) {
            System.err.println(e.getMessage() + " (try --help)");
            return;
        }

        if (parse.has(helpOpt)) {
            parser.printHelpOn(System.err);
            return;
        }

        boolean manual = parse.has(manualOpt);
        boolean debug = parse.has(debugOpt);

        File boardFile = parse.valueOf(boardFileOpt);
        File wordsFile = parse.valueOf(wordsFileOpt);

        GameConfiguration gameConfiguration = GameConfiguration.loadFrom(boardFile, wordsFile);
        ISolver solver = new SolvingAlgorithm(gameConfiguration.getBoard(), new WordManager(gameConfiguration.getWords()), debug);
        if(!manual) {
            solver.run();
        }

        try {
            GameRenderer.runGame(gameConfiguration, solver);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

}
