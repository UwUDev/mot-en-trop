package me.uwu.motentrop.api.solver;

import me.uwu.motentrop.api.struct.game.BoardScore;
import me.uwu.motentrop.api.struct.game.BoardState;
import me.uwu.motentrop.api.struct.game.IBoard;
import me.uwu.motentrop.api.struct.game.Move;
import me.uwu.motentrop.api.struct.math.Tuple;
import me.uwu.motentrop.api.word.IWordlist;

import java.util.HashSet;
import java.util.List;

public interface ISolver extends Runnable {

    /**
     * Run the solver.
     */
    @Override
    void run();

    void runStep();

    List<Move> getNextPossibleMoves();

    Tuple<Move, BoardScore> findBestMove();

    BoardScore evaluateBoard();

    default boolean isSolved() {
        return getWordlist().getUnusedWords().size() == 1;
    }

    IBoard getBoard();

    IWordlist getWordlist();

    default BoardState buildBoardState() {
        return new BoardState(new HashSet<>(getBoard().getLetters()), new HashSet<>(getWordlist().getUsedWords()));
    }

    int getTotalPaths();

    String getSearchedWord();
}
