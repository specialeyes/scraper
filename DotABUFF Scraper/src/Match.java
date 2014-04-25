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

	public int getmID() {
		return mID;
	}

	public String getLobbyType() {
		return lobbyType;
	}

	public String getGameMode() {
		return gameMode;
	}

	public String getRegion() {
		return region;
	}

	public String getDuration() {
		return duration;
	}

	public boolean isRadiantVictory() {
		return radiantVictory;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public PlayerInstance[] getPlayers() {
		return players;
	}

}
