class NegamaxGame extends Game {
	int[] savedLastMove;
	// Implements the negamax tree search algorithm.
	public NegamaxGame() {}
	public NegamaxGame(int[] dimsOfBoard, int startingPlayer) {
		super(dimsOfBoard, startingPlayer);
	}
	@Override
	protected Value valueOfMove(int k, int l, boolean keepLastMove) {
		/* Returns the value gained by the active agent of the move (k,l).
		   It checks whether the move ends the game and if not then recursively 
		   tries out all the possible moves. The implementation needs to make 
		   sure that the game status is the same coming in and out of the 
		   method. If the last argument is true, the current LastMove is saved
		   and restored in the end.*/
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
			value = new Value(-2,0); // Corresponds to -infty
			Value tempValue;
			for(int i = 0; i < board.length; i++) {
				for(int j = 0; j < board[i].length; j++) {
					if (board[i][j] == 0) {
						tempValue = valueOfMove(i, j, false);
						if (tempValue.compareTo(value) == 1)
							value = tempValue;
					}
				}
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
