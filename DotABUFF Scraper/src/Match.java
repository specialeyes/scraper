import java.sql.Time;
import java.sql.Timestamp;

public class Match {
	private int mID;
	private String lobbyType;
	private String gameMode;
	private String region;
	private Time duration;
	private boolean dRVictory;
	private Timestamp timestamp;
	private PlayerInstance[] players;
	
	public Match(int mID, String lobbyType, String gameMode, String region, Time duration, 
			boolean dRVictory, Timestamp timestamp, PlayerInstance[] players) {
		this.mID = mID;
		this.lobbyType = lobbyType;
		this.gameMode = gameMode;
		this.region = region;
		this.duration = duration;
		this.dRVictory = dRVictory;
		this.timestamp = timestamp;
		this.players = players;
	}
}
