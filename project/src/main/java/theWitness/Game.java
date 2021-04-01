package theWitness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
//import java.util.Arrays;

public class Game extends Grid {
	private int level;
	protected static int[] start = {0,0};
	
	private GameCollection gameCollection;
	
//    private ArrayList<Tile> movedLine = new ArrayList<Tile>();
//    private ArrayList<String> moveOrder = new ArrayList<String>();
    private Map<Tile,String> moves = new LinkedHashMap<Tile,String>(); //map som har Tile som key, og trekket som førte til Tile som value.
    private boolean isGameWon;
    private boolean isCorrectPath;
    private boolean firstMove;
    private boolean isGameOver;
	
	public Game(int width, int height) {
		super(width,height);
		isGameWon=false;
		isCorrectPath=false;
		firstMove=true;
		isGameOver=false;
	}
	
	public Game(Game game) {
		this(game.getWidth(),game.getHeight());
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level=level;
	}

//	public ArrayList<Tile> getMovedLine() {
//		return movedLine;
//	}
	
	public Map<Tile,String> getMoves() {
		return moves;
	}
	
	public boolean getIsGameWon() {
		return isGameWon;
	}
	
	public boolean isGameOver() {
		return isGameOver;
	}
	
	public void addGameCollection(GameCollection gameCollection) {
		this.gameCollection = gameCollection;
	}
	
	public void removeGameCollection(GameCollection gameCollection) {
		gameCollection=null;
	}
	
	public GameCollection getGameCollection() {
		return gameCollection;
	}
	
	public void clear() {
		getGrid().forEach(list -> list.forEach(tile -> tile.setLine()));
		isGameWon=false;
		isCorrectPath=false;
		firstMove=true;
		isGameOver=false;
		moves.clear();
		
	}
		
	private void drawLine(int dx, int dy, String move) {		
		//hvis spillet ikke har startet enda, må start-ruten endre seg
		if (firstMove) { 
			getStreamFromIterator().filter(tile -> tile.isStart()).forEach(tile -> {
				tile.setMovedLine();
				moves.put(tile, move);
			});
			firstMove=false;
		}
		
//		int targetX = movedLine.get(movedLine.size()-1).getX() + dx;
//		int targetY = movedLine.get(movedLine.size()-1).getY() + dy;
		//Lager en arraylist av settet av keys i moves for å hente ut den siste Tile som ble lagt til
		List<Tile> movesList = new ArrayList<Tile>(moves.keySet());
		int targetX = movesList.get(moves.size()-1).getX() + dx;
		int targetY = movesList.get(moves.size()-1).getY() + dy;
		Tile targetTile = getTile(targetX,targetY);
		
		//validerer trekket før det gjøres (store) endringer i tilstanden
		validateMove(targetX, targetY, targetTile, true); 
		
		if (movesList.size()>=2 && targetTile==movesList.get(movesList.size()-2)) { //Hvis man går tilbake
			if (movesList.size()>=3) {
				movesList.get(movesList.size()-3).setLastMovedLine();
			}
			//grid[targetY-dy][targetX-dx].setLine();
			getTile(targetX-dx, targetY-dy).setLine(); //setter tilbake til vanlig rute
			targetTile.setMovedLine();
			moves.remove(movesList.get(movesList.size()-1)); //Fjerner siste element fra moves
		}
		else {
			moves.put(targetTile, move);
			movesList.add(targetTile);
			if (targetTile.isGoal()) {
				if (checkCorrectPath()) {
					isGameWon=true;
					gameCollection.gameStateChanged(this, isGameWon);
				} else {
					isGameOver=true;
				}
			}
			targetTile.setMovedLine();
			//grid[targetY-dy][targetX-dx].setLastMovedLine();
			getTile(targetX-dx, targetY-dy).setLastMovedLine();
			if (movesList.size()>=3) {
				movesList.get(movesList.size()-3).setMovedLine(); //ruten to steg bak den siste skal bli "vanlig" igjen slik at den får kollisjon
			}
		}
		if (isGameWon) {
			movesList.forEach(tile -> tile.setMovedLine());
			System.out.println(moves.values());
		}
	}

    public void moveUp() {
        drawLine(0, -1, "Up");
	    System.out.println(this);
    }

    public void moveDown() {
        drawLine(0, 1,"Down");
	    System.out.println(this);
    }

    public void moveLeft() {
        drawLine(-1, 0,"Left");
	    System.out.println(this);
    }

    public void moveRight() {
        drawLine(1, 0,"Right");
	    System.out.println(this);
    }
	
	// Oppgave 4 a)
	/*private boolean canMoveTo(int dx, int dy) {
		
		// Snake head coordinates
		int targetX = snake.get(0).getX() + dx;
		int targetY = snake.get(0).getY() + dy;
		
		if(!isTile(targetX, targetY)) {
			return false;
		}
		
		Tile targetTile = getTile(targetX, targetY);
		boolean tileIsSnakeTail = (targetTile == snake.get(snake.size()-1));
		
		return !targetTile.hasCollision() || tileIsSnakeTail;
	}*/
    
    private boolean checkCorrectPath() {                                       //WTF?????
    	List<Tile> movedLine = new ArrayList<Tile>(moves.keySet());
    	List<String> moveOrder = new ArrayList<String>(moves.values());
    	System.out.println(movedLine);
    	System.out.println(moveOrder);
    	
    	List<Set<Tile>> partitions = new ArrayList<Set<Tile>>();    	
    	partitions.add(new HashSet<Tile>());
    	partitions.add(new HashSet<Tile>());
    	
    	HashSet<Character> sector1 = new HashSet<Character>();
    	HashSet<Character> sector2 = new HashSet<Character>();
    	
    	for (int i=1;i<movedLine.size();i++) {
    		AtomicInteger nextX1 = new AtomicInteger(0); //AtomicInteger hjelper med objektreferanser
        	AtomicInteger nextY1 = new AtomicInteger(0);
        	AtomicInteger nextX2 = new AtomicInteger(0);
        	AtomicInteger nextY2 = new AtomicInteger(0);
    		AtomicInteger counterObject = new AtomicInteger(0);
    		//int counter = 0;
    		//Tile nextTile = new Tile();
    		//(movedLine.get(i+1).getX()-movedLine.get(i).getX()!=1 && movedLine.get(i+1).getY()-movedLine.get(i).getY()!=1) ||
    		if (moveOrder.get(i).equals("Up")) {
    			nextX1.set(-1);   				
    			nextX2.set(1);
    			nextY1=counterObject;
    			nextY2=counterObject;
    		}
    		if (moveOrder.get(i).equals("Down")) {
    			nextX1.set(-1);    				
    			nextX2.set(1);
    			nextY1=counterObject;
    			nextY2=counterObject;
    		}
    		if (moveOrder.get(i).equals("Left")) {
    			nextX1=counterObject;    				
    			nextX2=counterObject;
    			nextY1.set(-1);
    			nextY2.set(1);
    		}
    		if (moveOrder.get(i).equals("Right")) { //Funker bare rett ved siden av
    			nextX1=counterObject;    				
    			nextX2=counterObject;
    			nextY1.set(1);
    			nextY2.set(-1);
    		}
    		//Tile[][] nextTile = new Tile[movedLine.get(i).getX()+nextX1.get()][movedLine.get(i).getY()-nextY1.get()];
    		//System.out.println(nextTile);
    		while (isTile(movedLine.get(i).getX()+nextX1.get(),movedLine.get(i).getY()-nextY1.get()) && !getTile(movedLine.get(i).getX()+nextX1.get(),movedLine.get(i).getY()-nextY1.get()).isMovedLine()) { 
    			if (getTile(movedLine.get(i).getX()+nextX1.get(),movedLine.get(i).getY()-nextY1.get()).isWhite() || getTile(movedLine.get(i).getX()+nextX1.get(),movedLine.get(i).getY()-nextY1.get()).isBlack()) {
    				partitions.get(0).add(getTile(movedLine.get(i).getX()+nextX1.get(),movedLine.get(i).getY()-nextY1.get()));
    				sector1.add(getTile(movedLine.get(i).getX()+nextX1.get(),movedLine.get(i).getY()-nextY1.get()).getType());
    			}
    			//counter++;
    			counterObject.getAndIncrement();
    		}
    		while (isTile(movedLine.get(i).getX()+nextX2.get(),movedLine.get(i).getY()-nextY2.get()) && !getTile(movedLine.get(i).getX()+nextX2.get(),movedLine.get(i).getY()-nextY2.get()).isMovedLine()) {
    			if (getTile(movedLine.get(i).getX()+nextX2.get(),movedLine.get(i).getY()-nextY2.get()).isWhite() || getTile(movedLine.get(i).getX()+nextX2.get(),movedLine.get(i).getY()-nextY2.get()).isBlack()) {
    				partitions.get(1).add(getTile(movedLine.get(i).getX()+nextX2.get(),movedLine.get(i).getY()-nextY2.get()));
    				sector2.add(getTile(movedLine.get(i).getX()+nextX2.get(),movedLine.get(i).getY()-nextY2.get()).getType());
    			}
    			//counter++;
    			counterObject.getAndIncrement();
    		}
    	}
    	System.out.println(sector1);
    	System.out.println(sector2);
    	if (sector1.size()>1 || sector2.size()>1) { //hvis det er mer enn 1 element i hvert sektor-set betyr det at det er en hvit og en svart i en sektor
    		return false;
    	}
    	return true;
    }
    
    private boolean validateMove(int x, int y, Tile targetTile, boolean throwException) {		
		if (!isTile(x, y)) {
			if (throwException) {
				throw new IllegalArgumentException("Invalid move");
			}
			return false;
		}
		if (isGameWon) {
			if (throwException) {
				throw new IllegalStateException("Game is already completed");
			}
			return false;
		}
		if (targetTile.hasCollision()) {
			throw new IllegalArgumentException("Invalid move");
		}
		return true;
    }
    
    @Override
    public String toString() {
    	String boardString = super.toString();
		if (isGameWon) {
			boardString += "\n\nGame won!";
		}
		return boardString;
    }
	
	public static void main(String[] args) {
		 Game game = new Game(7, 7);
		 
		 game.getTile(1, 1).setWhite();
		 game.getTile(5,5).setBlack();
		 game.getTile(0, game.getHeight()-1).setStart();
		 game.getTile(game.getWidth()-1, 0).setGoal();
		 System.out.println(game);
		 System.out.println(game.getTile(0, 6));
		 game.moveUp();
		 game.moveUp();
		 //System.out.println(game.getMovedLine());
		 game.moveDown();
		 /*game.moveDown();
		 game.moveDown();
		 game.moveDown();
		 game.moveDown();
		 game.moveLeft();
		 game.moveRight();*/
		/*Game game = new Game(3, 7);
		 
		 game.getTile(1, 1).setWhite();
		 game.getTile(1,3).setBlack();
		 game.getTile(1, 5).setWhite();
		 game.getTile(0, 2).setStart();
		 game.getTile(game.getWidth()-1, 2).setGoal();
		 System.out.println(game);
		 game.moveRight();
		 game.moveRight();

		 for (int i=0;i<game.getMovedLine().size();i++) {
			 System.out.println(game.getMovedLine().get(i).getX() + "," + game.getMovedLine().get(i).getY());
		 }
		 System.out.println(game.getMovedLine());*/
	}

}
