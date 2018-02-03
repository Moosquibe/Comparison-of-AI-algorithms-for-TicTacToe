import java.util.Scanner;

class MinMaxGame extends Game {

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
		int[] bestMoveValue = new int[] {-10, 0};
		int[] moveValue = new int[2];

		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				if (table[i][j] == 0) {
					// Trying a move, getting its value for the enemy and the
					// length of the resulting game assuming optimal play.
					table[i][j] = 2;
					moveValue = getValue(table, i, j, 1, lengthToWin, 1);
					table[i][j] = 0;

					// We choose the move that gives the least value to the
					// enemy. When two moves are of equal value, we choose 
					// between them in a way such that the computer wins
					// as fast as it can or survives as long as it can given
					// optimal play.

					if ((-moveValue[0] == bestMoveValue[0] && bestMoveValue[0] == 1 && 
													moveValue[1] < bestMoveValue[1]) ||
						(-moveValue[0] == bestMoveValue[0] && bestMoveValue[0] == -1 && 
													moveValue[1] > bestMoveValue[1]) ||
						(-moveValue[0] > bestMoveValue[0])) {
						bestMoveValue[0] = -moveValue[0];
						bestMoveValue[1] = moveValue[1];
						moveY = i;
						moveX = j;
					}
				}
			}
		}
		//(new Scanner(System.in)).nextLine();
		table[moveY][moveX] = 2;
		if (whoWon(moveY, moveX, 2) == 2)
			return 2;
		else if (isFull())
			return 3;
		else
			return 0;
	}
	protected int[] getValue(int[][] table, int lastMoveY, int lastMoveX, 
								int player, int lengthToWin, int depth) {
		// Opponent makes move (lastMoveY, lastMoveX) leading to table. 
		// RETURNS: 
		//    First entry:  The value of the position for player.
		//    Second entry: depth + The lenght of the resulting game assuming optimal play.
		// 
		// NOTE: opponent = 3 - player. The second coordinate is needed so that the 
		// computer player wants to win as quickly as possible or survive as long 
		// as it can.

		boolean lost = (Game.whoWon(table, lastMoveY, lastMoveX, 3-player, lengthToWin) 
																			== 3-player);
		if (lost) 
			return new int[] {-1, depth};
		else if (Game.isFull(table))
			return new int[] {0, depth};
		else {
			int[] bestValue = new int[] {-10, depth};
			int[] value = new int[2];
			for(int i = 0; i < table.length; i++) {
				for(int j = 0; j < table[i].length; j++) {
					if (table[i][j] == 0) {
						// Trying out move (i,j)
						table[i][j] = player;
						value = getValue(table, i, j, 3-player, lengthToWin, depth + 1);
						table[i][j] = 0;
						if ((-value[0] == bestValue[0] && bestValue[0] == 1 && 
														value[1] < bestValue[1]) ||
							(-value[0] == bestValue[0] && bestValue[0] == -1 && 
														value[1] > bestValue[1]) ||
							(-value[0] > bestValue[0])) {
							
							bestValue[0] = -value[0];
							bestValue[1] = value[1];
						}
					}
				}
			}
			return bestValue;
		}
	}
}