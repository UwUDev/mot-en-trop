package me.uwu.motentrop.api.analysis;

import me.uwu.motentrop.api.struct.game.IBoard;
import me.uwu.motentrop.api.struct.game.IWordBox;

import java.util.List;

public interface IBoardPopulator {

    List<IWordBox> locateWordBoxes(IBoard board);

}
