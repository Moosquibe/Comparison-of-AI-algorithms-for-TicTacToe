import java.util.Scanner;

class AlphaBetaGame extends NegamaxGameMemoized {
	public AlphaBetaGame(int[] dimsOfBoard, int startingPlayer) {
		super(dimsOfBoard, startingPlayer);
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
		Value alpha = new Value(-2, 0);
		Value beta = new Value(2,0);
		Value value = alphaBeta(k, l, alpha, beta, keepLastMove);
		database.put(key, new int[] {value.outcome, value.lengthOfGame});
		return value;
	}
	protected Value alphaBeta(int k, int l, Value alpha, Value beta, 
							  boolean keepLastMove) { 
		/* Getting best move at a current state of the table.*/
		if (keepLastMove) 
			savedLastMove = lastMove.clone();
		// Make the move
		lastMove[0] = k;
		lastMove[1] = l;
		board[k][l] = activeAgent;
		movesCompleted += 1;
		// Evaluate the move
		Value value;
		if (winOnLastMove()) {
			value = new Value(1, movesCompleted);
		}
		else if (isFull()) {
			value = new Value(0, movesCompleted);
		}
		else {
			// Recursively evaluate move (i,j) for opponent and
			// find the best one from his/her perspective
			activeAgent = 3 - activeAgent;
			value = alpha; 
			Value tempValue;
			Value newAlpha = new Value(-beta.outcome, beta.lengthOfGame);
			Value newBeta = new Value(-value.outcome, value.lengthOfGame);
			for(int i = 0; i < board.length; i++) {
				for(int j = 0; j < board[i].length; j++) {
					if (board[i][j] == 0) {
						tempValue = alphaBeta(i, j, newAlpha, newBeta, false);
						if (tempValue.compareTo(value) == 1) 
							value = tempValue;
						// Beta pruning
						if (value.compareTo(beta) == 1)
							break;
					}
				}
				if (value.compareTo(beta) == 1)
					break;
			}
			activeAgent = 3 - activeAgent;
			// The value of the position is the negative of the best value
			// for the opponent.
			value.flipValue();
		}
		// Undo the move
		if (keepLastMove)
			lastMove = savedLastMove;
		board[k][l] = 0;
		movesCompleted -= 1;
		return value;
	}
}
