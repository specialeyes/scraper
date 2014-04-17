import java.util.HashMap;

public class MetaDotA {
	private HashMap<String, Skill> skills;	// skillID -> skill
	private HashMap<String, Hero> heroes;   // heroID  -> hero
	private HashMap<String, Item> items;	// itemID  -> item
	
	//Item-Hero Aggregates
	public HashMap<Integer, HashMap<String, String>> numItemOnHero;     // matchesUsed -> <itemID -> heroID>
	private HashMap<Double, HashMap<String, String>> winRateItemOnHero; // winRate 	   -> <itmeID -> heroID>
	
	private HashMap<Double, Integer>  playerWinRates; 	//winRate -> playerID
	//TODO: Number of times an item is bought by a player
	//TODO: Number of games an item has been used in
}
