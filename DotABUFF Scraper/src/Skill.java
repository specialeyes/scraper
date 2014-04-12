import java.net.URL;

public class Skill {
	private String name;
	private String description;
	private URL videoURL; 
	
	public Skill(String name, String description, URL videoURL) {
		this.name = name;
		this.description = description;
		this.videoURL = videoURL;
	}
}
