package com.kami.cracks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.kami.gui.KamiSolution;
import com.kami.gui.KamiTriangle;
import com.kami.gui.KamiZone;
import com.kami.gui.KamiZoneMap;

import javafx.scene.paint.Color;

public class KamiCrack3 {
	private List<Set<KamiTriangle>> groupList;
	private int numOfMoves;
	private Stack<KamiSolution> solutionStack;
	private int minValueLimit = 999;
	private int maxValueLimit = 0;
	
	public KamiCrack3(List<Set<KamiTriangle>> groupList, int numOfMoves, Stack<KamiSolution> solutionStack) {
		this.groupList = groupList;
		this.numOfMoves = numOfMoves;
		this.solutionStack = solutionStack;
	}
	
	public void crack() {
		KamiZoneMap zoneMap = analyzegroupList(groupList);
		for (int i = minValueLimit; i <= maxValueLimit; i++) {
			if (solveZoneList(zoneMap, numOfMoves, solutionStack, i)) {
				break;
			}
		}
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
		
		setZoneValue(zoneList);
		for (KamiZone zone:zoneList) {
			if (zone.getValue() < this.minValueLimit) {
				this.minValueLimit = zone.getValue();
			}
			if (zone.getValue() > this.maxValueLimit) {
				this.maxValueLimit = zone.getValue();
			}
		}
		
		zoneMap.setZoneList(zoneList);
		zoneMap.setAllDisconnected(allDisconnected);

		return zoneMap;
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
			zoneCopy.setValue(oldZoneList.get(i).getValue());
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
	
	private boolean solveZoneList(KamiZoneMap zoneMap, int numOfMoves, Stack<KamiSolution> solutionStack, int valueLimit) {
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
						if (zone.getValue() <= valueLimit) {
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
							} else if (solveZoneList(newZoneMap, numOfMoves -1, solutionStack, valueLimit)) {
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
						if (zone.getValue() <= valueLimit) {
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
									if (solveZoneList(newZoneMap, numOfMoves -1, solutionStack, valueLimit)) {
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
	
	private void setZoneValue(List<KamiZone> zoneList) {
		int size = zoneList.size();
		
		for (KamiZone zone:zoneList) {
			Set<KamiZone> tempZoneSet1 = new HashSet<KamiZone>();
			Set<KamiZone> tempZoneSet2 = new HashSet<KamiZone>();
			Set<KamiZone> tempZoneSet3 = new HashSet<KamiZone>();
			tempZoneSet1.add(zone);
			tempZoneSet2.add(zone);
			int n = 0;
			while (tempZoneSet1.size() < size) {
				for (KamiZone valueZone:tempZoneSet2) {
					for (KamiZone neighbour:valueZone.getConnected()) {
						if (!tempZoneSet1.contains(neighbour)) {
							tempZoneSet3.add(neighbour);
						}
					}
				}
				if (tempZoneSet3.size() == 0) {
					break;
				}
				tempZoneSet2.clear();
				tempZoneSet2.addAll(tempZoneSet3);
				tempZoneSet1.addAll(tempZoneSet3);
				tempZoneSet3.clear();
				n++;
			}
			zone.setValue(n);
			System.out.println(zone.getName() + ":" + zone.getValue());
		}
	}
}
