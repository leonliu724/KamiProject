package com.kami.threads;

import java.util.Stack;

import com.kami.gui.KamiSolution;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

public class SolutionListener implements TaskListener {

	private Label[] moves;
	private Rectangle[] recs;
	private Label progress;
	
	public SolutionListener(Label[] moves, Rectangle[] recs, Label progress) {
		this.moves = moves;
		this.recs = recs;
		this.progress = progress;
	}
	
	public void threadComplete(Runnable runner) {
		SolutionThread solutionThread = (SolutionThread) runner;
		Stack<KamiSolution> solutions = solutionThread.getSolutions();
		int size = solutions.size();
		
		Platform.runLater(new Runnable(){
			public void run() {
				for (int i = 0; i < size; i++) {
					KamiSolution kamiSolution = solutions.pop();
					moves[i].setText("Step " + (i + 1) + ": (" + kamiSolution.getRow() + "," + kamiSolution.getCol() + ") ");
					recs[i].setFill(kamiSolution.getColor());
				}
				progress.setText("Done");
			}
		});
		
		
	}

}
