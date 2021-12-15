package me.uwu.motentrop.solver;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import me.uwu.motentrop.api.solver.ISolver;
import me.uwu.motentrop.api.struct.game.*;
import me.uwu.motentrop.api.struct.math.Tuple;
import me.uwu.motentrop.api.word.IWordlist;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2(topic = "SolvingAlgorithm")
public @Data
class SolvingAlgorithm implements ISolver {

    private final IBoard board;
    private final IWordlist wordlist;
    private final boolean debug;

    private final Map<BoardState, List<Move>> possibleMovesCache = new HashMap<>();

    private final AtomicInteger total = new AtomicInteger();

    @Override
    public void run() {
        if (debug) {
            ((Logger) log).setLevel(Level.ALL);
        }

        log.info("Solving...");
        while (!isSolved()) {
            runStep();
        }

        log.info("Mot en trop: {}", getSearchedWord());
    }

    @Override
    public void runStep() {
        Tuple<Move, BoardScore> best = findBestMove();
        assert best.getSecond() == BoardScore.SOLVED;

        Move bestMove = best.getFirst();
        assert bestMove != null;

        log.info("Playing " + best.getFirst() + "@" + best.getSecond());

        this.board.play(bestMove);
        this.wordlist.setWordUsed(String.valueOf(bestMove.getWord()), true);
    }

    @Override
    public BoardScore evaluateBoard() {
        if (isSolved()) return BoardScore.SOLVED;
        if (getNextPossibleMoves().isEmpty()) return BoardScore.FAILED;
        return BoardScore.UNDETERMINED;
    }

    @Override
    public List<Move> getNextPossibleMoves() {
        List<Move> possibleMoves = new ArrayList<>();

        this.board.getLetters().forEach(letter -> {
            for (Direction direction : Direction.values()) {
                IWordBox wordBox = this.board.getWordBoxAt(letter.getPosition(), direction);
                if (wordBox != null) {
                    this.wordlist.getUnusedWords()
                            .stream()
                            .map(String::toCharArray)
                            .filter(wordBox::canAccept)
                            .filter(content -> this.board.canBePlaced(content, wordBox))
                            .forEach(content -> possibleMoves.add(new Move(content, wordBox)));
                }
            }
        });

        return possibleMoves;
    }

    @Override
    public Tuple<Move, BoardScore> findBestMove() {
        List<Tuple<Move, BoardScore>> scoreBranches = new ArrayList<>();
        List<Move> availableMoves = this.getNextPossibleMoves();

        assert !availableMoves.isEmpty();

        for (Move move : availableMoves) {
            this.total.incrementAndGet();

            AtomicBoolean shouldBreak = new AtomicBoolean(false);
            this.board.withPlay(move, b -> {
                String word = String.valueOf(move.getWord());
                this.wordlist.setWordUsed(word, true);

                BoardScore score = evaluateBoard();

                log.debug(
                        "[{}/{}] {} to {}@{} as {}",
                        availableMoves.indexOf(move) + 1,
                        availableMoves.size(),
                        String.valueOf(move.getWord()),
                        move.getWordBox().getStartPosition(),
                        move.getWordBox().getDirection(),
                        score
                );

                log.debug("Missing: {}", String.join(", ", this.wordlist.getUnusedWords()));
                board.printBoard(log::debug);

                if (score.isUndetermined()) {
                    score = findBestMove().getSecond();
                }

                scoreBranches.add(new Tuple<>(move, score));
                this.wordlist.setWordUsed(word, false);

                if (score == BoardScore.SOLVED) {
                    log.debug("Got solved, breaking loop, going up");
                    shouldBreak.set(true);
                }
            });
            if (shouldBreak.get()) break;
        }

        Move bestMove = scoreBranches.stream()
                .filter(b -> b.getSecond() == BoardScore.SOLVED)
                .map(Tuple::getFirst)
                .findFirst()
                .orElse(null);

        return new Tuple<>(
                bestMove,
                bestMove == null ? BoardScore.FAILED : BoardScore.SOLVED
        );
    }

    @Override
    public int getTotalPaths() {
        return this.total.get();
    }

    @Override
    public String getSearchedWord() {
        assert isSolved() : "Board isn't solved.";
        return this.wordlist.getUnusedWords().get(0);
    }
}