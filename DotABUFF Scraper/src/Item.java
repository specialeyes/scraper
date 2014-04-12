
public class Item {
	private String name;
	private int cost;
	private String description;
	private int lore;
	private int[] components; 
	
	public Item(String name, int cost, String description, int lore, int[] components) {
		this.name = name;
		this.cost = cost;
		this.description = description;
		this.lore = lore;
		this.components = components;
	}
}
