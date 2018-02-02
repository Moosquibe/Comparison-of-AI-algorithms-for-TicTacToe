class MinMaxGame extends Game {
	private static int counter = 0;

	public MinMaxGame(int height, int width, boolean playersTurn) {
		super(height, width, playersTurn);
	}

	protected int computerMoves() {
		/* Computer moves. Afterwards returns
		0 : Nobody won yet
		2 : Computer won
		3 : Table is full  */

		int moveY=0;
		int moveX=0;
		int max = -10;
		int c;

		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				if (table[i][j] == 0) {
					table[i][j] = 2;
					c = MinMaxGame.getValue(table, i, j, 1, lengthToWin);
					table[i][j] = 0;
					if (-c > max) {
						max = -c;
						moveY = i;
						moveX = j;
					}
				}
			}
		}

		table[moveY][moveX] = 2;

		if (whoWon(moveY, moveX, 2) == 2)
			return 2;
		else if (isFull())
			return 3;
		else
			return 0;
	}

	private static int getValue(int[][] table, int lastMoveY, int lastMoveX, int player, int lengthToWin) {
		// Enemy makes lastMove leading to table. Returns -1 if this makes player
		// lose, returns 0 if this fills the board otherwise recursively calls the search
		// opponent = 3 - players

		if (lastMoveY >= table.length || lastMoveX >= table[0].length)
			System.out.print("Shitfuck");

		boolean lost = (Game.whoWon(table, lastMoveY, lastMoveX, 3-player, lengthToWin) == 3-player);
			
		if (lost) 
			return -1;
		else if (Game.isFull(table))
			return 0;
		else {
			int max = -10;
			int c;
			for(int i = 0; i < table.length; i++) {
				for(int j = 0; j < table[0].length; j++) {
					if (table[i][j] == 0) {
						table[i][j] = player;
						c = getValue(table, i, j, 3-player, lengthToWin);
						table[i][j] = 0;
						if (-c > max)
							max = -c;
					}
				}
			}
			return max;
		}
	}
}