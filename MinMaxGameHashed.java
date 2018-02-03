import java.util.HashMap;

class MinMaxGameHashed extends MinMaxGame {
	private HashMap<Integer, int[]> dataBase = new HashMap<Integer, int[]>(1000000);
	private int starterPlayer;

	public MinMaxGameHashed(int height, int width, boolean playersTurn) {
		super(height, width, playersTurn);
		if (playersTurn)
			starterPlayer = 1;
		else
			starterPlayer = 2;
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

		int key = arrayToInt(table);
		int[] entry = dataBase.get(key);
		if (entry == null) {
			boolean lost = (Game.whoWon(table, lastMoveY, lastMoveX, 3-player, lengthToWin) 
																			== 3-player);
			if (lost) {
				int[] value = new int[] {-1, depth};
				dataBase.put(key, value);
				return value;
			}
			else if (Game.isFull(table)) {
				int[] value = new int[] {0, depth};
				dataBase.put(key, value);
				return value;
			}
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
				dataBase.put(key, bestValue);
				return bestValue;
			}
		}
		return entry;
	}
	private int arrayToInt(int[][] table) {
		int temp = 0;
		for (int i=0; i < table.length; i++)
			for(int j=0; j < table[i].length; j++)
				temp = 3*temp + table[i][j];
		return temp;
	}
}