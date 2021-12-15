package me.uwu.motentrop.word;

import me.uwu.motentrop.api.word.IWordlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WordManager implements IWordlist {
    private final List<String> allWords;

    private final List<String> usedWords = new ArrayList<>();
    private final List<String> unusedWords;

    public WordManager(List<String> allWords) {
        this.allWords = allWords;
        this.unusedWords = new ArrayList<>();
        this.unusedWords.addAll(this.allWords);
    }

    @Override
    public void setWordUsed(String word, boolean usedState) {
        if (usedState) {
            this.unusedWords.removeIf(word::equals);
            this.usedWords.add(word);
        } else {
            assert isWordUsed(word) : "what.";

            this.unusedWords.add(word);
            this.usedWords.removeIf(word::equals);
        }
    }

    @Override
    public boolean isWordUsed(String word) {
        return this.usedWords.contains(word);
    }

    public List<String> getAllWords() {
        return this.allWords;
    }

    public List<String> getUsedWords() {
        return this.usedWords;
    }

    public List<String> getUnusedWords() {
        return this.unusedWords;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof WordManager)) return false;
        final WordManager other = (WordManager) o;
        if (!other.canEqual(this)) return false;
        final Object this$allWords = this.getAllWords();
        final Object other$allWords = other.getAllWords();
        if (!Objects.equals(this$allWords, other$allWords)) return false;
        final Object this$usedWords = this.getUsedWords();
        final Object other$usedWords = other.getUsedWords();
        if (!Objects.equals(this$usedWords, other$usedWords)) return false;
        final Object this$unusedWords = this.getUnusedWords();
        final Object other$unusedWords = other.getUnusedWords();
        return Objects.equals(this$unusedWords, other$unusedWords);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof WordManager;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $allWords = this.getAllWords();
        result = result * PRIME + ($allWords == null ? 43 : $allWords.hashCode());
        final Object $usedWords = this.getUsedWords();
        result = result * PRIME + ($usedWords == null ? 43 : $usedWords.hashCode());
        final Object $unusedWords = this.getUnusedWords();
        result = result * PRIME + ($unusedWords == null ? 43 : $unusedWords.hashCode());
        return result;
    }

    public String toString() {
        return "WordManager(allWords=" + this.getAllWords() + ", usedWords=" + this.getUsedWords() + ", unusedWords=" + this.getUnusedWords() + ")";
    }
}
