package com.kami.cracks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.kami.gui.KamiSolution;
import com.kami.gui.KamiTriangle;
import com.kami.gui.KamiZone;
import com.kami.gui.KamiZoneMap;
import com.kami.threads.CrackCallable;

public class KamiCrack4 {
	private List<Set<KamiTriangle>> groupList;
	private int numOfMoves;
	private Stack<KamiSolution> solutionStack;
	
	public KamiCrack4(List<Set<KamiTriangle>> groupList, int numOfMoves, Stack<KamiSolution> solutionStack) {
		this.groupList = groupList;
		this.numOfMoves = numOfMoves;
		this.solutionStack = solutionStack;
	}
	
	public void crack() {
		KamiZoneMap zoneMap = analyzegroupList(groupList);
		
		ExecutorService executorService = Executors.newFixedThreadPool(4);
		Set<Callable<Stack<KamiSolution>>> callables = new HashSet<Callable<Stack<KamiSolution>>>();
		int quartile1 = zoneMap.getZoneList().size() / 4;
		int quartile2 = zoneMap.getZoneList().size() / 2;
		int quartile3 = zoneMap.getZoneList().size() / 4 * 3;
		
		CrackCallable callable1 = new CrackCallable(zoneMap, numOfMoves, 0, quartile1);
		CrackCallable callable2 = new CrackCallable(zoneMap, numOfMoves, quartile1, quartile2);
		CrackCallable callable3 = new CrackCallable(zoneMap, numOfMoves, quartile2, quartile3);
		CrackCallable callable4 = new CrackCallable(zoneMap, numOfMoves, quartile3, zoneMap.getZoneList().size());
		callables.add(callable1);
		callables.add(callable2);
		callables.add(callable3);
		callables.add(callable4);
		
		Stack<KamiSolution> localSolutionStack = new Stack<KamiSolution>();
		try {
			localSolutionStack = executorService.invokeAny(callables);
		} catch (Exception e) {
			e.printStackTrace();
		}
		executorService.shutdownNow();
		solutionStack.addAll(localSolutionStack);
		
	}
	
	private KamiZoneMap analyzegroupList(List<Set<KamiTriangle>> groupList) {
		List<KamiZone> zoneList = new ArrayList<KamiZone>();
		for (int i = 0; i < groupList.size(); i++) {
			zoneList.add(new KamiZone());
			zoneList.get(i).setIndex(i);
			zoneList.get(i).setName("Zone" + i);
		}
		
		for (int i = 0; i < groupList.size(); i++) {
			Set<KamiTriangle> group = groupList.get(i);
			boolean isElementSet = false;
			for (KamiTriangle element:group) {
				if (!isElementSet) {
					zoneList.get(i).setElement(element);
					zoneList.get(i).setColor(element.getColor());
					isElementSet = true;
				}
				if (element.isBorder()) {
					for (KamiTriangle neighbour:element.getConnected()) {
						if (neighbour.isBlank()) {
						} else {
							if (!element.getColor().equals(neighbour.getColor())) {
								for (int j = 0; j < groupList.size(); j++) {
									if (i != j) {
										if (groupList.get(j).contains(neighbour)) {
											zoneList.get(i).getConnectedColorSet().add(neighbour.getColor());
											zoneList.get(i).getConnected().add(zoneList.get(j));
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		KamiZoneMap zoneMap = new KamiZoneMap();
		boolean allDisconnected = true;
		
		Collections.sort(zoneList);
		
		for (int i = 0; i < zoneList.size(); i++) {
			zoneList.get(i).setIndex(i);
			if (allDisconnected) {
				if (zoneList.get(i).getConnected().size() > 0) {
					allDisconnected = false;
				}
			}
		}
		
		zoneMap.setZoneList(zoneList);
		zoneMap.setAllDisconnected(allDisconnected);

		return zoneMap;
	}
}
