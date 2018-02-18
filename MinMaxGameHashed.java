import java.util.HashMap;

class MinMaxGameHashed extends MinMaxGame {
	protected HashMap<Integer, int[]> database = new HashMap<Integer, int[]>();

	public MinMaxGameHashed(int height, int width, boolean playersTurn) {
		super(height, width, playersTurn);
		database.put(0, null);
	}
	@Override
	protected ValueWithMove getBestMove() {
		/* Getting best move at a current state of the table.
		   RETURNS: Value of position and best move realizing that value. */

		if (database == null) {
			database = new HashMap<Integer, int[]>();
		}
		// If it was computed before, just recall 
		// the move from the database.
		int key = genKey();
		if (database.containsKey(key)) {
			int[] entry = database.get(key);
			return new ValueWithMove(entry[0], entry[1], entry[2], entry[3]);
		}
		// If we have not met this situation,
		// it needs to be computed.
		int tempAgent = activeAgent;
		int tempY = lastMoveY;
		int tempX = lastMoveX;
		
		activeAgent = 3 - tempAgent;
		if (winOnLastMove()) {
			database.put(key, new int[] {-1, movesCompleted, -1, -1});
			return new ValueWithMove(-1, movesCompleted, -1, -1);
		}
		else if (isFull()) {
			ValueWithMove bestMove = new ValueWithMove(0, movesCompleted, -1, -1);
			database.put(key, new int[] {0, movesCompleted, -1, -1});
			return new ValueWithMove(0, movesCompleted, -1, -1);
		}
		else {
			ValueWithMove move;
			ValueWithMove bestMove = new ValueWithMove(-2,0, -1, -1);
			for(int i = 0; i < table.length; i++) {
				for(int j = 0; j < table[i].length; j++) {
					if (table[i][j] == 0) {

						// Trying out move (i,j)
						lastMoveY = i;
						lastMoveX = j;
						table[i][j] = tempAgent;
						activeAgent = 3 - tempAgent;
						movesCompleted += 1;

						// Evaluating the value of the action
						move = getBestMove();
						move.flipValue();

						// Undoing tried move
						table[i][j] = 0;
						movesCompleted -= 1;

						move.bestMoveY = i;
						move.bestMoveX = j;

						if (move.compareTo(bestMove) == 1)
							bestMove = move;
					}
				}
			}
			lastMoveY = tempY;
			lastMoveX = tempX;
			activeAgent = tempAgent;
			database.put(key, 
						 new int[] {bestMove.outcome, bestMove.lengthOfGame,
								 	bestMove.bestMoveY, bestMove.bestMoveX});
			return bestMove;
		}
	}
	protected int genKey() {
		/* Generates a unique integer based on the state of the game for 
		   easier hashing. Since all entries are either 0,1,2, this is
		   essentially a ternary representation. */
		int temp = 0;
		for (int i=0; i < table.length; i++)
			for(int j=0; j < table[i].length; j++)
				temp = 3*temp + table[i][j];
		return 3*temp+activeAgent;
	}
}
