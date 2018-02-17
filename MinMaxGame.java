import java.util.Scanner;

class MinMaxGame extends Game {
	// Implements the negamax tree search algorithm.

	public MinMaxGame(int height, int width, boolean playersTurn) {
		super(height, width, playersTurn);
	}
	@Override
	protected int computerMoves() {
		/* Computer moves. Afterwards returns
		0 : Nobody won yet
		2 : Computer won
		3 : Table is full  */

		assert activeAgent == 2;

		ValueWithMove move = getBestMove();
		lastMoveY=move.bestMoveY;
		lastMoveX=move.bestMoveX;
	
		table[lastMoveY][lastMoveX] = 2;
		movesCompleted += 1;
		if (winOnLastMove()) {
			scoreBoard[1] += 1;
			return 2;
		}
		else if (isFull())
			return 3;
		else {
			activeAgent = 1;
			return 0;
		}
	}
	protected ValueWithMove getBestMove() {
		// Getting best move at a current state of the table.
		// RETURNS: Value of position and best move realizing that value.
		//    
		// NOTE: opponent = 3 - player. The second coordinate is needed so that the 
		// computer player wants to win as quickly as possible or survive as long 
		// as it can.
		//
		// Return the value if the game is over after opponents move.
		// In this case (-1,-1) for best move in place of null.

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
			return bestMove;
		}
	}
}