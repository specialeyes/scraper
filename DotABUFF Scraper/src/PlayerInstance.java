import java.util.HashMap;


public class PlayerInstance {
	private int pID;
	private int heroID;
	private int lvl;
	private int kills;
	private int deaths;
	private int assists;
	private int gold;
	private int lastHits;
	private int denies;
	private int XPM;
	private int GPM;
	private int HD;
	private int HH;
	private int TD;
	private char team;
	private int[] itemBuild;
	private HashMap<Integer, Integer> skillBuild; //lvl mapped to skillID
	
	public PlayerInstance(int pID, int heroID, int lvl, int kills, int deaths, int assists, int gold,
			int lastHits, int denies, int XPM, int GPM, int HD, int HH, int TD, char team, 
			int[] itemBuild, HashMap<Integer, Integer> skillBuild) {
		this.pID = pID;
		this.heroID = heroID;
		this.lvl = lvl;
		this.kills = kills;
		this.deaths = deaths;
		this.assists = assists;
		this.gold = gold;
		this.lastHits = lastHits;
		this.denies = denies;
		this.XPM = XPM;
		this.GPM = GPM;
		this.HD = HD;
		this.HH = HH;
		this.TD = TD;
		this.team = team;
		this.itemBuild = itemBuild;
		this.skillBuild = skillBuild;
	}
}
