import java.sql.Timestamp;

public class Match {
	private int mID;
	private String lobbyType;
	private String gameMode;
	private String region;
	private String duration;
	private boolean radiantVictory;
	private Timestamp timestamp;
	private PlayerInstance[] players;

	public Match(int mID, String lobbyType, String gameMode, String region, String duration, boolean radiantVictory, Timestamp timestamp, PlayerInstance[] players) {
		this.mID = mID;
		this.lobbyType = lobbyType;
		this.gameMode = gameMode;
		this.region = region;
		this.duration = duration;
		this.radiantVictory = radiantVictory;
		this.timestamp = timestamp;
		this.players = players;
	}
}
