abstract class Game {
	private int width, height;
	private int[][] table;
	private boolean playersTurn;

	public Game(int height, int width, boolean playersTurn) {
		this.width = width;
		this.height = height;
		this.playersTurn = playersTurn;
		table = new int[height][width];
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				table[i][j] = 0;
			}
		}
	}

	public void printTable() {

	}
}

class BruteForceGame extends Game {

	public BruteForceGame(int height, int width, boolean playersTurn) {
		super(height, width, playersTurn);
	}


}

