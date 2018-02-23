import java.util.Scanner;

class MinMaxGame extends Game {
	// Implements the negamax tree search algorithm.
	public MinMaxGame(int[] dimsOfBoard, int startingPlayer) {
		super(dimsOfBoard, startingPlayer);
	}
	public class ValueWithMove extends Value {
		// Value of a position with the best move out of that position
		int bestMoveY, bestMoveX;

		public ValueWithMove(int outcome, int lengthOfGame, int bestMoveY, int bestMoveX) {
			super(outcome, lengthOfGame);
			this.bestMoveY = bestMoveY;
			this.bestMoveX = bestMoveX;
		}

		public ValueWithMove(ValueWithMove move) {
			super(move.outcome, move.lengthOfGame);
			bestMoveY = move.bestMoveY;
			bestMoveX = move.bestMoveX;
		}
	}
	@Override
	protected Value valueOfMove(int i, int j) {
		// Returns the value of the move (i,j)
		int tempAgent = activeAgent;
		int tempY = lastMoveY;
		int tempX = lastMoveX;
		
		activeAgent = 3 - tempAgent;
		if (winOnLastMove()) {
			return new ValueWithMove(-1, movesCompleted, -1, -1);
		}
		else if (isFull()) {
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
			return bestMove;
		}
	}
}
