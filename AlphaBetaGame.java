import java.util.HashMap;

class AlphaBetaGame extends NegamaxGameMemoized {
	private int bestMoveY, bestMoveX;
	// public class ValueWithMove extends Value {
	// 	// Value of a position with the best move out of that position
	// 	int bestMoveY, bestMoveX;

	// 	public ValueWithMove(int outcome, int lengthOfGame, 
	// 						 int bestMoveY, int bestMoveX) {
	// 		super(outcome, lengthOfGame);
	// 		this.bestMoveY = bestMoveY;
	// 		this.bestMoveX = bestMoveX;
	// 	}

	// 	public ValueWithMove(ValueWithMove move) {
	// 		super(move.outcome, move.lengthOfGame);
	// 		bestMoveY = move.bestMoveY;
	// 		bestMoveX = move.bestMoveX;
	// 	}
	// }


	public AlphaBetaGame(int height, int width, boolean playersTurn) {
		super(height, width, playersTurn);
	}
	@Override
	protected ValueWithMove getBestMove() {
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
		ValueWithMove bestMove = alphaBeta(new Value(-1, 0), new Value(1,0));
		database.put(key, new int[] {bestMove.outcome, bestMove.lengthOfGame,
									 bestMove.bestMoveY, bestMove.bestMoveX});
		return bestMove;
	}
	protected ValueWithMove alphaBeta(Value alpha, Value beta) { 
		/* Getting best move at a current state of the table.
		   RETURNS: Value of position and best move realizing that value. */

		int tempAgent = activeAgent;
		int tempY = lastMoveY;
		int tempX = lastMoveX;

		// winOnLastMove needs the active agent to be the opponent
		// to work properly.
		activeAgent = 3 - tempAgent;
		if (winOnLastMove()) {
			return new ValueWithMove(-1, movesCompleted, -1, -1);
		}
		else if (isFull()) {
			return new ValueWithMove(0, movesCompleted, -1, -1);
		}
		else {
			Value bestValue = alpha;
			ValueWithMove move;

			int moveY = 0;
			int moveX = 0;

			for(int i = 0; i < table.length; i++) {
				for(int j = 0; j < table[i].length; j++) {
					if (table[i][j] == 0) {
						// Trying move (i,j)
						lastMoveY = i;
						lastMoveX = j;
						table[i][j] = tempAgent;
						activeAgent = 3 - tempAgent;
						movesCompleted += 1;

						// Evaluating the value of the action
						move = alphaBeta(
								new Value(-beta.outcome, beta.lengthOfGame),
								new Value(-bestValue.outcome, 
										  bestValue.lengthOfGame));
						move.flipValue();

						// Undoing tried move
						table[i][j] = 0;
						movesCompleted -= 1;

						// Comparing to current best move
						if (move.compareTo(bestValue) == 1) {
							bestValue = move;
							moveY = i;
							moveX = j;
						}
						// Beta pruning
						if (bestValue.compareTo(beta) == 1) {
							return new ValueWithMove(bestValue.outcome,
													 bestValue.lengthOfGame,
													 moveY, moveX);
						}
					}
				}
			}
			lastMoveY = tempY;
			lastMoveX = tempX;
			activeAgent = tempAgent;
			return new ValueWithMove(bestValue.outcome, bestValue.lengthOfGame,
									 moveY, moveX);
		}
	}
}
