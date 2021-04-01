package theWitness;

import java.io.FileNotFoundException;

public interface ISaveHandler {
	
	public void save(String filename, GameCollection games) throws FileNotFoundException;
	
	public void load(String filename) throws FileNotFoundException;
}