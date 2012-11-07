package com.globallogic.training.oleksii.morozov.sapperthehero.game.minegenerator;

import java.util.List;

import com.globallogic.training.oleksii.morozov.sapperthehero.game.Board;
import com.globallogic.training.oleksii.morozov.sapperthehero.game.objects.Mine;

public class SavedStateMinesGenerator implements MinesGenerator {
	List<Mine> mines;

	public SavedStateMinesGenerator(List<Mine> mines) {
		this.mines = mines;
	}

	@Override
	public List<Mine> get(int count, Board board) {
		return mines;
	}

}
