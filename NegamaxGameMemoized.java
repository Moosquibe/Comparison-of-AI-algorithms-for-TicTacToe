import java.util.HashMap;
import java.util.Scanner;

class NegamaxGameMemoized extends NegamaxGame {
	protected HashMap<Integer, int[]> database;

	public NegamaxGameMemoized(int[] dimsOfBoard, int startingPlayer) {
		database = new HashMap<Integer, int[]>();
		scoreBoard = new int[] {0,0};
		board = new int[dimsOfBoard[0]][dimsOfBoard[1]];
		lastMove = new int[] {-1, -1};
		setVictoryCondition();
		restart(startingPlayer);
	}
	@Override
	protected Value valueOfMove(int k, int l, boolean keepLastMove) {
		int key = genKey(k,l);
		// If it was computed before, just recall the move from the database.
		if (database.containsKey(key)) {
			int[] entry = database.get(key);
			return new Value(entry[0], entry[1]);
		}
		// If we have not met this situation, it needs to be computed.
		Value value = super.valueOfMove(k,l, keepLastMove);
		database.put(key, new int[] {value.outcome, value.lengthOfGame});
		return value;
	}
	protected int genKey(int k, int l) {
		/* Generates a unique integer based on the state of the game. Since all 
		   entries are either 0,1,2, this integer is the ternary representation.
		   of the board in a row major order*/
		int key = 0;
		board[k][l] = activeAgent;
		for (int i=0; i < board.length; i++)
			for(int j=0; j < board[i].length; j++)
				key = 3 * key + board[i][j];
		key = 3 * key + activeAgent;
		board[k][l] = 0;
		return key;
	}
}
