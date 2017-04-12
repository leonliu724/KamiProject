package com.kami.threads;

import java.util.Stack;

import com.kami.gui.KamiGraph;
import com.kami.gui.KamiSolution;

public class SolutionThread extends NotificationThread {
	
	private KamiGraph graph;
	private int num;
	private Stack<KamiSolution> solutions;
	
	public SolutionThread(KamiGraph graph, int num) {
		this.graph = graph;
		this.num = num;
		this.solutions = null;
	}
	
	public Stack<KamiSolution> getSolutions() {
		return solutions;
	}

	@Override
	public void doWork() {
		graph.backup();
		solutions = graph.crack(num);
		graph.restore();
	}
}
