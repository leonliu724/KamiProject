package com.kami.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kami.gui.KamiGraph;
import com.kami.gui.KamiTriangle;
import com.kami.threads.SolutionListener;
import com.kami.threads.SolutionThread;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Kami extends Application {

	private Color colorSelection = Color.WHITESMOKE;
	private Set<Color> colorPanel = new HashSet<Color>(Arrays.asList(
//			Color.MEDIUMSEAGREEN, 
//			Color.GRAY, 
//			Color.DARKSEAGREEN, 
//			Color.KHAKI,
//			Color.SADDLEBROWN, 
			Color.YELLOW,
			Color.LIGHTGREEN,
//			Color.PURPLE,
//			Color.DARKBLUE, 
//			Color.HOTPINK, 
			Color.INDIANRED, 
			Color.CADETBLUE,
//			Color.LIGHTSKYBLUE, 
//			Color.DARKORANGE, 
			Color.WHITESMOKE));
	private String mode = "DESIGN";
	private static final double SCREEN_SIZE_X = 650;
	private static final double SCREEN_SIZE_Y = 900; 
	private static final double COLOR_PANEL_LENGTH = 60;
	private static final double BUTTON_SIZE = 60;
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Kami 2 Java Application");
		Group root = new Group();
		primaryStage.setScene(new Scene(root, SCREEN_SIZE_X, SCREEN_SIZE_Y, Color.WHITE));
		
		KamiGraph graph = new KamiGraph(29, 10);
		List<KamiTriangle> list = graph.export();
		root.getChildren().addAll(list);
		graph.setLabel(root);
		Group colorGroup = new Group();
		root.getChildren().add(colorGroup);
		setColorPanel(colorGroup, colorPanel);
		
		
		Label currentMode = new Label("Current Mode: " + mode);
		Button gameMode = new Button("Game Mode");
		Button designMode = new Button("Design Mode");
		
		currentMode.setLayoutX(SCREEN_SIZE_X - 180.0);
		currentMode.setLayoutY(20.0);
		gameMode.setLayoutX(SCREEN_SIZE_X - 2.7 * BUTTON_SIZE);
		gameMode.setLayoutY(1.2 * BUTTON_SIZE);
		designMode.setLayoutX(SCREEN_SIZE_X - 2.7 * BUTTON_SIZE);
		designMode.setLayoutY(2.0 * BUTTON_SIZE);
		gameMode.setMinSize(2.1 * BUTTON_SIZE, BUTTON_SIZE / 2);
		designMode.setMinSize(2.1 * BUTTON_SIZE, BUTTON_SIZE / 2);
		
		root.getChildren().add(currentMode);
		root.getChildren().add(gameMode);
		root.getChildren().add(designMode);
		
		Label numOfMovesLabel = new Label("Number of Moves");
		TextField numOfMoves = new TextField();
		Button crack = new Button("Crack");
		Button restore = new Button("Restore");
		Label progress = new Label();
		
		numOfMovesLabel.setLayoutX(20.0);
		numOfMovesLabel.setLayoutY(SCREEN_SIZE_Y - 150.0);
		numOfMoves.setLayoutX(20.0);
		numOfMoves.setLayoutY(SCREEN_SIZE_Y - 120.0);
		numOfMoves.setMaxSize(60.0, 20.0);
		crack.setLayoutX(100);
		crack.setLayoutY(SCREEN_SIZE_Y - 80.0);
		crack.minWidth(60.0);
		restore.setLayoutX(90);
		restore.setLayoutY(SCREEN_SIZE_Y - 120.0);
		restore.minWidth(60.0);
		progress.setLayoutX(20.0);
		progress.setLayoutY(SCREEN_SIZE_Y - 90.0);
		
		root.getChildren().add(numOfMovesLabel);
		root.getChildren().add(numOfMoves);
		root.getChildren().add(crack);
		root.getChildren().add(restore);
		root.getChildren().add(progress);
		
		Label[] moves = new Label[12];
		Rectangle[] recs = new Rectangle[12];
		
		for (int i = 0; i < 12; i++) {
			moves[i] = new Label();
			recs[i] = new Rectangle();
			moves[i].setFont(new Font("Arial", 12));
			recs[i].setWidth(12);
			recs[i].setHeight(12);
			recs[i].setArcWidth(3);
			recs[i].setArcHeight(3);
			recs[i].setFill(Color.TRANSPARENT);
			
			if (i < 6) {
				moves[i].setLayoutX(200);
				moves[i].setLayoutY(SCREEN_SIZE_Y - (6 - i) * 25.0 - 20.0);
				recs[i].setX(300);
				recs[i].setY(SCREEN_SIZE_Y - (6 - i) * 25.0 - 20.0 + 3.0);
			} else {
				moves[i].setLayoutX(360);
				moves[i].setLayoutY(SCREEN_SIZE_Y - (12 - i) * 25.0 - 20.0);
				recs[i].setX(460);
				recs[i].setY(SCREEN_SIZE_Y - (12 - i) * 25.0 - 20.0 + 3.0);
			}
			root.getChildren().add(moves[i]);
			root.getChildren().add(recs[i]);
		}
		
		Button save = new Button("Save");
		Button load = new Button("Load");
		save.setLayoutX(SCREEN_SIZE_X - 90.0);
		save.setLayoutY(SCREEN_SIZE_Y - 120.0);
		save.minWidth(60.0);
		load.setLayoutX(SCREEN_SIZE_X - 90.0);
		load.setLayoutY(SCREEN_SIZE_Y - 80.0);
		load.minWidth(60.0);
		
		
		root.getChildren().add(save);
		root.getChildren().add(load);
		
		/* Listener Below */
		for (KamiTriangle element:list) {
			element.setOnMouseClicked(new EventHandler<MouseEvent>(){
				public void handle(MouseEvent arg0) {
					if (mode.equals("GAME")) {
						int num = Integer.parseInt(numOfMoves.getText());
						if (num <= 0) {
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Out of Moves");
							alert.setHeaderText(null);
							alert.setContentText("You are running out of moves");
							alert.showAndWait();
						} else {
							numOfMoves.setText(Integer.toString(--num));
							if(graph.clickGame(element.getRow(), element.getColumn(), colorSelection)) {
								Alert alert = new Alert(AlertType.INFORMATION);
								alert.setTitle("Success");
								alert.setHeaderText(null);
								alert.setContentText("Yeah...!");
								alert.showAndWait();
							}
						}
					} else if (mode.equals("DESIGN")){
						graph.clickDesign(element.getRow(), element.getColumn(), colorSelection);
					}
				}
			});
			
			element.setOnMouseDragEntered(new EventHandler<MouseEvent>(){
				public void handle(MouseEvent event) {
					if (mode.equals("DESIGN")) {
						graph.clickDesign(element.getRow(), element.getColumn(), colorSelection);
					}
					event.consume();
				}
			});
			
			element.setOnDragDetected(new EventHandler<MouseEvent>(){
				public void handle(MouseEvent event) {
					element.startFullDrag();
					event.consume();
				}
			});
		}
		
		gameMode.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	String num = numOfMoves.getText();
		    	if (num != null && num.matches("^[0-9]{1,2}$")) {
		    		mode = "GAME";
			    	currentMode.setText("Current Mode: " + mode);
		    		graph.backup();
		    		graph.setNumOfMoves(Integer.parseInt(num));
		    		numOfMoves.setEditable(false);
		    	} else {
		    		Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText(null);
					alert.setContentText("Cannot start GAME mode. Number of moves is not set correctly.");
					alert.showAndWait();
		    	}
		    }
		});
		
		designMode.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	mode = "DESIGN";
		    	currentMode.setText("Current Mode: " + mode);
		    	numOfMoves.setEditable(true);
		    }
		});
		
		
		
		crack.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	String num = numOfMoves.getText();
		    	if (num != null && num.matches("^[0-9]{1,2}$")) {
		    		reset(moves, recs);
		    		progress.setText("Cracking...");
		    		
		    		SolutionListener solutionListener = new SolutionListener(moves, recs, progress);
		    		SolutionThread solutionThread = new SolutionThread(graph, Integer.parseInt(num));
		    		solutionThread.addListener(solutionListener);
		    		new Thread(solutionThread).start();;
		    	}
		    }
		});
		
		restore.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	numOfMoves.setText(Integer.toString(graph.getNumOfMoves()));
		    	graph.restore();
		    }
		});
		
		save.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	if (!mode.equals("DESIGN")) {
		    		Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Cannot Save");
					alert.setHeaderText(null);
					alert.setContentText("You can only save in DESIGN mode.");
					alert.showAndWait();
		    	} else {
		    		FileChooser fileChooser = new FileChooser();
			    	fileChooser.setTitle("Save Kami Puzzle");
			    	fileChooser.setInitialDirectory(new File("C:\\Users\\leon\\Documents\\KamiSave"));
			    	
			    	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)","*.csv");
			    	fileChooser.getExtensionFilters().add(extFilter);
			    	
			    	File file = fileChooser.showSaveDialog(primaryStage);
			    	
			    	if (file != null) {
			    		saveKami(file, graph, numOfMoves);
			    	}
		    	}
		    }
		});
		
		load.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	FileChooser fileChooser = new FileChooser();
		    	fileChooser.setTitle("Load Kami Puzzle");
		    	fileChooser.setInitialDirectory(new File("C:\\Users\\leon\\Documents\\KamiSave"));
		    	
		    	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)","*.csv");
		    	fileChooser.getExtensionFilters().add(extFilter);
		    	
		    	File file = fileChooser.showOpenDialog(primaryStage);
		    	
		    	if (file != null) {
		    		loadKami(file, graph, root, colorGroup, colorPanel, numOfMoves, gameMode);
		    	}
		    }
		});
		
		primaryStage.show();
	}
	
	private void reset(Label[] moves, Rectangle[] recs) {
		for (int i = 0; i < moves.length; i++) {
			moves[i].setText(null);
			recs[i].setFill(Color.TRANSPARENT);
		}
	}
	
	private void setColorPanel(Group colorGroup, Set<Color> colorPanel) {
		Rectangle emptyRec = new Rectangle();
		emptyRec.setX(SCREEN_SIZE_X - 2 * COLOR_PANEL_LENGTH);
		emptyRec.setY(1.0 * COLOR_PANEL_LENGTH + 150.0);
		emptyRec.setWidth(COLOR_PANEL_LENGTH);
		emptyRec.setHeight(1.0 * COLOR_PANEL_LENGTH);
		emptyRec.setArcWidth(10);
		emptyRec.setArcHeight(10);
		emptyRec.setFill(Color.WHITE);
		emptyRec.setStroke(Color.BLACK);
		colorGroup.getChildren().add(emptyRec);
		
		emptyRec.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent arg0) {
				colorSelection = (Color) emptyRec.getFill();
			}
		});
		
		int i = 0;
		for (Color color:colorPanel) {
			Rectangle rec = new Rectangle();
			rec.setX(SCREEN_SIZE_X - 2 * COLOR_PANEL_LENGTH);
			rec.setY((i + 2) * COLOR_PANEL_LENGTH + 150.0);
			rec.setWidth(COLOR_PANEL_LENGTH);
			rec.setHeight(1.0 * COLOR_PANEL_LENGTH);
			rec.setArcWidth(10);
			rec.setArcHeight(10);
			rec.setFill(color);
			colorGroup.getChildren().add(rec);
			
			rec.setOnMouseClicked(new EventHandler<MouseEvent>(){
				public void handle(MouseEvent arg0) {
					colorSelection = (Color) rec.getFill();
				}
			});
			i++;
		}
	}
	
	private void saveKami(File file, KamiGraph graph, TextField numOfMoves) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			StringBuilder sb = new StringBuilder();
			sb.append(numOfMoves.getText()).append("\n");
			for (int i = 0; i < graph.getRow(); i++) {
				for (int j = 0; j < graph.getCol(); j++) {
					sb.append(graph.getElement(i, j).getColor().toString()).append("\n");
				}
			}
			writer.write(sb.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	private void loadKami(File file, KamiGraph graph, Group root, Group colorGroup, Set<Color> colorPanel, TextField numOfMoves, Button gameMode) {
		colorPanel = new HashSet<Color>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			int lineNum = -1;
			String line = br.readLine();
			
			while (line != null) {
				if (lineNum < 0) {
					numOfMoves.setText(line);
				} else {
					Color color = Color.valueOf(line);
					int row = lineNum / graph.getCol();
					int col = lineNum % graph.getCol();
					graph.clickDesign(row, col, color);
					colorPanel.add(color);
				}
				lineNum++;
				line = br.readLine();
			}
			colorGroup.getChildren().clear();
			colorPanel.remove(Color.WHITE);
			setColorPanel(colorGroup, colorPanel);
			gameMode.fire();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
}
