import java.util.HashMap;

class MinMaxGameHashed extends MinMaxGame {
	protected HashMap<Integer, int[]> dataBase = new HashMap<Integer, int[]>(1000000);

	public MinMaxGameHashed(int height, int width, boolean playersTurn) {
		super(height, width, playersTurn);
		dataBase.put(0, null);
	}
	@Override
	protected ValueWithMove getBestMove() {
		// Getting best move at a current state of the table.
		// RETURNS: Value of position and best move realizing that value.

		
		// If it was computed before
		int key = genKey();
		if (dataBase == null) {
			dataBase = new HashMap<Integer, int[]>(1000000);
		}
		if (dataBase.containsKey(key)) {
			int[] entry = dataBase.get(key);
			return new ValueWithMove(entry[0], entry[1], entry[2], entry[3]);
		}

		// If it needs to be computed

		int tempAgent = activeAgent;
		int tempY = lastMoveY;
		int tempX = lastMoveX;
		
		activeAgent = 3 - tempAgent;
		if (winOnLastMove()) {
			dataBase.put(key, new int[] {-1, movesCompleted, -1, -1});
			return new ValueWithMove(-1, movesCompleted, -1, -1);
		}
		else if (isFull()) {
			ValueWithMove bestMove = new ValueWithMove(0, movesCompleted, -1, -1);
			dataBase.put(key, new int[] {0, movesCompleted, -1, -1});
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

						// Cosmetics on the move
						move.bestMoveY = i;
						move.bestMoveX = j;

						// Comparing to current best move

						if (move.compareTo(bestMove) == 1)
							bestMove = move;
					}
				}
			}
			lastMoveY = tempY;
			lastMoveX = tempX;
			activeAgent = tempAgent;
			dataBase.put(key, new int[] {bestMove.outcome, bestMove.lengthOfGame,
										bestMove.bestMoveY, bestMove.bestMoveX});
			return bestMove;
		}
	}
	protected int genKey() {
		int temp = 0;
		for (int i=0; i < table.length; i++)
			for(int j=0; j < table[i].length; j++)
				temp = 3*temp + table[i][j];
		return 3*temp+activeAgent;
	}
}