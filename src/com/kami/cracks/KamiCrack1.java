package com.kami.cracks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.kami.gui.KamiSolution;
import com.kami.gui.KamiTriangle;
import com.kami.gui.KamiZone;
import com.kami.gui.KamiZoneColorOption;
import com.kami.gui.KamiZoneMap;

import javafx.scene.paint.Color;

public class KamiCrack1 {
	private List<Set<KamiTriangle>> groupList;
	private int numOfMoves;
	private Stack<KamiSolution> solutionStack;
	
	public KamiCrack1(List<Set<KamiTriangle>> groupList, int numOfMoves, Stack<KamiSolution> solutionStack) {
		this.groupList = groupList;
		this.numOfMoves = numOfMoves;
		this.solutionStack = solutionStack;
	}
	
	public void crack() {
		KamiZoneMap zoneMap = analyzegroupList(groupList);
		solveZoneList(zoneMap, numOfMoves, solutionStack);
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
		List<KamiZoneColorOption> zoneColorOptionList = new ArrayList<KamiZoneColorOption>();
		boolean allDisconnected = true;
		
		for (KamiZone zone:zoneList) {
			for (KamiZone neighbour:zone.getConnected()) {
				zone.addColor(neighbour.getColor());
				if (allDisconnected) {
					allDisconnected = false;
				}
			}
		}
		
		if (!allDisconnected) {
			for (KamiZone zone:zoneList) {
				for (Color color:zone.getConnectedColor().keySet()) {
					KamiZoneColorOption zoneColorOption = new KamiZoneColorOption();
					zoneColorOption.setZone(zone);
					zoneColorOption.setColor(color);
					zoneColorOption.setOccurence(zone.getConnectedColor().get(color));
					zoneColorOptionList.add(zoneColorOption);
				}
			}
			Collections.sort(zoneColorOptionList);
		}
		
		zoneMap.setZoneList(zoneList);
		zoneMap.setZoneColorOptionList(zoneColorOptionList);
		zoneMap.setAllDisconnected(allDisconnected);

		return zoneMap;
	}
	
	private KamiZoneMap changeZoneColor(KamiZoneMap oldZoneMap, int index, Color newColor) {
		/****** DEBUG ***** /
		this.counter++;
		System.out.println(oldZoneList.get(index).getElement().getRow() + "," + oldZoneList.get(index).getElement().getColumn() + " " + newColor.toString());
		if (counter > 50) {
			System.exit(0);
		}
		/****** DEBUG *****/
		List<KamiZone> newZoneList = copyZoneList(oldZoneMap.getZoneList());
//		Color oldColor = newZoneList.get(index).getColor();
		newZoneList.get(index).setColor(newColor);
//		newZoneList.get(index).getConnectedColor().remove(newColor);
		
		for (KamiZone neighbour:newZoneList.get(index).getConnected()) {
			if (neighbour.getColor().equals(newColor)) {
				neighbour.setMerged(true);
//				neighbour.getConnected().remove(newZoneList.get(index));
//				neighbour.removeColor(oldColor);
//				newZoneList.get(index).getConnectedColor().addAll(neighbour.getConnectedColor());
			} 
//			else {
//				neighbour.getConnectedColor().add(newColor);
//				if (onlyOneOldColor) {
//					neighbour.getConnectedColor().remove(oldColor);
//				}
//			}
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
		for (int i = 0; i < newZoneList.size(); i++) {
			newZoneList.get(i).setIndex(i);
		}
		
		KamiZoneMap newZoneMap = new KamiZoneMap();
		List<KamiZoneColorOption> newZoneColorOptionList = new ArrayList<KamiZoneColorOption>();
		boolean allDisconnected = true;
		
		for (KamiZone zone:newZoneList) {
			for (KamiZone neighbour:zone.getConnected()) {
				zone.addColor(neighbour.getColor());
				if (allDisconnected) {
					allDisconnected = false;
				}
			}
		}
		
		if (!allDisconnected) {
			for (KamiZone zone:newZoneList) {
				for (Color color:zone.getConnectedColor().keySet()) {
					KamiZoneColorOption zoneColorOption = new KamiZoneColorOption();
					zoneColorOption.setZone(zone);
					zoneColorOption.setColor(color);
					zoneColorOption.setOccurence(zone.getConnectedColor().get(color));
					newZoneColorOptionList.add(zoneColorOption);
				}
			}
			Collections.sort(newZoneColorOptionList);
		}
		
		newZoneMap.setZoneList(newZoneList);
		newZoneMap.setZoneColorOptionList(newZoneColorOptionList);
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
//			for (Color connectedColor:oldZoneList.get(i).getConnectedColor().keySet()) {
//				newZoneList.get(i).getConnectedColor().put(connectedColor,oldZoneList.get(i).getConnectedColor().get(connectedColor));
//			}
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
					for (KamiZoneColorOption zoneColorOption:zoneMap.getZoneColorOptionList()) {
						KamiZoneMap newZoneMap = changeZoneColor(zoneMap, zoneColorOption.getZone().getIndex(), zoneColorOption.getColor());
						if (validateZones(newZoneMap.getZoneList())) {
							KamiSolution solution = new KamiSolution();
							solution.setRow(zoneColorOption.getZone().getElement().getRow());
							solution.setCol(zoneColorOption.getZone().getElement().getColumn());
							solution.setColor(zoneColorOption.getColor());
							solutionStack.push(solution);
							return true;
						}
					}
//					for (KamiZone zone:zoneList) {
//						for (Color color:zone.getConnectedColor()) {
//							KamiZoneMap newZoneMap = changeZoneColor(zoneList, zone.getIndex(), color);
//							if (validateZones(newZoneList)) {
//								KamiSolution solution = new KamiSolution();
//								solution.setRow(zone.getElement().getRow());
//								solution.setCol(zone.getElement().getColumn());
//								solution.setColor(color);
//								solutionStack.push(solution);
//								return true;
//							}
//						}
//					}
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
					for (KamiZoneColorOption zoneColorOption:zoneMap.getZoneColorOptionList()) {
						KamiZoneMap newZoneMap = changeZoneColor(zoneMap, zoneColorOption.getZone().getIndex(), zoneColorOption.getColor());
						if (validateZones(newZoneMap.getZoneList())) {
							KamiSolution solution = new KamiSolution();
							solution.setRow(zoneColorOption.getZone().getElement().getRow());
							solution.setCol(zoneColorOption.getZone().getElement().getColumn());
							solution.setColor(zoneColorOption.getColor());
							solutionStack.push(solution);
							return true;
						} else if (solveZoneList(newZoneMap, numOfMoves -1, solutionStack)) {
							KamiSolution solution = new KamiSolution();
							solution.setRow(zoneColorOption.getZone().getElement().getRow());
							solution.setCol(zoneColorOption.getZone().getElement().getColumn());
							solution.setColor(zoneColorOption.getColor());
							solutionStack.push(solution);
							return true;
						}
					}
//					for (KamiZone zone:zoneList) {
//						for (Color color:zone.getConnectedColor()) {
//							List<KamiZone> newZoneList = changeZoneColor(zoneList, zone.getIndex(), color);
//							if (validateZones(newZoneList)) {
//								KamiSolution solution = new KamiSolution();
//								solution.setRow(zone.getElement().getRow());
//								solution.setCol(zone.getElement().getColumn());
//								solution.setColor(color);
//								solutionStack.push(solution);
//								return true;
//							} else {
//								if (solveZoneList(newZoneList, numOfMoves -1, solutionStack)) {
//									KamiSolution solution = new KamiSolution();
//									solution.setRow(zone.getElement().getRow());
//									solution.setCol(zone.getElement().getColumn());
//									solution.setColor(color);
//									solutionStack.push(solution);
//									return true;
//								} 
//							}
//						}
//					}
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
