package theWitness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

public class GameCollection {	
	private String name;
	private Map<Integer,Game> games;
	private Map<Integer,Boolean> isGamesWon;
	private int levelCount = 1;
	
	public GameCollection(String s, Game... games) {
		this.games=new TreeMap<Integer,Game>();
		this.isGamesWon=new TreeMap<Integer,Boolean>();
		Stream.of(games).forEach(game -> {
			game.setLevel(levelCount);
			this.games.put(levelCount, game);
			this.isGamesWon.put(levelCount, game.getIsGameWon());
			levelCount++;
		});
		this.games.values().forEach(game -> game.addGameCollection(this)); //legger til denne GameCollection i hvert game sin GameCollection-liste
		this.name=name;
	}
	
	public Map<Integer,Boolean> getIsGamesWon() {
		return isGamesWon;
	}
	
	public Map<Integer,Game> getGames() {
		return games;
	}
	
	public void addGame(Game game, int level) {
		if (level>=levelCount+2) {
			throw new IllegalStateException("Only " + levelCount + " levels exists");
		}
		if (games.keySet().contains(level)) {
			games.put(level, game); //overskriver game som allerede eksisterer
			isGamesWon.put(level, game.getIsGameWon());
			game.addGameCollection(this);
			game.setLevel(level);
		}
		else {
			addGame(game);
		}
	}
	public void addGame(Game game) {
		games.put(levelCount, game); //legger til et nytt game eller overskriver et som allerede eksisterer (med samme LEVEL)
		isGamesWon.put(levelCount, game.getIsGameWon());
		
		game.addGameCollection(this);
		game.setLevel(levelCount);
		levelCount++;
	}
	public void removeGame(Game game, int level) { //fak
		if (!games.keySet().contains(level)) {
			throw new IllegalStateException("Cannot remove game that does not exist");
		}
		games.remove(level);
		isGamesWon.remove(level);
		games.keySet().stream().filter(key -> key>level).forEach(key -> key--);
		isGamesWon.keySet().stream().filter(key -> key>level).forEach(key -> key--);
		
		game.removeGameCollection(this);
		games.values().stream().filter(g -> g.getLevel()>level).forEach(g -> g.setLevel(g.getLevel()-1)); //minker nivået med 1 til alle games etter gamet som ble fjernet 
		levelCount--;
	}
	public void gameStateChanged(Game game,boolean isGameWon) {
		isGamesWon.put(game.getLevel(), isGameWon);
	}
	
	public boolean hasNextLevel(int currentLevel) {
		return getIsGamesWon().getOrDefault(currentLevel, null) && getIsGamesWon().containsKey(currentLevel+1);//skjekker at isGameWon er satt til true før neste game
	}
	
	public boolean hasPrevLevel(int currentLevel) {
		return getIsGamesWon().containsKey(currentLevel-1);
	}
	@Override
	public String toString() {
		return name;
	}
}