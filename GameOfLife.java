/*
Author: Adel Kassem

This program is a simulation that is played on a grid of cells. The user can click on a cell to 
toggle its status (live to dead, or dead to live). When the simulation is running, a cell stays 
alive in the next generation of the simulation if its number of live neighbors in the current 
generation is either 2 or 3. A non-living cell comes to life in the next generation if its current 
number of live neighbors is exactly 3
**************************************************************************************************
Extra Features
  - If all cells die in an animation then the animation automatically stops - the user does not need
    to click the stop button to stop it
  - At first when the program starts, there is three different kinds of color for the cells. The first 
    color is for live cells, the second color is for cells that were alive but are now dead, and the 
    third color is for cells that have never been alive so far in the current simulation. The clear 
    button clears everything out to the never alive state.
  - There is a menu item for the different kinds of cells that allows the user to change their color 
    from a list of five pre-defined colors
  - A Random button added onto the program - when it is clicked the game is cleared and repopulated 
    randomly
*/


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class GameOfLife extends Application{
    // Declare final int value
    final int DIM = 32;
	
    // Create two dimensional cell array
    private Cell[][] cell =  new Cell[DIM][DIM];
	
    // Create Slider with min value of 0 and max value 100
    private Slider slHorizontal = new Slider(0, 100, 5);
	
    // Create Timeline
    private Timeline animation = new Timeline(new KeyFrame(Duration.millis(1000), e -> Game()));
	
    // Create Button
    private Button btnPlay = new Button("Play");
    private Button btnStep = new Button("Step");
    private Button btnRandom = new Button("Random");
	
    // Create RadioButton 
    private RadioButton rbLife = new RadioButton("Life");
    private RadioButton rbHighLife = new RadioButton("HighLife");
    
    // Pane to hold cell
    GridPane gridPane = new GridPane(); 
    
    // Create two dimensional boolean array 
    private boolean[][] nextState = new boolean[DIM][DIM];
    private boolean[][] currentState = new boolean[DIM][DIM];
    
    // Initialize cell color
    String x = "green";
    String y = "orange";
    String z = "black";
	  
    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {
	    // Initialize cell
	    for (int row = 0; row < DIM; row++)
	      for (int col = 0; col < DIM; col++)
	        gridPane.add(cell[row][col] = new Cell(), col, row);
	    
	    btnStep.setPrefSize(70, 40);
	    btnStep.setOnAction(e -> Game());
	    btnPlay.setPrefSize(70, 40);
	    btnPlay.setOnAction(e -> animation());
	    btnRandom.setPrefSize(90, 40);
	    btnRandom.setOnAction(e -> random());

	    Button btnClear = new Button("Clear");
	    btnClear.setPrefSize(70, 40);
	    btnClear.setOnAction(e -> clear());
	    
	    slHorizontal.setShowTickLabels(false);
	    slHorizontal.setShowTickMarks(false); 
	    
	    // Create menu and menu items
	    MenuBar menuBar = new MenuBar();    
		
	    Menu menuFile = new Menu("File");
	    Menu menuFile2 = new Menu("Live Cell Color");
	    Menu menuFile3 = new Menu("Dead Cell Color");
	    Menu menuFile4 = new Menu("Never Alive Cell Color");
	    menuBar.getMenus().addAll(menuFile, menuFile2, menuFile3, menuFile4);
		
	    MenuItem menuItemClear = new MenuItem("Clear");
	    MenuItem menuItemSaveAs = new MenuItem("Save As...");
	    MenuItem menuItemLoadGame = new MenuItem("Load Game");
	    MenuItem menuItemExit = new MenuItem("Exit");
	    menuFile.getItems().addAll(menuItemClear, new SeparatorMenuItem(), 
				menuItemSaveAs, menuItemLoadGame, new SeparatorMenuItem(),
				menuItemExit);
		
	    menuItemClear.setOnAction(e -> clear());
	    menuItemSaveAs.setOnAction(e -> saveAs(primaryStage));
	    menuItemLoadGame.setOnAction(e -> loadGame(primaryStage));
	    menuItemExit.setOnAction(e -> System.exit(0));
	    
	    menuItemClear.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
	    menuItemSaveAs.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
	    menuItemLoadGame.setAccelerator(KeyCombination.keyCombination("Ctrl+L"));
	    menuItemExit.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
		
	    MenuItem menuItemBlack = new MenuItem("Black");
	    MenuItem menuItemGreen = new MenuItem("Green");
	    MenuItem menuItemYellow = new MenuItem("Yellow");
	    MenuItem menuItemRed = new MenuItem("Red");
	    MenuItem menuItemBlue = new MenuItem("Blue");
	    menuFile2.getItems().addAll(menuItemBlack, menuItemGreen, menuItemYellow, menuItemRed, menuItemBlue);
	    menuItemBlack.setOnAction(e -> black(0));
	    menuItemGreen.setOnAction(e -> green(0));
	    menuItemYellow.setOnAction(e -> yellow(0));
	    menuItemRed.setOnAction(e -> red(0));
	    menuItemBlue.setOnAction(e -> blue(0));
		
	    MenuItem menuItemBlack1 = new MenuItem("Black");
	    MenuItem menuItemGreen1 = new MenuItem("Green");
	    MenuItem menuItemYellow1 = new MenuItem("Yellow");
	    MenuItem menuItemRed1 = new MenuItem("Red");
	    MenuItem menuItemBlue1 = new MenuItem("Blue");
	    menuFile3.getItems().addAll(menuItemBlack1, menuItemGreen1, menuItemYellow1, menuItemRed1, menuItemBlue1);
	    menuItemBlack1.setOnAction(e -> black(1));
	    menuItemGreen1.setOnAction(e -> green(1));
	    menuItemYellow1.setOnAction(e -> yellow(1));
	    menuItemRed1.setOnAction(e -> red(1));
	    menuItemBlue1.setOnAction(e -> blue(1));
		
	    MenuItem menuItemBlack2 = new MenuItem("Black");
	    MenuItem menuItemGreen2 = new MenuItem("Green");
	    MenuItem menuItemYellow2 = new MenuItem("Yellow");
	    MenuItem menuItemRed2 = new MenuItem("Red");
	    MenuItem menuItemBlue2 = new MenuItem("Blue");
	    menuFile4.getItems().addAll(menuItemBlack2, menuItemGreen2, menuItemYellow2, menuItemRed2, menuItemBlue2);
	    menuItemBlack2.setOnAction(e -> black(2));
	    menuItemGreen2.setOnAction(e -> green(2));
	    menuItemYellow2.setOnAction(e -> yellow(2));
	    menuItemRed2.setOnAction(e -> red(2));
	    menuItemBlue2.setOnAction(e -> blue(2));
		
	    rbLife.setSelected(true);
	    rbLife.setOnAction(e -> {rbLife.setSelected(true);});
	    rbHighLife.setOnAction(e -> {rbHighLife.setSelected(true);});
	    
	    // Create vBox and vBox items
	    VBox paneForRadioButtons = new VBox(5);
	    paneForRadioButtons.getChildren().addAll(rbLife, rbHighLife);
	    
	    ToggleGroup group = new ToggleGroup();
	    rbLife.setToggleGroup(group);
	    rbHighLife.setToggleGroup(group);
	    
	    // Create hBox and hBox items
	    HBox hBox = new HBox(20);
	    hBox.setPadding(new Insets(5, 5, 5, 5));
	    hBox.setAlignment(Pos.CENTER);
	    hBox.getChildren().addAll(btnStep, btnPlay, new Label("Rate:   "), slHorizontal, btnClear, btnRandom, paneForRadioButtons );
	    
	    BorderPane borderPane = new BorderPane();
	    borderPane.setTop(menuBar);
	    borderPane.setCenter(gridPane);
	    borderPane.setBottom(hBox);
	    
	    // Create a scene and place it in the stage
	    Scene scene = new Scene(borderPane, 850, 850);
	    primaryStage.setTitle("Game Of Life"); // Set the stage title
	    primaryStage.setScene(scene); // Place the scene in the stage
	    primaryStage.show(); // Display the stage 
	}
	
	public void Game() {
		for (int row = 0; row < DIM; row++){
		      for (int col = 0; col < DIM; col++){
		    	  currentState[row][col] = cell[row][col].isAlive;
		      }
		}
		
		for (int row = 0; row < DIM; row++){
			for (int col = 0; col < DIM; col++){
				int liveCount = 0;
				for(int i = -1; i <= 1; i++){
					for(int j = -1; j <= 1; j++){
						if(cell[(row + i + DIM) % DIM][(col + j + DIM) % DIM].isAlive)
							liveCount++;
					}
				}
				if(cell[row][col].isAlive){
					cell[row][col].wasEverAlive = true;
				}
				if(cell[row][col].isAlive){
					liveCount--;
				}
				if(currentState[row][col]){
					if(liveCount == 2 || liveCount == 3){
						nextState[row][col] = true;
					}
					else{
						nextState[row][col] = false;
					}
				}else{
					if(rbLife.isSelected()){
						if(liveCount == 3){
							nextState[row][col] = true;
						}
					}else{
						if(liveCount == 3 || liveCount == 6){
							nextState[row][col] = true;
						}
					}
				}
			}
		}
		for (int row = 0; row < DIM; row++){
			for (int col = 0; col < DIM; col++){
				cell[row][col].updateState(nextState[row][col], cell[row][col].wasEverAlive);
			}
		}
	}
	
	public void isEveryCellDead () {
		if(btnPlay.getText() == "Stop"){
			 boolean check = false;
			 for (int row = 0; row < DIM; row++){
				 for (int col = 0; col < DIM; col++){
					 if(cell[row][col].isAlive){
						 check = true;
						 break;
					 }
				 }
			 }
			 if(check == false){
				 animation.stop(); 
				 btnPlay.setText("Play");
				 btnStep.setDisable(false);
			 }
		}
	}
	public void animation() {
		if(btnPlay.getText() == "Play"){
			    animation.setCycleCount(Timeline.INDEFINITE);
			    animation.rateProperty().bind(slHorizontal.valueProperty());
			    animation.play(); // Start animation
			    btnPlay.setText("Stop");
			    btnStep.setDisable(true);
		}else{
			 animation.stop(); // Stop animation
			 btnPlay.setText("Play");
			 btnStep.setDisable(false);
		}  
	}
	public void random() {
		clear();
		int value;
		for (int row = 0; row < DIM; row++){
		      for (int col = 0; col < DIM; col++){
		    	  value = (Math.random() <= 0.5) ? 1 : 2;
		    	  if(value == 1){
		    		  cell[row][col].isAlive = true;
		    	  }else{
		    		  cell[row][col].isAlive = false;
		    	  }
		      }
		}
		Game();
	}

	
	public class Cell extends Pane {
	    // Token used for this cell
	    private boolean isAlive;
	    private boolean wasEverAlive;
	    
	    public Cell() {
	      isAlive = false;
	      wasEverAlive = false;
	      setStyle("-fx-border-width:0.2; -fx-border-color: white; -fx-background-color: black"); 
	      this.setPrefSize(800, 800);
	      this.setOnMouseClicked(e -> handleMouseClick());
	    }
	    
	    private void updateState(boolean Alive, boolean wasAlive){
	    	this.isAlive = Alive;
	    	this.wasEverAlive = wasAlive;
	    	paintState(x, y, z);
	    	isEveryCellDead();
	    }
	    
	    /* Handle a mouse click event */
	    private void handleMouseClick() {
	    	if(isAlive){
	    		unclicked();
	    	}else{
	    		clicked();
	    	}
	    }
	    
	    private void clicked(){
	    	isAlive = true;
    		wasEverAlive = true;
    		paintState(x, y, z);
	    }
	    private void unclicked(){
	    	isAlive = false;
	    	wasEverAlive = false;
	    	paintState(x, y, z);
	    }
	    
	    private void paintState(String x, String y, String z) {
	    	if (isAlive == true){
	    		setStyle("-fx-border-width:0.2; -fx-border-color: white; -fx-background-color: " + x);
	    	}else if(wasEverAlive == true){
	    		setStyle("-fx-border-width:0.2; -fx-border-color: white; -fx-background-color: " + y);
	    	}else{
	    		setStyle("-fx-border-width:0.2; -fx-border-color: white; -fx-background-color: " + z);
	    	}
	    }
	}
	
	public void clear() {
		for (int row = 0; row < DIM; row++){
		  for (int col = 0; col < DIM; col++) {
			nextState[row][col] = false;
			cell[row][col].updateState(false, false); 
			x = "green";
			y = "orange"; 
			z =	"black";
			cell[row][col].paintState(x, y, z);
		  }
		}
		if(btnPlay.getText() == "Stop"){
			animation.stop();
			btnPlay.setText("Play");
			btnStep.setDisable(false);
		}
	}
	
	// This method saves the state of the game
	private void saveAs(Stage primaryStage) {
		 animation.stop();
		 btnPlay.setText("Play");
		 btnStep.setDisable(false);
		 FileChooser fileChooser = new FileChooser();
		 fileChooser.setInitialDirectory(new File("."));
		 fileChooser.setTitle("Enter file name");
		 fileChooser.getExtensionFilters().add(new ExtensionFilter("Game Of Life Files", "*.lif"));
		 File selectedFile = fileChooser.showSaveDialog(primaryStage);
		 if (selectedFile != null) {
			 try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(selectedFile));) {
				 boolean[][] state = new boolean[DIM][DIM];
				 boolean lifeState;
				 for (int row = 0; row < DIM; row++){
					  for (int col = 0; col < DIM; col++) {
						  state[row][col] = cell[row][col].isAlive;
					  }
			 	 }
				 if(rbLife.isSelected()){
					  lifeState = true;
				 }else{
					 lifeState = false;
				 }
				 output.writeObject(state);
				 output.writeBoolean(lifeState);
			  }
			  catch (IOException ex) {
				ex.printStackTrace();
			  }
		}
	}
			
	// This method loads saved games
	private void loadGame(Stage primaryStage) {
		 animation.stop();
		 btnPlay.setText("Play");
		 btnStep.setDisable(false);
		 clear();
		 FileChooser fileChooser = new FileChooser();
		 fileChooser.setInitialDirectory(new File("."));
		 fileChooser.setTitle("Enter file name");
		 fileChooser.getExtensionFilters().add(new ExtensionFilter("Game Of Life Files", "*.lif"));
		 File selectedFile = fileChooser.showOpenDialog(primaryStage);
		 if (selectedFile != null){
			 try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(selectedFile));) {
				 boolean[][] state = (boolean[][])(input.readObject());
				 boolean lifeState = (boolean)(input.readBoolean());
				 if(lifeState){
					 rbLife.fire();
				 }else{
					 rbHighLife.fire();
				 }
				 for (int row = 0; row < DIM; row++){
					  for (int col = 0; col < DIM; col++) {
						  nextState[row][col] = state[row][col];
					  }
			 	 }
				Game();
			 }
			 catch (Exception ex) {
			    ex.printStackTrace();
			 }
		 }
	}
	
	// Changes the color of the cell to black 
	public void black(int var) {
		if(var == 0){
			x = "black";
			cell[0][0].paintState(x, y, z);
		}else if(var == 1){
			y = "black";
			cell[0][0].paintState(x, y, z);
		}else{
			z = "black";
			cell[0][0].paintState(x, y, z);
		}
	}
	
	// Changes the color of the cell to green
	public void green(int var) {
		if(var == 0){
			x = "green";
			cell[0][0].paintState(x, y, z);
		}else if(var == 1){
			y = "green";
			cell[0][0].paintState(x, y, z);
		}else{
			z = "green";
			cell[0][0].paintState(x, y, z);
		}
	}

	// Changes the color of the cell to yellow
	public void yellow(int var) {
		if(var == 0){
			x = "yellow";
			cell[0][0].paintState(x, y, z);
		}else if(var == 1){
			y = "yellow";
			cell[0][0].paintState(x, y, z);
		}else{
			z = "yellow";
			cell[0][0].paintState(x, y, z);
		}
	}

	public void red(int var) {
		if(var == 0){
			x = "red";
			cell[0][0].paintState(x, y, z);
		}else if(var == 1){
			y = "red";
			cell[0][0].paintState(x, y, z);
		}else{
			z = "red";
			cell[0][0].paintState(x, y, z);
		}
	}

	public void blue(int var) {
		if(var == 0){
			x = "blue";
			cell[0][0].paintState(x, y, z);
		}else if(var == 1){
			y = "blue";
			cell[0][0].paintState(x, y, z);
		}else{
			z = "blue";
			cell[0][0].paintState(x, y, z);
		}
	}
	  
	
	 /**
	   * The main method is only needed for the IDE with limited
	   * JavaFX support. Not needed for running from the command line.
	   */
	  public static void main(String[] args) {
	    launch(args);
	  }
}
