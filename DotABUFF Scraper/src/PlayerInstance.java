import java.util.HashMap;

public class PlayerInstance {
	private String player;
	private int pID;
	private String heroName;
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
	private boolean radiant;
	private String[] itemBuild;
	private HashMap<Integer, Integer> skillBuild; // lvl mapped to skillID

	public PlayerInstance(String player, int pID, String heroName, int[] stats, boolean radiant, String[] itemBuild, HashMap<Integer, Integer> skillBuild) {
		this.player = player;
		this.pID = pID;
		this.heroName = heroName;
		this.lvl = stats[0];
		this.kills = stats[1];
		this.deaths = stats[2];
		this.assists = stats[3];
		this.gold = stats[4];
		this.lastHits = stats[5];
		this.denies = stats[6];
		this.XPM = stats[7];
		this.GPM = stats[8];
		this.HD = stats[9];
		this.HH = stats[10];
		this.TD = stats[11];
		this.radiant = radiant;
		this.itemBuild = itemBuild;
		this.skillBuild = skillBuild;
	}
}
