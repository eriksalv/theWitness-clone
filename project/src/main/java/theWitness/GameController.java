package theWitness;

import java.io.IOException;
import java.util.Iterator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameController {
	
	@FXML private SaveGameController saveGameController;
	
	private Game game;
	private GameCollection games;
	private int gameIndex = 1;
	
	@FXML
	Pane board;
	
	@FXML Text winText = new Text();
	
	@FXML
	private void initialize() {
		Game game1 = new Game(6,6);
		games = new GameCollection("games",new Game(5,5), new Game(10,10));
		games.addGame(game1);
		setInitialGameState();		 
		createBoard();
		drawBoard();
	}
	
	private void setInitialGameState() {
		System.out.println(games.getIsGamesWon());
		game = games.getGames().getOrDefault(gameIndex, null);
		game.clear();
		 
		game.getTile(1, 1).setWhite();
		game.getTile(3, 1).setWhite();
		game.getTile(1, 3).setWhite();
		game.getTile(3, 3).setBlack();
		//game.getTile(5,5).setBlack();
		game.getTile(0, game.getHeight()-1).setStart();
		game.getTile(game.getWidth()-1, 0).setGoal();
	}
	
	private void createBoard() {
		board.getChildren().clear();
		double tileHeight = board.getPrefHeight()/game.getHeight();
		double tileWidth= board.getPrefWidth()/game.getWidth();
		game.getStreamFromIterator().forEach(gameTile -> {
			//tileHeight=getTileSize(y,x)[0];
			//tileWidth=getTileSize(y,x)[1];
			Pane tile = new Pane();
			tile.setPrefHeight(tileHeight);
			tile.setPrefWidth(tileWidth);
			tile.setTranslateX((40+gameTile.getX()*tileWidth));
			tile.setTranslateY((10+gameTile.getY()*tileHeight));
			/*tile.setStyle("-fx-border-color: black;\n"
	                + "-fx-border-width: 1;\n"
	                + "-fx-border-style: solid;\n"
	                + "-fx-border-radius:10px;\n"
	                + "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.8), 10, 0, 0, 0);\n"
	                + "-fx-background-color:green;");*/
			tile.getStyleClass().add("tile");
			board.getChildren().add(tile);
		});
	}
	private void drawBoard() {
		for (int y = 0; y < game.getHeight(); y++) {
			for (int x = 0; x < game.getWidth(); x++) {
				board.getChildren().get(y*game.getWidth() + x).setStyle("-fx-background-color: " + getTileColor(game.getTile(x, y))[1] + ";");
				if (game.getTile(x, y).isBlack() || game.getTile(x, y).isWhite()) {
					board.getChildren().get(y*game.getWidth() + x).setStyle("-fx-background-color: " + getTileColor(game.getTile(x, y))[1] + "; -fx-border-width: " + (board.getPrefWidth()/game.getWidth())/3.5 + ";");
				}
				board.getChildren().get(y*game.getWidth()+x).getStyleClass().add(getTileColor(game.getTile(x, y))[0]);
			}
		}
		
		if (game.getIsGameWon()) {
			winText.setText("Du vant!");
			winText.setStyle("-fx-font-size: 100px");
			winText.setFill(Color.GREEN);
			//winText.setStyle("-fx-background-color:blue");
			winText.setTranslateX(160.0);
			winText.setTranslateY(200.0);
			board.getChildren().add(winText);
		}
		if (game.isGameOver()) {
			handleReset(); //resetter automatisk når linjen ikke er korrekt
		}
	}
	@FXML
	public void keyListener(KeyEvent e) {
		if (e.getCode() == KeyCode.UP) {
			handleUp();
		}
		if (e.getCode() == KeyCode.DOWN) {
			handleDown();
		}
		if (e.getCode() == KeyCode.LEFT) {
			handleLeft();
		}
		if (e.getCode() == KeyCode.RIGHT) {
			handleRight();
		}
		if (e.getCode() == KeyCode.R) {
			handleReset();
		}
		if (e.getCode() == KeyCode.N) {
			handleNextGame();
		}
		if (e.getCode() == KeyCode.DELETE) {
			handlePrevGame();
		}
	}
	@FXML
	public void handleNextGame() {
		if (games.hasNextLevel(gameIndex)) { //Hvis isGameWon er true for gamet man er på
			gameIndex++;
			setInitialGameState();
			createBoard();
			drawBoard();
		}
	}
	@FXML
	public void handlePrevGame() {
		if (games.hasPrevLevel(gameIndex)) {
			gameIndex--;
			setInitialGameState();
			createBoard();
			drawBoard();
		}
	}
	@FXML
	public void handleUp() {
		game.moveUp();
		drawBoard();
	}
	@FXML
	public void handleDown() {
		game.moveDown();
		drawBoard();
	}
	@FXML
	public void handleLeft() {
		game.moveLeft();
		drawBoard();
	}
	@FXML
	public void handleRight() {
		game.moveRight();
		drawBoard();
	}
	@FXML
	public void handleReset() {
		if (board.getChildren().contains(winText)) {
			board.getChildren().remove(winText);
		}
		setInitialGameState();
		drawBoard();
	}
	@FXML
	public void openSaveView(ActionEvent e) throws IOException { //åpner nytt vindu for lagring
		FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SaveGame.fxml"));
        Parent tableViewParent = loader.load();
        
        Scene tableViewScene = new Scene(tableViewParent);
        
        //access the controller and call a method
        SaveGameController controller = loader.getController();
        controller.initData(games);
        
        //This line gets the Stage information
        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
        
        window.setScene(tableViewScene);
        window.show();
	}
	private String[] getTileColor(Tile tile) {
    	if (tile.isWhite()) { 
    		//return "#1db121"; //kan rendomizes
    		//return "white";
    		String[] color = {"white", "white"};
    		return color;
    	} else if(tile.isBlack()) {
    		//return "#24d628";
    		//return "black";
    		//tile.setStyle("-fx-border-width:20;");
    		String[] color = {"black", "black"};
    		return color;
    	} else if(tile.isMovedLine() || tile.isLastMovedLine()) {
    		//return "#a26f42";
    		//return "movedLine";
    		String[] color = {"movedLine", "#00bebe"};
    		return color;
    	} else if(tile.isStart()) {
    		//return "#e5303a";
    		//return "start";
    		String[] color = {"start", "#e5303a"};
    		return color;
    	} else if(tile.isGoal()) {
    		//return "#f6ec5a";
    		//return "goal";
    		String[] color = {"goal", "#f6ec5a"};
    		return color;
    	} else {
    		//return "normal";
    		String[] color = {"normal", "#00003f"};
    		return color;
    	}
    }
	/*private double[] getTileSize(int y, int x) {
		Tile tile = game.getTile(x, y);
		if (tile.isLine() || tile.isMovedLine() || tile.isLastMovedLine() || tile.isGoal() || tile.isStart()) {
			if (tile.getX()%2==0 && tile.getY()!=0 && tile.getY()!=game.getHeight()-1) {
				double[] arr = {board.getPrefHeight()/game.getHeight(), board.getPrefWidth()/game.getWidth()*0.5};
				return arr;
			}
			double[] arr = {board.getPrefHeight()/game.getHeight()*0.5, board.getPrefWidth()/game.getWidth()};
			return arr;
		}
		else {
			double[] arr = {board.getPrefHeight()/game.getHeight(), board.getPrefWidth()/game.getWidth()};
			return arr;
		}
	}*/

}
