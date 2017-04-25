package com.kami.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.kami.cracks.KamiCrack1;
import com.kami.cracks.KamiCrack2;
import com.kami.cracks.KamiCrack3;
import com.kami.cracks.KamiCrack4;
import com.kami.gui.KamiTriangle;

import javafx.animation.FillTransition;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class KamiGraph {
	private static final double EDGE_LENGTH = 24.0;
	private static final double X_OFFSET = 0.0;
	private static final double Y_OFFSET = 6.0;
	private Stack<KamiSolution> solutionStack;
	private KamiTriangle[][] kamiGraph;
	private KamiTriangle[][] kamiGraphBackup;
	private int row;
	private int col;
	private int numOfMoves;

	public int getNumOfMoves() {
		return numOfMoves;
	}
	public void setNumOfMoves(int numOfMoves) {
		this.numOfMoves = numOfMoves;
	}
	public int getRow() {
		return row;
	}
	public int getCol() {
		return col;
	}
	
	public KamiGraph(int row, int col) {
		kamiGraph = new KamiTriangle[row][col];
		kamiGraphBackup  = new KamiTriangle[row][col];
		this.row = row;
		this.col = col;
		
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				kamiGraph[i][j] = new KamiTriangle();
				kamiGraphBackup[i][j] = new KamiTriangle();
				kamiGraph[i][j].setRow(i);
				kamiGraph[i][j].setColumn(j);
			}
		}
		
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				if (i == 0) {
					Double[] coordinates = new Double[6];
					kamiGraph[i][j].getConnected().add(kamiGraph[i+1][j]);
					coordinates[0] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
					coordinates[1] = 0.0  + Y_OFFSET;
					coordinates[2] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
					coordinates[3] = 0.0 + Y_OFFSET;
					coordinates[5] = EDGE_LENGTH + Y_OFFSET;
					
					if (j == 0) {
						coordinates[4] = 0.0 + X_OFFSET;
					} else if (j < col - 1) {
						if (j % 2 == 1) {
							kamiGraph[i][j].getConnected().add(kamiGraph[i][j+1]);
							coordinates[4] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
						} else {
							kamiGraph[i][j].getConnected().add(kamiGraph[i][j-1]);
							coordinates[4] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
						}
					} else {
						if (j % 2 == 1) {
							coordinates[4] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
						} else {
							kamiGraph[i][j].getConnected().add(kamiGraph[i][j-1]);
							coordinates[4] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
						}
					}
					kamiGraph[i][j].getPoints().addAll(coordinates);
				} else if (i < row - 1) {
					Double[] coordinates = new Double[6];
					kamiGraph[i][j].getConnected().add(kamiGraph[i+1][j]);
					kamiGraph[i][j].getConnected().add(kamiGraph[i-1][j]);
					
					if (j == 0) {
						if (i % 2 == 1) {
							kamiGraph[i][j].getConnected().add(kamiGraph[i][j+1]);
							coordinates[0] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[1] = (i - 1) * EDGE_LENGTH + Y_OFFSET;
							coordinates[2] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[3] = (i + 1) * EDGE_LENGTH + Y_OFFSET;
							coordinates[4] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[5] = i * EDGE_LENGTH + Y_OFFSET;
						} else {
							coordinates[0] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[1] = (i - 1) * EDGE_LENGTH + Y_OFFSET;
							coordinates[2] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[3] = i * EDGE_LENGTH + Y_OFFSET;
							coordinates[4] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[5] = (i + 1) * EDGE_LENGTH + Y_OFFSET;
						}
					} else if (j < col - 1) {
						if ((i + j) % 2 == 1) {
							kamiGraph[i][j].getConnected().add(kamiGraph[i][j+1]);
							coordinates[0] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[1] = (i - 1) * EDGE_LENGTH + Y_OFFSET;
							coordinates[2] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[3] = (i + 1) * EDGE_LENGTH + Y_OFFSET;
							coordinates[4] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[5] = i * EDGE_LENGTH + Y_OFFSET;
						} else {
							kamiGraph[i][j].getConnected().add(kamiGraph[i][j-1]);
							coordinates[0] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[1] = (i - 1) * EDGE_LENGTH + Y_OFFSET;
							coordinates[2] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[3] = i * EDGE_LENGTH + Y_OFFSET;
							coordinates[4] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[5] = (i + 1) * EDGE_LENGTH + Y_OFFSET;
						}
					} else {
						if ((i + j) % 2 == 1) {
							coordinates[0] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[1] = (i - 1) * EDGE_LENGTH + Y_OFFSET;
							coordinates[2] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[3] = (i + 1) * EDGE_LENGTH + Y_OFFSET;
							coordinates[4] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[5] = i * EDGE_LENGTH + Y_OFFSET;
						} else {
							kamiGraph[i][j].getConnected().add(kamiGraph[i][j-1]);
							coordinates[0] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[1] = (i - 1) * EDGE_LENGTH + Y_OFFSET;
							coordinates[2] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[3] = i * EDGE_LENGTH + Y_OFFSET;
							coordinates[4] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[5] = (i + 1) * EDGE_LENGTH + Y_OFFSET;
						}
					}
					kamiGraph[i][j].getPoints().addAll(coordinates);
				} else {
					Double[] coordinates = new Double[6];
					kamiGraph[i][j].getConnected().add(kamiGraph[i-1][j]);
					
					if (j == 0) {
						if (i % 2 == 1) {
							kamiGraph[i][j].getConnected().add(kamiGraph[i][j+1]);
							coordinates[0] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[1] = (i - 1) * EDGE_LENGTH + Y_OFFSET;
							coordinates[2] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[3] = i  * EDGE_LENGTH + Y_OFFSET;
							coordinates[4] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[5] = i * EDGE_LENGTH + Y_OFFSET;
						} else {
							coordinates[0] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[1] = (i - 1) * EDGE_LENGTH + Y_OFFSET;
							coordinates[2] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[3] = i * EDGE_LENGTH + Y_OFFSET;
							coordinates[4] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[5] = i  * EDGE_LENGTH + Y_OFFSET;
						}
					} else if (j < col - 1) {
						if ((i + j) % 2 == 1) {
							kamiGraph[i][j].getConnected().add(kamiGraph[i][j+1]);
							coordinates[0] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[1] = (i - 1) * EDGE_LENGTH + Y_OFFSET;
							coordinates[2] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[3] = i  * EDGE_LENGTH + Y_OFFSET;
							coordinates[4] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[5] = i * EDGE_LENGTH + Y_OFFSET;
						} else {
							kamiGraph[i][j].getConnected().add(kamiGraph[i][j-1]);
							coordinates[0] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[1] = (i - 1) * EDGE_LENGTH + Y_OFFSET;
							coordinates[2] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[3] = i * EDGE_LENGTH + Y_OFFSET;
							coordinates[4] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[5] = i  * EDGE_LENGTH + Y_OFFSET;
						}
					} else {
						if ((i + j) % 2 == 1) {
							coordinates[0] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[1] = (i - 1) * EDGE_LENGTH + Y_OFFSET;
							coordinates[2] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[3] = i  * EDGE_LENGTH + Y_OFFSET;
							coordinates[4] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[5] = i * EDGE_LENGTH + Y_OFFSET;
						} else {
							kamiGraph[i][j].getConnected().add(kamiGraph[i][j-1]);
							coordinates[0] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[1] = (i - 1) * EDGE_LENGTH + Y_OFFSET;
							coordinates[2] = (j + 1) * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[3] = i * EDGE_LENGTH + Y_OFFSET;
							coordinates[4] = j * EDGE_LENGTH * Math.sqrt(3.0) + X_OFFSET;
							coordinates[5] = i  * EDGE_LENGTH + Y_OFFSET;
						}
					}
					kamiGraph[i][j].getPoints().addAll(coordinates);
				}
			}
		}
	}
	
	public void backup() {
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				kamiGraphBackup[i][j].setColor(kamiGraph[i][j].getColor());
			}
		}
	}
	
	public void restore() {
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				kamiGraph[i][j].setColor(kamiGraphBackup[i][j].getColor());
				kamiGraph[i][j].setFill(kamiGraphBackup[i][j].getColor());
			}
		}
	}
	
	public KamiTriangle getElement(int row, int col) {
		return kamiGraph[row][col];
	}
	
	public List<KamiTriangle> export() {
		List<KamiTriangle> list = new ArrayList<KamiTriangle>();
		
		for(KamiTriangle[] array:kamiGraph) {
			list.addAll(Arrays.asList(array));
		}
		return list;
	}
	
	public boolean validate() {
		boolean colorSet = false;
		Color color = null;
		
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				if (kamiGraph[i][j].isBlank()) {
				} else {
					if (colorSet) {
						if (!kamiGraph[i][j].getColor().equals(color)) {
							return false;
						}
					} else {
						color = kamiGraph[i][j].getColor();
						colorSet = true;
					}
				}
			}
		}
		return true;
	}
	
	public void clickDesign(int rowNum, int columnNum, Color color) {
		if (color.equals(Color.WHITE)) {
			kamiGraph[rowNum][columnNum].setBlank(true);
		} else {
			kamiGraph[rowNum][columnNum].setBlank(false);
		}
		kamiGraph[rowNum][columnNum].setFill(color);
		kamiGraph[rowNum][columnNum].setColor(color);
	}
	
	public boolean clickGame(int rowNum, int columnNum, Color color) {
		if (!color.equals(Color.WHITE) && !kamiGraph[rowNum][columnNum].isBlank()) {
			spread(rowNum, columnNum, color, "HUMAN");
			resetChangedFlag();
		}
		return validate();
	}
	
	public void setLabel(Group group) {
		double coordY = (row - 1) * EDGE_LENGTH + 8.0;
		double coordX = col * EDGE_LENGTH * Math.sqrt(3.0) + 8.0;
		int fontSize = 12;
		
		for (int i = 0; i < col; i++) {
			Label label = new Label(Integer.toString(i));
			label.setFont(new Font("Arial", fontSize));
			label.setLayoutX((i + 0.5) * EDGE_LENGTH * Math.sqrt(3.0));
			label.setLayoutY(coordY);
			group.getChildren().add(label);
		}
		
		for (int i = 0; i < row; i++) {
			Label label = new Label(Integer.toString(i));
			label.setFont(new Font("Arial", fontSize));
			label.setLayoutX(coordX);
			label.setLayoutY(i * EDGE_LENGTH);
			group.getChildren().add(label);
		}
		
		
	}
	
	public Stack<KamiSolution> crack(int numOfMoves) {
		long startTime = System.currentTimeMillis();
		solutionStack = new Stack<KamiSolution>();
		List<Set<KamiTriangle>> groupList = getGraphgroupList();
//		KamiCrack1 cracker = new KamiCrack1(groupList, numOfMoves, solutionStack);
//		KamiCrack2 cracker = new KamiCrack2(groupList, numOfMoves, solutionStack);
		KamiCrack3 cracker = new KamiCrack3(groupList, numOfMoves, solutionStack);
//		KamiCrack4 cracker = new KamiCrack4(groupList, numOfMoves, solutionStack);
		cracker.crack();
		long endTime = System.currentTimeMillis();
		System.out.println("It took " + (endTime - startTime) + " ms.");
		return solutionStack;
	}
	
	private void spread(int rowNum, int columnNum, Color color, String operator) {
		Color currentColor = kamiGraph[rowNum][columnNum].getColor();
		
		if (operator.equals("HUMAN")) {
			FillTransition ft = new FillTransition(Duration.millis(800), kamiGraph[rowNum][columnNum], currentColor, color);
			ft.play();
		} 
		kamiGraph[rowNum][columnNum].setColor(color);
		kamiGraph[rowNum][columnNum].setChanged(true);
		
		for (KamiTriangle neighbour:kamiGraph[rowNum][columnNum].getConnected()) {
			if (!neighbour.isChanged() 
					&& !neighbour.isBlank()
					&& neighbour.getColor().equals(currentColor)) {
				spread(neighbour.getRow(), neighbour.getColumn(), color, operator);
			}
		}	
	}
	
	private void resetChangedFlag() {
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				kamiGraph[i][j].setChanged(false);
			}
		}
	}
	
	private List<Set<KamiTriangle>> getGraphgroupList() {
		List<Set<KamiTriangle>> groupList = new ArrayList<Set<KamiTriangle>>();
		Map<Integer,KamiTriangle> allElements = new HashMap<Integer,KamiTriangle>();
		
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				if (!kamiGraph[i][j].isBlank()) {
					allElements.put(i*col + j, kamiGraph[i][j]);
				}
			}
		}
		analyzeElement(groupList, allElements);
		return groupList;
	}
	
	private void analyzeElement(List<Set<KamiTriangle>> groupList, Map<Integer,KamiTriangle> remainingElements) {
		if (remainingElements.size() > 0) {
			KamiTriangle element = remainingElements.entrySet().iterator().next().getValue();
			Set<KamiTriangle> newGroup = new HashSet<KamiTriangle>();
			groupElements(element, newGroup, remainingElements);
			groupList.add(newGroup);
			analyzeElement(groupList, remainingElements);
		}
	}
	
	private void groupElements(KamiTriangle element, Set<KamiTriangle> newGroup, Map<Integer,KamiTriangle> remainingElements) {
		newGroup.add(element);
		remainingElements.remove(element.getRow() * col + element.getColumn());
		
		for (KamiTriangle neighbour:element.getConnected()) {
			if (!neighbour.isBlank()) {
				if (neighbour.getColor().equals(element.getColor())) {
					if (remainingElements.containsKey(neighbour.getRow()*col + neighbour.getColumn())) {
						groupElements(neighbour, newGroup, remainingElements);
					}
				} else {
					element.setBorder(true);
				}
			}
		}
	}
	
	
	
	/*
	private boolean solveGroupList(List<Set<KamiTriangle>> groupList, int numOfMoves, Stack<KamiSolution> solutionStack) {
		if (numOfMoves == 1) {
			Set<Color> colorSet = new HashSet<Color>();
			for (Set<KamiTriangle> group:groupList) {
				colorSet.add(group.iterator().next().getColor());
			}
			if (colorSet.size() <= 2) {
				for (Set<KamiTriangle> group:groupList) {
					KamiTriangle element = group.iterator().next();
					Set<Color> colorToChooseFrom = new HashSet<Color>(colorSet);
					colorToChooseFrom.remove(element.getColor());
					
					for (Color colorToChange:colorToChooseFrom) {
						KamiTriangle[][] temp = makeTempBackup();
						clickMachine(element.getRow(), element.getColumn(), colorToChange);
						if (validate()) {
							KamiSolution solution = new KamiSolution();
							solution.setRow(element.getRow());
							solution.setCol(element.getColumn());
							solution.setColor(colorToChange);
							solutionStack.push(solution);
							return true;
						} else {
							restoreFromTempBackup(temp);
						}
					}
				}
				return false;
			} else {
				return false;
			}
		} else {
			Set<Color> colorSet = new HashSet<Color>();
			for (Set<KamiTriangle> group:groupList) {
				colorSet.add(group.iterator().next().getColor());
			}
			if (colorSet.size() <= numOfMoves + 1) {
				for (Set<KamiTriangle> group:groupList) {
					KamiTriangle element = group.iterator().next();
					Set<Color> colorToChooseFrom = new HashSet<Color>(colorSet);
					colorToChooseFrom.remove(element.getColor());
					
					for (Color colorToChange:colorToChooseFrom) {
						KamiTriangle[][] temp = makeTempBackup();
						clickMachine(element.getRow(), element.getColumn(), colorToChange);
						if (validate()) {
							KamiSolution solution = new KamiSolution();
							solution.setRow(element.getRow());
							solution.setCol(element.getColumn());
							solution.setColor(colorToChange);
							solutionStack.push(solution);
							return true;
						} else {
							List<Set<KamiTriangle>> tempgroupList = getGraphgroupList();
							if (solveGroupList(tempgroupList, numOfMoves -1, solutionStack)) {
								KamiSolution solution = new KamiSolution();
								solution.setRow(element.getRow());
								solution.setCol(element.getColumn());
								solution.setColor(colorToChange);
								solutionStack.push(solution);
								return true;
							} else {
								restoreFromTempBackup(temp);
							}
						}
					}
				}
				return false;
			} else {
				return false;
			}
		}
	}
	
	private void clickMachine(int rowNum, int columnNum, Color color) {
		spread(rowNum, columnNum, color, "MACHINE");
		resetChangedFlag();
	}
	
	private KamiTriangle[][] makeTempBackup() {
		KamiTriangle[][] copy = new KamiTriangle[row][col];
		
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				copy[i][j] = new KamiTriangle();
				copy[i][j].setColor(kamiGraph[i][j].getColor());
			}
		}
		return copy;
	}
	
	private void restoreFromTempBackup(KamiTriangle[][] copy) {
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				kamiGraph[i][j].setColor(copy[i][j].getColor());
			}
		}
	}
	*/
}
