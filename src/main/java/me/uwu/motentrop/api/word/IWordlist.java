package me.uwu.motentrop.api.word;

import java.util.List;

public interface IWordlist {

    void setWordUsed(String word, boolean usedState);

    boolean isWordUsed(String word);

    List<String> getAllWords();

    List<String> getUsedWords();

    List<String> getUnusedWords();

}
