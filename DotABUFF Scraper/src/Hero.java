
public class Hero {
	private String name;
	private String description;
	private String lore;
	private Skill[] skillSet;
	
	public Hero (String name, String description, String lore, Skill[] skillSet) {
		this.name = name;
		this.description = description;
		this.lore = lore;
		this.skillSet = skillSet;
	}
}
