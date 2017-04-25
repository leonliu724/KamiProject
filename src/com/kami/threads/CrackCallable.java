package com.kami.threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Callable;

import com.kami.gui.KamiSolution;
import com.kami.gui.KamiZone;
import com.kami.gui.KamiZoneMap;

import javafx.scene.paint.Color;

public class CrackCallable implements Callable<Stack<KamiSolution>> {
	KamiZoneMap zoneMap;
	private int numOfMoves;
	private int start;
	private int end;
	
	public CrackCallable(KamiZoneMap zoneMap, int numOfMoves, int start, int end) {
		this.zoneMap = zoneMap;
		this.numOfMoves = numOfMoves;
		this.start = start;
		this.end = end;
	}
	
	public Stack<KamiSolution> call() throws Exception {
		Stack<KamiSolution> solutionStack = new Stack<KamiSolution>();
		for (int i = start; i < end; i++) {
			KamiZone zone = zoneMap.getZoneList().get(i);
			for (Color color:zone.getConnectedColorSet()) {
				KamiZoneMap newZoneMap = changeZoneColor(zoneMap, zone.getIndex(), color);
				if (solveZoneList(newZoneMap, numOfMoves -1, solutionStack)) {
					KamiSolution solution = new KamiSolution();
					solution.setRow(zone.getElement().getRow());
					solution.setCol(zone.getElement().getColumn());
					solution.setColor(color);
					solutionStack.push(solution);
					return solutionStack;
				} 
			}
		}
		throw new Exception("No Solution Available");
	}
	
	private KamiZoneMap changeZoneColor(KamiZoneMap oldZoneMap, int index, Color newColor) {
		List<KamiZone> newZoneList = copyZoneList(oldZoneMap.getZoneList());
		Color oldColor = newZoneList.get(index).getColor();
		newZoneList.get(index).setColor(newColor);
		newZoneList.get(index).getConnectedColorSet().remove(newColor);
		
		for (KamiZone neighbour:newZoneList.get(index).getConnected()) {
			boolean onlyOneOldColor = true;
			
			for (KamiZone farNeighbour:neighbour.getConnected()) {
				if (farNeighbour.getIndex() != index) {
					if (farNeighbour.getColor().equals(oldColor)) {
						onlyOneOldColor = false;
						break;
					}
				}
			}
			
			if (neighbour.getColor().equals(newColor)) {
				neighbour.setMerged(true);
				neighbour.getConnected().remove(newZoneList.get(index));
				if (onlyOneOldColor) {
					neighbour.getConnectedColor().remove(oldColor);
				}
				newZoneList.get(index).getConnectedColorSet().addAll(neighbour.getConnectedColorSet());
			} else {
				neighbour.getConnectedColorSet().add(newColor);
				if (onlyOneOldColor) {
					neighbour.getConnectedColorSet().remove(oldColor);
				}
			}
		}
		
		List<Integer> removeList = new ArrayList<Integer>();
		for (int i = 0; i < newZoneList.size(); i++) {
			if (newZoneList.get(i).isMerged()) {
				newZoneList.get(i).getConnected().remove(newZoneList.get(index));
				newZoneList.get(index).getConnected().remove(newZoneList.get(i));
				
				for (KamiZone neighbourOfMergedNeighbour:newZoneList.get(i).getConnected()) {
					neighbourOfMergedNeighbour.getConnected().remove(newZoneList.get(i));
					neighbourOfMergedNeighbour.getConnected().add(newZoneList.get(index));
					newZoneList.get(index).getConnected().add(neighbourOfMergedNeighbour);
				}
				removeList.add(i);
			}
		}
		for (int i = removeList.size() - 1; i >= 0; i--) {
			newZoneList.remove(removeList.get(i).intValue());
		}
		Collections.sort(newZoneList);
		
		KamiZoneMap newZoneMap = new KamiZoneMap();
		boolean allDisconnected = true;
		
		
		
		for (int i = 0; i < newZoneList.size(); i++) {
			newZoneList.get(i).setIndex(i);
			if (allDisconnected) {
				if (newZoneList.get(i).getConnected().size() > 0) {
					allDisconnected = false;
				}
			}
		}
		
		newZoneMap.setZoneList(newZoneList);
		newZoneMap.setAllDisconnected(allDisconnected);

		return newZoneMap;
	}
	
	private List<KamiZone> copyZoneList(List<KamiZone> oldZoneList) {
		List<KamiZone> newZoneList = new ArrayList<KamiZone>();
		for (int i = 0; i < oldZoneList.size(); i++) {
			KamiZone zoneCopy = new KamiZone();
			zoneCopy.setIndex(i);
			zoneCopy.setElement(oldZoneList.get(i).getElement());
			zoneCopy.setColor(oldZoneList.get(i).getColor());
			newZoneList.add(zoneCopy);
		}
		for (int i = 0; i < oldZoneList.size(); i++) {
			for (KamiZone connected:oldZoneList.get(i).getConnected()) {
				newZoneList.get(i).getConnected().add(newZoneList.get(connected.getIndex()));
			}
			for (Color connectedColor:oldZoneList.get(i).getConnectedColorSet()) {
				newZoneList.get(i).getConnectedColorSet().add(connectedColor);
			}
		}
		return newZoneList;
	}
	
	private boolean solveZoneList(KamiZoneMap zoneMap, int numOfMoves, Stack<KamiSolution> solutionStack) {
		if (numOfMoves == 1) {
			Map<Color, Integer> colorSet = new HashMap<Color, Integer>();
			for (KamiZone zone:zoneMap.getZoneList()) {
				if (colorSet.containsKey(zone.getColor())) {
					colorSet.put(zone.getColor(), colorSet.get(zone.getColor()) + 1);
				} else {
					colorSet.put(zone.getColor(), 1);
				}
			}
			if (colorSet.size() <= 2) {
				if (zoneMap.isAllDisconnected()) {
					Color majorityColor = null;
					int majorityOccur = 0;
					
					for (Color color:colorSet.keySet()) {
						if (colorSet.get(color) > majorityOccur) {
							majorityColor = color;
							majorityOccur = colorSet.get(color);
						}
					}
					
					for (KamiZone zone:zoneMap.getZoneList()) {
						if (!zone.getColor().equals(majorityColor)) {
							KamiZoneMap newZoneMap = changeZoneColor(zoneMap, zone.getIndex(), majorityColor);
							if (validateZones(newZoneMap.getZoneList())) {
								KamiSolution solution = new KamiSolution();
								solution.setRow(zone.getElement().getRow());
								solution.setCol(zone.getElement().getColumn());
								solution.setColor(majorityColor);
								solutionStack.push(solution);
								return true;
							}
						}
					}
					return false;
				} else {
					for (KamiZone zone:zoneMap.getZoneList()) {
						for (Color color:zone.getConnectedColorSet()) {
							KamiZoneMap newZoneMap = changeZoneColor(zoneMap, zone.getIndex(), color);
							if (validateZones(newZoneMap.getZoneList())) {
								KamiSolution solution = new KamiSolution();
								solution.setRow(zone.getElement().getRow());
								solution.setCol(zone.getElement().getColumn());
								solution.setColor(color);
								solutionStack.push(solution);
								return true;
							}
						}
					}
					return false;
				}
			} else {
				return false;
			}
		} else {
			Map<Color, Integer> colorSet = new HashMap<Color, Integer>();
			for (KamiZone zone:zoneMap.getZoneList()) {
				if (colorSet.containsKey(zone.getColor())) {
					colorSet.put(zone.getColor(), colorSet.get(zone.getColor()) + 1);
				} else {
					colorSet.put(zone.getColor(), 1);
				}
			}
			if (colorSet.size() <= numOfMoves + 1) {
				if (zoneMap.isAllDisconnected()) {
					Color majorityColor = null;
					int majorityOccur = 0;
					
					for (Color color:colorSet.keySet()) {
						if (colorSet.get(color) > majorityOccur) {
							majorityColor = color;
							majorityOccur = colorSet.get(color);
						}
					}
					
					for (KamiZone zone:zoneMap.getZoneList()) {
						if (!zone.getColor().equals(majorityColor)) {
							KamiZoneMap newZoneMap = changeZoneColor(zoneMap, zone.getIndex(), majorityColor);
							if (validateZones(newZoneMap.getZoneList())) {
								KamiSolution solution = new KamiSolution();
								solution.setRow(zone.getElement().getRow());
								solution.setCol(zone.getElement().getColumn());
								solution.setColor(majorityColor);
								solutionStack.push(solution);
								return true;
							} else if (solveZoneList(newZoneMap, numOfMoves -1, solutionStack)) {
								KamiSolution solution = new KamiSolution();
								solution.setRow(zone.getElement().getRow());
								solution.setCol(zone.getElement().getColumn());
								solution.setColor(majorityColor);
								solutionStack.push(solution);
								return true;
							}
						}
					}
					return false;
				} else {
					for (KamiZone zone:zoneMap.getZoneList()) {
						for (Color color:zone.getConnectedColorSet()) {
							KamiZoneMap newZoneMap = changeZoneColor(zoneMap, zone.getIndex(), color);
							if (validateZones(newZoneMap.getZoneList())) {
								KamiSolution solution = new KamiSolution();
								solution.setRow(zone.getElement().getRow());
								solution.setCol(zone.getElement().getColumn());
								solution.setColor(color);
								solutionStack.push(solution);
								return true;
							} else {
								if (solveZoneList(newZoneMap, numOfMoves -1, solutionStack)) {
									KamiSolution solution = new KamiSolution();
									solution.setRow(zone.getElement().getRow());
									solution.setCol(zone.getElement().getColumn());
									solution.setColor(color);
									solutionStack.push(solution);
									return true;
								} 
							}
						}
					}
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
	private boolean validateZones(List<KamiZone> zoneList) {
		Color color = zoneList.get(0).getColor();
		
		for (KamiZone zone:zoneList) {
			if (!zone.getColor().equals(color)) {
				return false;
			}
		}
		return true;
	}
	
}
