public class TeamComposition {
	private double winrates[][]; // #ofgames, #wins, index * 5 = time

	public TeamComposition() {
		this.winrates = new double[18][2];
	}

	public void record(int length, boolean win) {
		winrates[length][0]++;
		winrates[length][1] += (win ? 1 : 0);
	}
}
